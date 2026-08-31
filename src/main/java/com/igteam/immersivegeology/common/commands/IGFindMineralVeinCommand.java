/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.commands;

import com.igteam.immersivegeology.common.event.IGCommonForgeEvents;
import com.igteam.immersivegeology.common.event.VeinScanTask;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.*;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.TaskChainer.DelayedTask;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.server.command.EnumArgument;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Locale;

public class IGFindMineralVeinCommand
{
	private static final Logger LOGGER = IGLib.getNewLogger();
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{

		dispatcher.register(
				Commands.literal("locate")
						.requires(source -> source.hasPermission(2))
						.then(Commands.literal("mineral")
								.then(Commands.argument("type", EnumArgument.enumArgument(MineralEnum.class))
										.then(Commands.argument("radius", IntegerArgumentType.integer(0, 32))
												.executes(context -> {
													MineralEnum mineral = context.getArgument("type", MineralEnum.class);
													int radius = IntegerArgumentType.getInteger(context, "radius");
													findMineralVienAsync(context.getSource(), mineral, radius);
													return 1;
												}))))
		);

		dispatcher.register(
				Commands.literal("locate")
						.requires(source -> source.hasPermission(2))
						.then(Commands.literal("metal")
								.then(Commands.argument("type", StringArgumentType.word())
										.suggests((ctx, builder) -> {
											for (MetalEnum metal : MetalEnum.values()) {
												if (metal.hasFlag(BlockCategoryFlags.ORE_BLOCK)) {
													builder.suggest(metal.name().toLowerCase(Locale.ROOT));
												}
											}
											return builder.buildFuture();
										})
										.then(Commands.argument("radius", IntegerArgumentType.integer(0, 32))
												.executes(context -> {
													String typeName = StringArgumentType.getString(context, "type");
													MetalEnum metal = Arrays.stream(MetalEnum.values())
															.filter(m -> m.name().equalsIgnoreCase(typeName) && m.hasFlag(BlockCategoryFlags.ORE_BLOCK))
															.findFirst()
															.orElseThrow(() -> new IllegalArgumentException("Invalid native metal: " + typeName));
													int radius = IntegerArgumentType.getInteger(context, "radius");
													findMetalVienAsync(context.getSource(), metal, radius);
													return 1;
												}))))
		);
	}

	public static void findMineralVienAsync(CommandSourceStack source, MineralEnum type, int radius) {
		source.sendSuccess(() -> Component.literal("Locating Mineral Vein..."), false);
		source.getServer().submit(() -> {
			if(source.getPlayer() == null) source.sendFailure(Component.literal("Command must be run by a player"));
			IGCommonForgeEvents.activeVeinScans.add(new VeinScanTask(source, source.getLevel(), type, radius));
		});
	}

	public static void findMetalVienAsync(CommandSourceStack source, MetalEnum type, int radius) {
		source.sendSuccess(() -> Component.literal("Locating Native Metal Vein..."), false);
		source.getServer().submit(() -> {
			if(source.getPlayer() == null) source.sendFailure(Component.literal("Command must be run by a player"));
			IGCommonForgeEvents.activeVeinScans.add(new VeinScanTask(source, source.getLevel(), type, radius));
		});
	}

	public static boolean findMineralVien(CommandSourceStack source, ServerLevel level, MineralEnum type, int radius) throws CommandSyntaxException {
		// Get player position
		ServerPlayer player = source.getPlayerOrException();
		ChunkPos playerPos = player.chunkPosition();

		int minBuildHeight = level.getMinBuildHeight();
		int maxBuildHeight = level.getMaxBuildHeight();
		int sectionMin = level.getSectionIndex(minBuildHeight);
		int sectionMax = level.getSectionIndex(maxBuildHeight);
		TagKey<Block> materialTag = type.getBlockMaterialTag();
		// Loop over the chunks in the specified radius around the player
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				ChunkPos currentChunk = new ChunkPos(playerPos.x + x, playerPos.z + z);
				// Get chunk
				LevelChunk chunk = level.getChunk(currentChunk.x, currentChunk.z);
				for (int sectionIndex = sectionMin; sectionIndex < sectionMax; sectionIndex++)
				{
					LevelChunkSection section = chunk.getSection(sectionIndex);
					// Skip empty sections
					if (section.hasOnlyAir()) continue;

					// Broad check - if the section doesn't have any ores at all, skip it entirely
					if (!section.maybeHas(b -> b.is(materialTag))) continue;

					BlockPos orePosition = chunk.getPos().getWorldPosition();
					int distance = Mth.floor(Mth.sqrt((float)source.getPosition().distanceToSqr(orePosition.getX(), orePosition.getY(), orePosition.getZ())));

					Component coordinates = ComponentUtils.wrapInSquareBrackets(
							Component.translatable("chat.coordinates", orePosition.getX(), orePosition.getY(), orePosition.getZ())
					).withStyle((style) -> {
						return style.withColor(ChatFormatting.GREEN)
								.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
										"/tp @s " + orePosition.getX() + " ~ " + orePosition.getZ()))
								.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										Component.translatable("chat.coordinates.tooltip")));
					});

					source.sendSuccess(() -> Component.translatable("command.immersivegeology.veinlocate",
							type.name(), coordinates, distance), false);
					return true;
				}
			}
		}
		// If no matching feature is found, send a message to the player
		source.sendFailure(Component.literal("No " + type.name() + " ore vein found within " + radius + " chunk radius."));
		return false;
	}
}
