/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.gametest.tests;

import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.Vein;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.MapColor.Brightness;
import org.checkerframework.checker.units.qual.C;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
public class SpawnChunkCapture {
	// Constants
	private static final int CHUNK_SIZE = 16;
	private static final int MAP_SIZE_IN_CHUNKS = 128;
	private static final int IMAGE_SIZE = CHUNK_SIZE * MAP_SIZE_IN_CHUNKS;
	private static final String BASE_PATH = "../src/main/resources/assets/immersivegeology/textures/";
	private static final double NOISE_THRESHOLD_HIGH = 0.8;
	private static final double NOISE_THRESHOLD_MEDIUM = 0.6;
	private static final double NOISE_THRESHOLD = IGOreFeature.THRESHOLD;

	// Static fields
	private static MinecraftServer server;
	private static long seed;
	private static final Map<BlockPos, IGOreFeatureConfig> toRender = new HashMap<>();
	private static final Map<String, BufferedImage> ORE_PALETTES = new HashMap<>();
	private static final Map<Integer, Color> PALETTE_KEY_COLORS = new HashMap<>();
	private static BufferedImage oreItemImage;
	private static IGOreFeatureConfig mineralEntry;

	// Static initialization block
	static {
		try {
			initializePalettes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GameTest
	public static void captureSpawnChunks(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		if (level == null) {
			helper.fail("Level not found.");
			return;
		}

		server = level.getServer();
		seed = level.getSeed();

		BlockPos spawnPos = level.getSharedSpawnPos();
		try {
			BufferedImage image = generateMapImage(level, spawnPos);
			saveImage(image, helper);
		} catch (Exception e) {
			helper.fail("Error generating map: " + e.getMessage());
		}
	}

	private static BufferedImage generateMapImage(Level level, BlockPos spawnPos) {
		int spawnChunkX = (spawnPos.getX() / CHUNK_SIZE) + 32;
		int spawnChunkZ = (spawnPos.getZ() / CHUNK_SIZE);

		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		configureGraphics(g2d);

		renderChunks(level, g2d, spawnChunkX, spawnChunkZ);
		renderMineralDeposits(g2d);

		g2d.dispose();
		return image;
	}

	private static void configureGraphics(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	}

	private static void renderChunks(Level level, Graphics2D g2d, int spawnChunkX, int spawnChunkZ) {
		int size = MAP_SIZE_IN_CHUNKS / 2;
		int totalChunks = MAP_SIZE_IN_CHUNKS * MAP_SIZE_IN_CHUNKS;
		int processed = 0;

		for (int chunkX = -size; chunkX < size; chunkX++) {
			for (int chunkZ = -size; chunkZ < size; chunkZ++) {
				ChunkPos chunkPos = new ChunkPos(spawnChunkX + chunkX, spawnChunkZ + chunkZ);
				LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);

				renderChunkToImage(chunk, g2d, chunkX + size, chunkZ + size);
				processed++;

				if (processed % MAP_SIZE_IN_CHUNKS == 0) {
					double percentComplete = (processed * 100.0) / totalChunks;
					IGLib.IG_LOGGER.info("Map Generation: {}%", percentComplete);
				}
			}
		}
	}

	private static void renderChunkToImage(LevelChunk chunk, Graphics2D g2d, int chunkXOffset, int chunkZOffset) {
		final int blockSize = 1;
		ChunkPos chunkPos = chunk.getPos();
		int midPoint = CHUNK_SIZE / 2;
		boolean foundFeature;

		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int z = 0; z < CHUNK_SIZE; z++) {
				int surfaceY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
				BlockPos blockPos = new BlockPos(x, surfaceY, z);
				BlockState blockState = chunk.getBlockState(blockPos);
				Holder<Biome> biomeHolder = chunk.getNoiseBiome(x, surfaceY, z);
				Biome biome = biomeHolder.get();

				// Calculate color
				int col = blockState.is(Blocks.WATER)
						? biome.getWaterColor()
						: blockState.getBlock().defaultMapColor().calculateRGBColor(Brightness.NORMAL);
				Color blockColor = new Color(col);

				// Check for ore feature
				foundFeature = isCustomOreFeaturePresent(biomeHolder, chunkPos);

				// Calculate position in image
				int xPos = (chunkXOffset * CHUNK_SIZE) + x;
				int zPos = (chunkZOffset * CHUNK_SIZE) + z;

				// Mark central position for rendering
				if (foundFeature && x == midPoint && z == midPoint) {
					toRender.put(new BlockPos(xPos, 0, zPos), mineralEntry);
				}

				// Render block
				g2d.setColor(blockColor);
				g2d.fillRect(xPos, zPos, blockSize, blockSize);
			}
		}
	}

	private static void renderMineralDeposits(Graphics2D g2d) {
		for (Map.Entry<BlockPos, IGOreFeatureConfig> entry : toRender.entrySet()) {
			IGOreFeatureConfig feature = entry.getValue();
			if(feature==null) continue;
			if(!feature.canSpawn()) continue;
			if(!feature.canStateGenerate(Blocks.STONE.defaultBlockState())) continue;
			GeologyMaterial material = feature.entry().instance();
			BlockPos p = entry.getKey();
			if (material == null) continue;
			ChunkPos chunkPos = new ChunkPos(p);
			ChunkAccess chunk = server.overworld().getChunk(chunkPos.x, chunkPos.z);
			OreConfig rConfig = IGServerConfig.ORES.ores.get(feature.entry());
			RandomSource random = new XoroshiroRandomSource(seed ^ (long)chunkPos.x * 61728364132L, feature.seed() ^ (long)chunkPos.z * 16298364123L);
			Vein vein = IGOreFeature.createVein(random, rConfig, feature.seed());
			Pair<Integer, Double> analysis = feature.findOptimalYLevelWithMedian(vein, p, chunk.getHeight(Heightmap.Types.WORLD_SURFACE, 8, 8), rConfig.minY.get());
			int bestY = analysis.getFirst();
			int bestYSection = chunk.getSectionIndex(bestY);
			if(chunk.getSection(bestYSection).hasOnlyAir()) continue;
			if(!chunk.getSection(bestYSection).maybeHas((s) -> s.is(Blocks.STONE))) continue;
			double medianNoise = analysis.getSecond();
			//if the median noise of the entire deposit is lower than 0.3 (poor ore is 0.4) we discard it.
			if(medianNoise < 0.001) continue;

			int chunkXPos = p.getX() - CHUNK_SIZE;
			int chunkZPos = p.getZ() - CHUNK_SIZE;
			int depositWidth = CHUNK_SIZE * 3;
			int depositHeight = CHUNK_SIZE * 3;
			g2d.setColor(Color.RED);
			g2d.setStroke(new BasicStroke(1));
			g2d.drawRect(chunkXPos, chunkZPos, depositWidth, depositHeight);
			renderDepositDetails(g2d, feature, p, chunkXPos, chunkZPos, depositWidth, depositHeight, analysis, vein);

			String oreName = material.getName();
			BufferedImage materialImage = mapOreItemImageToPalette(oreName);
			if (materialImage != null) {
				g2d.drawImage(materialImage, chunkXPos-8, chunkZPos-8, null);
			}
			g2d.drawString(oreName, chunkXPos, chunkZPos-8);
		}
	}
	public static void timeVoidFunction(Runnable function, String functionName) {
		long startTime = System.nanoTime();
		function.run();
		long endTime = System.nanoTime();

		long durationNanos = endTime - startTime;

		String formattedTime;
		if (durationNanos < 1_000) {
			formattedTime = durationNanos + " ns";
		} else if (durationNanos < 1_000_000) {
			formattedTime = String.format("%.2f μs", durationNanos / 1000.0);
		} else if (durationNanos < 1_000_000_000) {
			formattedTime = String.format("%.2f ms", durationNanos / 1000000.0);
		} else {
			formattedTime = String.format("%.2f s", durationNanos / 1000000000.0);
		}

		System.out.println("Function '" + functionName + "' executed in: " + formattedTime);
	}

	private static void renderDepositDetails(Graphics2D g2d, IGOreFeatureConfig feature, BlockPos p,
											 int chunkXPos, int chunkZPos, int width, int height, Pair<Integer, Double> analysis, Vein vein) {

		// Constants for noise thresholds
		final double NOISE_THRESHOLD = 0.3;
		final double NOISE_THRESHOLD_MEDIUM = 0.6;
		final double NOISE_THRESHOLD_HIGH = 0.8;

		ChunkPos chunkPos = new ChunkPos(p);
		int bestY = analysis.getFirst();
		for (int x = 0; x < width; x++) {
			for (int z = 0; z < height; z++) {
				int xPos = chunkXPos + x;
				int zPos = chunkZPos + z;
				double noise = feature.noise(chunkPos, xPos, bestY, zPos, vein);

				if (noise > NOISE_THRESHOLD) {
					// Color based on noise intensity
					if (noise > NOISE_THRESHOLD_HIGH) {
						g2d.setColor(Color.GREEN);
					} else if (noise > NOISE_THRESHOLD_MEDIUM) {
						g2d.setColor(Color.YELLOW);
					} else {
						g2d.setColor(Color.RED);
					}
					g2d.drawRect(xPos, zPos, 1, 1); // Use relative coordinates for drawing
				}
			}
		}

		// Optionally display the Y level found
		g2d.setColor(Color.WHITE);
		g2d.drawString("Y("+bestY+")",  chunkXPos ,  chunkZPos);
	}

	private static void saveImage(BufferedImage image, GameTestHelper helper) {
		File outputFile = new File(server.getServerDirectory(), "spawn_chunks.png");
		try {
			ImageIO.write(image, "PNG", outputFile);
			helper.succeed();
		} catch (IOException e) {
			helper.fail("Error saving the image: " + e.getMessage());
		}
	}

	private static boolean isCustomOreFeaturePresent(Holder<Biome> biomeHolder, ChunkPos pos) {
		Biome biome = biomeHolder.get();
		List<HolderSet<PlacedFeature>> features = biome.getGenerationSettings().features();

		for (HolderSet<PlacedFeature> featureSet : features) {
			for (Holder<PlacedFeature> featureHolder : featureSet) {
				if (isCustomOreFeature(featureHolder.value(), pos, biomeHolder)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isCustomOreFeature(PlacedFeature placedFeature, ChunkPos pos, Holder<Biome> biome) {
		ConfiguredFeature<?, ?> feature = placedFeature.feature().get();
		if (feature.config() instanceof IGOreFeatureConfig igConfig) {
			boolean canSpawn = igConfig.canSpawn() &&
					igConfig.canSpawnAt(pos.getWorldPosition(), (p) -> biome) &&
					igConfig.canPlaceVein(pos, seed, biome);

			if (canSpawn) {
				mineralEntry = igConfig;
			}
			return canSpawn;
		}
		return false;
	}

	// Palette handling methods
	private static void initializePalettes() throws IOException {
		loadPaletteKey();
		loadOrePalettes();
		loadOreItemImage();
	}

	private static void loadPaletteKey() throws IOException {
		File paletteKeyFile = new File(BASE_PATH + "palette/palette_key.png");
		if (!paletteKeyFile.exists()) {
			throw new IOException("Palette key file not found.");
		}

		BufferedImage paletteKeyImage = ImageIO.read(paletteKeyFile);
		for (int i = 0; i < paletteKeyImage.getWidth(); i++) {
			Color color = new Color(paletteKeyImage.getRGB(i, 0));
			PALETTE_KEY_COLORS.put(i, color);
		}
	}

	private static void loadOreItemImage() throws IOException {
		File oreItemImageFile = new File(BASE_PATH + "palette/item/powder/type_1.png");
		if (!oreItemImageFile.exists()) {
			throw new IOException("Ore item image (type_1.png) not found.");
		}
		oreItemImage = ImageIO.read(oreItemImageFile);
	}

	private static void loadOrePalettes() throws IOException {
		Set<String> oreNames = Arrays.stream(MineralEnum.values())
				.map(e -> e.getName().toLowerCase())
				.collect(Collectors.toSet());

		for (String ore : oreNames) {
			File orePaletteFile = new File(BASE_PATH + "palette/palettes/" + ore + "/pristine.png");
			if (orePaletteFile.exists()) {
				BufferedImage orePaletteImage = ImageIO.read(orePaletteFile);
				ORE_PALETTES.put(ore, orePaletteImage);
			} else {
				System.err.println("Ore palette for " + ore + " not found.");
			}
		}
	}

	private static BufferedImage mapOreItemImageToPalette(String name) {
		if (oreItemImage == null) {
			return null;
		}

		BufferedImage orePalette = getOrePalette(name);
		if (orePalette == null) {
			return null;
		}

		BufferedImage modifiedImage = new BufferedImage(
				oreItemImage.getWidth(),
				oreItemImage.getHeight(),
				BufferedImage.TYPE_INT_ARGB
		);

		for (int y = 0; y < oreItemImage.getHeight() - 1; y++) {
			for (int x = 0; x < oreItemImage.getWidth() - 1; x++) {
				Color pixelColor = new Color(oreItemImage.getRGB(x, y), true);

				for (Map.Entry<Integer, Color> entry : PALETTE_KEY_COLORS.entrySet()) {
					if (isColorClose(pixelColor, entry.getValue())) {
						Color replacementColor = new Color(orePalette.getRGB(entry.getKey(), 0));
						modifiedImage.setRGB(x, y, replacementColor.getRGB());
						break;
					}
				}
			}
		}
		return modifiedImage;
	}

	private static boolean isColorClose(Color color1, Color color2) {
		int threshold = 3;
		return Math.abs(color1.getRed() - color2.getRed()) <= threshold &&
				Math.abs(color1.getGreen() - color2.getGreen()) <= threshold &&
				Math.abs(color1.getBlue() - color2.getBlue()) <= threshold;
	}

	private static BufferedImage getOrePalette(String oreName) {
		return ORE_PALETTES.getOrDefault(oreName.toLowerCase(), null);
	}
}


