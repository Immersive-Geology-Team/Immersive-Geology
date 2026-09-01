/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.event;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.excavator.MineralMix;
import blusunrize.immersiveengineering.common.util.compat.crafttweaker.managers.MineralMixManager;
import com.igteam.immersivegeology.common.commands.IGFindMineralVeinCommand;
import com.igteam.immersivegeology.common.loot.IGLootModifier;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.common.block.entity.device.IGDepositMapMarks;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import mezz.jei.library.recipes.RecipeManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class IGCommonForgeEvents
{
	@SubscribeEvent
	public void interruptLootTableLoading(LootTableLoadEvent event)
	{
		String namespace = event.getName().getNamespace();
		if(!IGLib.MODID.equals(namespace)) return;

		// Used to remove loot tables for inactive content
		String path = event.getName().getPath();
		for(ModFlags mods : ModFlags.values())
		{
			if(path.contains(mods.getName()) &! mods.isLoaded())
			{
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event)
	{
		IGLib.IG_LOGGER.info("Registered Custom Commands");
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		IGFindMineralVeinCommand.register(dispatcher);
	}


	public static final List<VeinScanTask> activeVeinScans = Collections.synchronizedList(new ArrayList<>());

	@SubscribeEvent
	public void updateVeinScans(TickEvent.LevelTickEvent event)
	{
		// LevelTickEvent fires once per phase and once per dimension; only advance a scan on its own level,
		// at the end of the tick, so a task is not stepped several times per tick.
		if(event.side.isClient()||event.phase!=TickEvent.Phase.END) return;
		if(activeVeinScans.isEmpty()) return;

		synchronized(activeVeinScans)
		{
			activeVeinScans.removeIf(task -> task.level==event.level&&task.tick());
		}
	}

	/**
	 * Maps whose deposit labels have already been put back this session, so the pass below runs once per map
	 * rather than once per tick. Cleared when a server starts, since map ids only mean anything within one save.
	 */
	private static final Set<Integer> relabelledMaps = new HashSet<>();

	@SubscribeEvent
	public void clearMapLabelCache(ServerStartingEvent event)
	{
		relabelledMaps.clear();
	}

	/**
	 * Restores the labels on deposit maps.
	 * <p>
	 * The markers themselves come back on their own - vanilla replays an item's {@code Decorations} list into the
	 * saved data every tick a player carries it - but it replays them unnamed. This puts the material and grade
	 * back, once per map, by adding the decoration again under the same id.
	 */
	@SubscribeEvent
	public void restoreDepositMapLabels(TickEvent.PlayerTickEvent event)
	{
		if(event.side.isClient()||event.phase!=TickEvent.Phase.END) return;
		// The markers are already correct without this; the labels can wait a second for a cheaper sweep.
		if(event.player.tickCount%20!=0) return;

		Inventory inventory = event.player.getInventory();
		for(int slot = 0; slot < inventory.getContainerSize(); slot++)
		{
			relabelDepositMap(inventory.getItem(slot), event.player.level());
		}
	}

	private static void relabelDepositMap(ItemStack stack, Level level)
	{
		if(stack.isEmpty()||!(stack.getItem() instanceof MapItem)) return;
		// Checked off the item first so an ordinary map never reaches the level's saved data over this.
		if(!IGDepositMapMarks.hasMarks(stack)) return;

		Integer mapId = MapItem.getMapId(stack);
		if(mapId==null||relabelledMaps.contains(mapId)) return;

		MapItemSavedData data = MapItem.getSavedData(mapId, level);
		if(data==null) return;
		if(IGDepositMapMarks.restore(stack, data, level)) relabelledMaps.add(mapId);
	}

	private static void checkAndRenderMap(ItemStack stack, Level level, Player player)
	{
		if(level == null) return;
		if(level instanceof ServerLevel serverLevel)
		{
			if(stack.getItem() instanceof MapItem)
			{
				Integer mapId = MapItem.getMapId(stack);
				MapItemSavedData data = MapItem.getSavedData(mapId, level);
				if(data!=null)
				{
					int scale_mult = 1 + Byte.toUnsignedInt(data.scale);
					int scale = (8 * scale_mult / 2);
					for(int offsetX = -scale; offsetX < scale; offsetX++)
					{
						for(int offsetZ = -scale; offsetZ < scale; offsetZ++)
						{
							byte br = 0;
							ChunkPos chunkPos = new ChunkPos(data.centerX + (offsetX * 16), data.centerZ + (offsetZ * 16));
							int z = chunkPos.z;
							int x = chunkPos.x;
							Holder<Biome> biomeHolder = serverLevel.getBiome(chunkPos.getWorldPosition());
							if(isCustomOreFeaturePresent(biomeHolder, chunkPos, serverLevel.getSeed()))
							{
								byte bx = (byte)x;
								byte bz = (byte)z;
								AtomicBoolean hasInstance = new AtomicBoolean(false);
								ArrayList<MapDecoration> decorations = new ArrayList<>();
								data.getDecorations().forEach(d ->
								{
									if(d.getX()==bx&&d.getY()==bz&&d.getRot()==br&&d.getType()==Type.RED_X)
									{
										hasInstance.set(true);
									}
									if(!d.getType().equals(Type.PLAYER)) decorations.add(d);
								});

								if(!hasInstance.get())
								{
									decorations.add(new MapDecoration(Type.RED_X, bx, bz, br, Component.empty()));
									data.addClientSideDecorations(decorations);
									level.setMapData(MapItem.makeKey(mapId), data);
									data.tickCarriedBy(player, stack);
								}
							}
						}
					}
				}
			}
		}
	}

	private static boolean isCustomOreFeaturePresent(Holder<Biome> biomeHolder, ChunkPos pos, long seed) {
		Biome biome = biomeHolder.get();
		List<HolderSet<PlacedFeature>> features = biome.getGenerationSettings().features();

		for (HolderSet<PlacedFeature> featureSet : features) {
			for (Holder<PlacedFeature> featureHolder : featureSet) {
				if (isCustomOreFeature(featureHolder.value(), pos, biomeHolder, seed)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isCustomOreFeature(PlacedFeature placedFeature, ChunkPos pos, Holder<Biome> biome, long seed) {
		ConfiguredFeature<?, ?> feature = placedFeature.feature().get();
		return feature.config() instanceof IGOreFeatureConfig;
	}
}
