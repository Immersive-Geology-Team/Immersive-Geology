/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.event;

import com.igteam.immersivegeology.common.commands.IGFindMineralVeinCommand;
import com.igteam.immersivegeology.common.loot.IGLootModifier;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;

import java.util.*;
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


	public static final List<VeinScanTask> activeVeinScans  = Collections.synchronizedList(new ArrayList<>());

	@SubscribeEvent
	public void updateMapData(TickEvent.LevelTickEvent event)
	{
		if (event.side.isClient()) return;

		List<VeinScanTask> toRemove = new ArrayList<>();

		for (VeinScanTask task : activeVeinScans) {
			if (task.isComplete()) {
				task.source.sendFailure(Component.literal("No " + task.type.getTranslationName() + " ore vein found within " + task.radius + " chunk radius."));
				toRemove.add(task);
				continue;
			}

			ChunkPos currentChunkPos = task.nextChunk();
			LevelChunk chunk = task.level.getChunk(currentChunkPos.x, currentChunkPos.z);

			int sectionMin = task.level.getSectionIndex(task.level.getMinBuildHeight());
			int sectionMax = task.level.getSectionIndex(task.level.getMaxBuildHeight());
			TagKey<Block> materialTag = task.type.getBlockMaterialTag();
			Component progressMessage = Component.literal("Scanning chunk: [" + currentChunkPos.x + ", " + currentChunkPos.z + "]")
					.withStyle(ChatFormatting.YELLOW);

			task.source.getPlayer().displayClientMessage(
					progressMessage,
					true
			);

			for (int sectionIndex = sectionMin; sectionIndex < sectionMax; sectionIndex++) {
				LevelChunkSection section = chunk.getSection(sectionIndex);
				if (section.hasOnlyAir()) continue;
				if (!section.maybeHas(b -> b.is(materialTag))) continue;

				BlockPos orePosition = chunk.getPos().getWorldPosition();
				int distance = Mth.floor(Mth.sqrt((float) task.source.getPosition().distanceToSqr(orePosition.getX(), orePosition.getY(), orePosition.getZ())));

				Component coordinates = ComponentUtils.wrapInSquareBrackets(
						Component.translatable("chat.coordinates", orePosition.getX(), orePosition.getY(), orePosition.getZ())
				).withStyle(style -> style.withColor(ChatFormatting.GREEN)
						.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + orePosition.getX() + " ~ " + orePosition.getZ()))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
				);

				task.source.sendSuccess(() -> Component.translatable("command.immersivegeology.veinlocate", task.type.getTranslationName(), coordinates, distance), false);

				toRemove.add(task);
				break;
			}
		}

		// Remove after iteration
		activeVeinScans.removeAll(toRemove);
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
