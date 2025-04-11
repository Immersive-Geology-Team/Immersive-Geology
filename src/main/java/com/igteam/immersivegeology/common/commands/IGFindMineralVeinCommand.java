/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.commands;

import com.igteam.immersivegeology.common.world.IGDefaultPlacement;
import com.igteam.immersivegeology.common.world.features.IGOreFeature;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.server.command.EnumArgument;
import org.slf4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IGFindMineralVeinCommand
{
	private static final Logger LOGGER = IGLib.getNewLogger();
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{

		dispatcher.register(
				Commands.literal("locateMineralVein")
						.requires(source -> source.hasPermission(2)) // OPs only
						.then(Commands.argument("type", EnumArgument.enumArgument(MineralEnum.class))
								.then(Commands.argument("radius", IntegerArgumentType.integer(0, 16))
										.executes(context -> {
											MineralEnum mineral = context.getArgument("type", MineralEnum.class);
											int radius = IntegerArgumentType.getInteger(context, "radius");
											findMineralVienAsync(context.getSource(), mineral, radius);
											return 1;
										})))
		);
	}

	public static void findMineralVienAsync(CommandSourceStack source, MineralEnum type, int radius) {
		source.sendSuccess(() -> Component.literal("Please note that this command is WIP and is NOT 100% accurate; known issues include inability to find Hematite or Magnetite"), false);
		source.getServer().submit(() -> {
			try {
				findMineralVien(source, type, radius);
			} catch (CommandSyntaxException e) {
				source.sendFailure(Component.literal("An error occurred while searching for the mineral veins."));
			}
		});
	}

	public static void findMineralVien(CommandSourceStack source, MineralEnum type, int radius) throws CommandSyntaxException {
		// Get player position
		ServerPlayer player = source.getPlayerOrException();
		ChunkPos playerPos = player.chunkPosition();

		// Get the current level (world)
		ServerLevel level = player.serverLevel();

		// Loop over the chunks in the specified radius around the player
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				ChunkPos currentChunk = new ChunkPos(playerPos.x + x, playerPos.z + z);
				// Get chunk
				LevelChunk chunk = level.getChunk(currentChunk.x, currentChunk.z);
				Holder<Biome> biomeHolder = chunk.getNoiseBiome(8, 64, 8);

				//source.sendSuccess(() -> Component.literal("Checking Chunk at " +currentChunk.getWorldPosition().toShortString()), false);

				if (isCustomOreFeaturePresent(biomeHolder, type, chunk.getPos(), level)) {
					source.sendSuccess(() -> Component.literal("Found " + type.name() + " ore vein at " + chunk.getPos().getWorldPosition().toShortString()), false);
					return;
				}
			}
		}

		// If no matching feature is found, send a message to the player
		source.sendFailure(Component.literal("No " + type.name() + " ore vein found within " + radius + " chunk radius."));
	}

	private static boolean isCustomOreFeaturePresent(Holder<Biome> biomeHolder, MineralEnum type, ChunkPos pos, ServerLevel server) {
		Biome biome = biomeHolder.get();
		List<HolderSet<PlacedFeature>> features = biome.getGenerationSettings().features();


		// Check each feature set in the biome
		for (HolderSet<PlacedFeature> featureSet : features) {
			// Iterate over all placed features in this set
			for (Holder<PlacedFeature> featureHolder : featureSet) {
				ConfiguredFeature<?, ?> feature = featureHolder.value().feature().get();

				// Check if the feature config is an instance of IGOreFeatureConfig
				if (feature.config() instanceof IGOreFeatureConfig igConfig) {
					IGDefaultPlacement placement = new IGDefaultPlacement(igConfig.entry());

					// Perform the actual check for ore feature placement
					if (placement.exposedPlace(server.getSeed(), server, pos, null)) {

						if(type.seed()==igConfig.seed())
						{
							// Check if the feature matches the desired mineral type
							return true;
						}
					}
				}
			}
		}
		return false; // No matching feature found
	}
}
