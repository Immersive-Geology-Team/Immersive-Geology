/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.gametest.tests;

import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.Vein;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.MapColor.Brightness;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Debug utility for generating visual maps of world generation and ore distribution.
 * This class captures spawn chunks and visualizes ore deposits for debugging purposes.
 */
public class SpawnChunkCapture {
	// Constants
	private static final int CHUNK_SIZE = 16;
	private static final int MAP_SIZE_IN_CHUNKS = 32;
	private static final int IMAGE_SIZE = CHUNK_SIZE * MAP_SIZE_IN_CHUNKS;
	private static final String BASE_PATH = "../src/main/resources/assets/immersivegeology/textures/";
	private static final double NOISE_THRESHOLD = IGOreFeature.THRESHOLD;

	// Performance tracking
	private static final List<Long> functionTimes = new ArrayList<>();

	// Map state
	private static MinecraftServer server;
	private static long seed;
	private static final Map<ChunkPos, IGOreFeatureConfig> toRender = new HashMap<>();

	// Image resources
	private static final Map<String, BufferedImage> ORE_PALETTES = new HashMap<>();
	private static final Map<Integer, Color> PALETTE_KEY_COLORS = new HashMap<>();
	private static BufferedImage oreItemImage;

	/**
	 * Static initialization block to load image resources
	 */
	static {
		try {
			initializePalettes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main GameTest entry point for capturing spawn chunks
	 */
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
			saveImage(image, helper, server.getServerDirectory(), "World Map");
			helper.succeed();
		} catch (Exception e) {
			helper.fail("Error generating map: " + e.getMessage());
		}
	}

	/**
	 * Analyzes the probability distribution of mineral spawns
	 */
	@GameTest
	public static void analyzeMineralDistribution(GameTestHelper helper) {
		try {
			checkMineralDistributionProps(helper);
			helper.succeed();
		} catch (Exception e) {
			helper.fail("Error analyzing mineral distribution: " + e.getMessage());
		}
	}

	// ===============================
	// Map Generation Methods
	// ===============================

	/**
	 * Generates the main world map image centered on spawn position
	 */
	private static BufferedImage generateMapImage(Level level, BlockPos spawnPos) {
		int spawnChunkX = (spawnPos.getX() / CHUNK_SIZE);
		int spawnChunkZ = (spawnPos.getZ() / CHUNK_SIZE);

		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		configureGraphics(g2d);

		renderChunks(level, g2d, spawnChunkX, spawnChunkZ);
		renderMineralDeposits(g2d);

		g2d.dispose();
		return image;
	}

	/**
	 * Configures graphics settings for rendering
	 */
	private static void configureGraphics(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	}

	/**
	 * Renders all chunks in the specified area
	 */
	private static void renderChunks(Level level, Graphics2D g2d, int spawnChunkX, int spawnChunkZ) {
		int size = MAP_SIZE_IN_CHUNKS / 2;
		int totalChunks = MAP_SIZE_IN_CHUNKS * MAP_SIZE_IN_CHUNKS;
		int processed = 0;

		for (int chunkX = -size; chunkX < size; chunkX++) {
			for (int chunkZ = -size; chunkZ < size; chunkZ++) {
				ChunkPos chunkPos = new ChunkPos(spawnChunkX + chunkX, spawnChunkZ + chunkZ);
				LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);

				renderChunkToImage(chunk, g2d, chunkX + size, chunkZ + size);

				// Check for custom ore features
				Holder<Biome> biomeHolder = chunk.getNoiseBiome(8, 64, 8);
				isCustomOreFeaturePresent(biomeHolder, chunkPos);

				processed++;
				if (processed % MAP_SIZE_IN_CHUNKS == 0) {
					double percentComplete = (processed * 100.0) / totalChunks;
					IGLib.IG_LOGGER.info("Map Generation: {}%", percentComplete);
				}
			}
		}
	}

	/**
	 * Renders a single chunk to the image
	 */
	private static void renderChunkToImage(LevelChunk chunk, Graphics2D g2d, int chunkXOffset, int chunkZOffset) {
		final int blockSize = 1;
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
						: blockState.getBlock().defaultMapColor().calculateRGBColor(Brightness.HIGH);
				Color blockColor = new Color(col);

				// Draw block
				int xPos = (chunkXOffset * CHUNK_SIZE) + x;
				int zPos = (chunkZOffset * CHUNK_SIZE) + z;
				g2d.setColor(blockColor);
				g2d.fillRect(xPos, zPos, blockSize, blockSize);
			}
		}
	}

	/**
	 * Renders all mineral deposits on the map
	 */
	private static void renderMineralDeposits(Graphics2D g2d) {
		ServerLevel level = server.overworld();
		for (Map.Entry<ChunkPos, IGOreFeatureConfig> entry : toRender.entrySet())
		{
			IGOreFeatureConfig feature = entry.getValue();
			if(feature==null) continue;

			GeologyMaterial material = feature.entry().instance();
			if(material==null) continue;

			ChunkPos p = entry.getKey();
			int depositWidth = CHUNK_SIZE;
			int depositHeight = CHUNK_SIZE;
			BlockPos centerPos = p.getMiddleBlockPosition(0);
			boolean render = renderDepositDetails(level, g2d, feature, centerPos, depositWidth, depositHeight);

			if(render)
			{
				int mapX = (centerPos.getX() + (MAP_SIZE_IN_CHUNKS * CHUNK_SIZE) / 2) % (MAP_SIZE_IN_CHUNKS * CHUNK_SIZE);
				int mapZ = (centerPos.getZ() + (MAP_SIZE_IN_CHUNKS * CHUNK_SIZE) / 2) % (MAP_SIZE_IN_CHUNKS * CHUNK_SIZE);
				g2d.setColor(Color.red);
				g2d.setStroke(new BasicStroke(1));
				g2d.drawRect(mapX,mapZ,depositWidth,depositHeight);
				g2d.setColor(Color.white);
				g2d.drawString(material.getName(), mapX, mapZ);
			}
		}

		// Log median performance time
		printFormattedTime(calculateMedian(functionTimes));
	}

	/**
	 * Renders detailed information about a specific deposit
	 */
	private static boolean renderDepositDetails(ServerLevel level, Graphics2D g2d, IGOreFeatureConfig feature,
												BlockPos p, int width, int height) {
		boolean oreFound = false;
		int worldX = p.getX();
		int worldZ = p.getZ();

		// Convert world coordinates to map coordinates
		int mapX = (worldX + (MAP_SIZE_IN_CHUNKS * CHUNK_SIZE) / 2) % (MAP_SIZE_IN_CHUNKS * CHUNK_SIZE);
		int mapZ = (worldZ + (MAP_SIZE_IN_CHUNKS * CHUNK_SIZE) / 2) % (MAP_SIZE_IN_CHUNKS * CHUNK_SIZE);

		// Scan for ore blocks
		for (int x = 0; x < width; x++) {
			for (int z = 0; z < height; z++) {
				for (int y = -64; y < 192; y++) {
					int xPos = worldX + x - width/2;
					int zPos = worldZ + z - height/2;

					BlockState blockState = level.getBlockState(new BlockPos(xPos, y, zPos));
					if (blockState.isAir()) continue;

					if (blockState.getBlock() instanceof IGOreBlock block) {
						OreRichness grade = block.getOreRichness();

						// Color based on ore richness
						switch (grade) {
							case POOR -> g2d.setColor(Color.RED);
							case NORMAL -> g2d.setColor(Color.YELLOW);
							case RICH -> g2d.setColor(Color.GREEN);
						}

						// Draw the ore location
						int drawX = mapX + x - width/2;
						int drawZ = mapZ + z - height/2;

						// Ensure we're drawing within bounds
						if (drawX >= 0 && drawX < IMAGE_SIZE && drawZ >= 0 && drawZ < IMAGE_SIZE) {
							g2d.drawRect(drawX, drawZ, 1, 1);
							oreFound = true;
						}
						break;
					}
				}
			}
		}

		return oreFound;
	}

	// ===============================
	// Feature Detection Methods
	// ===============================

	private static IGOreFeatureConfig mineralEntry;

	/**
	 * Checks if a custom ore feature is present in the given biome at the position
	 */
	private static void isCustomOreFeaturePresent(Holder<Biome> biomeHolder, ChunkPos pos) {
		Biome biome = biomeHolder.get();
		List<HolderSet<PlacedFeature>> features = biome.getGenerationSettings().features();

		for (HolderSet<PlacedFeature> featureSet : features) {
			for (Holder<PlacedFeature> featureHolder : featureSet) {
				ConfiguredFeature<?, ?> feature = featureHolder.value().feature().get();
				if (feature.config() instanceof IGOreFeatureConfig igConfig) {
					toRender.put(pos, igConfig);
				}
			}
		}
	}

	// ===============================
	// Noise Map Generation Methods
	// ===============================

	/**
	 * Generates a noise distribution image for the given ore config
	 */
	private static BufferedImage pregenNoiseDistribution(IGOreFeatureConfig config) {
		BufferedImage image = new BufferedImage(MAP_SIZE_IN_CHUNKS, MAP_SIZE_IN_CHUNKS, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		configureGraphics(g2d);
		renderNoiseDistribution(g2d, config);
		g2d.dispose();
		return image;
	}

	/**
	 * Renders noise distribution for ore veins
	 */
	private static void renderNoiseDistribution(Graphics2D g2d, IGOreFeatureConfig config) {
		int size = MAP_SIZE_IN_CHUNKS / 2;
		OreConfig rConfig = IGServerConfig.ORES.ores.get(config.entry());
		int y = config.entry().getMinY() + 8;

		if (rConfig == null) return;

		for (int chunkX = -size; chunkX < size; chunkX++) {
			for (int chunkZ = -size; chunkZ < size; chunkZ++) {
				ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

				// Create deterministic random based on chunk position and config seed
				RandomSource random = new XoroshiroRandomSource(
						seed ^ (long)chunkPos.x * 61728364132L,
						config.seed() ^ (long)chunkPos.z * 16298364123L
				);

				// Create and evaluate vein
				Vein vein = IGOreFeature.createVein(random, rConfig, config.seed());
				boolean hasSpawn = scanChunkForOre(chunkPos, y, vein);

				// Verify vein is worthwhile
				if (hasSpawn) {
					BlockPos centerPos = chunkPos.getMiddleBlockPosition(0);
					int bestY = config.findOptimalYLevel(
							vein,
							centerPos,
							rConfig.maxY.get(),
							rConfig.minY.get()
					);
					hasSpawn = config.isVeinWorthwhile(chunkPos, bestY, vein);
				}

				// Draw result
				g2d.setColor(hasSpawn ? Color.WHITE : Color.BLACK);
				g2d.drawRect(chunkX + size, chunkZ + size, 1, 1);
			}
		}
	}

	/**
	 * Scans a chunk to check if it contains ore above the noise threshold
	 */
	private static boolean scanChunkForOre(ChunkPos chunkPos, int y, Vein vein) {
		INoise3D noiseGen = vein.getNoise();

		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int z = 0; z < CHUNK_SIZE; z++) {
				int worldX = (chunkPos.x * CHUNK_SIZE) + x;
				int worldZ = (chunkPos.z * CHUNK_SIZE) + z;
				double noise = noiseGen.noise(worldX, y, worldZ);

				if (noise > NOISE_THRESHOLD) {
					return true;
				}
			}
		}
		return false;
	}

	// ===============================
	// Material Analysis Methods
	// ===============================

	public static HashMap<MaterialInterface<?>, Double> probability_map = new HashMap<>();

	/**
	 * Analyzes all materials for spawn probability
	 */
	private static void checkMineralDistributionProps(GameTestHelper helper) throws IOException {
		File dir = Path.of(server.getServerDirectory().getPath() + "/noise_maps/").toFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		int processed = 0;
		int total_materials = IGLib.getGeneratedMaterials().size();

		for (MaterialInterface<?> material : IGLib.getGeneratedMaterials()) {
			IWorldGenConfig data = material.getConfig();
			if (data == null || data.getVeinSize() == 0) {
				continue;
			}

			OreConfig rConfig = IGServerConfig.ORES.ores.get(data);
			if (rConfig == null) {
				continue;
			}

			String name = data.name().toLowerCase() + "chunk_noise";
			File outputFile = new File(dir, name + ".png");

			// Generate noise map if it doesn't exist
			if (!outputFile.exists()) {
				IGOreFeatureConfig oreConfig = new IGOreFeatureConfig(
						data,
						IGOreFeatureConfig.hash(data.name()),
						data.getMinSpawnTemp(),
						data.getMaxSpawnTemp(),
						data.getMinDownfall(),
						data.getMaxDownfall()
				);

				BufferedImage noiseDistribution = pregenNoiseDistribution(oreConfig);
				saveImage(noiseDistribution, helper, dir, name);
			}

			// Analyze noise map for spawn probability
			analyzeNoiseMapProbability(dir, name, material, data);

			// Log progress
			processed++;
			float progress = ((float) processed / total_materials) * 100;
			IGLib.IG_LOGGER.info("Processing {}%", progress);
		}

		// Log all probabilities
		logProbabilities();
	}

	/**
	 * Analyzes a noise map to calculate spawn probability
	 */
	private static void analyzeNoiseMapProbability(File dir, String name, MaterialInterface<?> material, IWorldGenConfig data)
			throws IOException {
		File cachedFile = new File(dir, name + ".png");
		BufferedImage cached = ImageIO.read(cachedFile);

		// Count black pixels (no spawn)
		int width = cached.getWidth();
		int height = cached.getHeight();
		int blackPixels = 0;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color pixelColor = new Color(cached.getRGB(x, y));
				if (pixelColor.equals(Color.BLACK)) {
					blackPixels++;
				}
			}
		}

		// Calculate probability
		int totalPixels = width * height;
		float noiseProbability = 1 - ((float)blackPixels / totalPixels);
		IGLib.IG_LOGGER.info("{} Noise Probability: {}", data.name(), noiseProbability);

		// Calculate final probability factoring in chunk generation chance
		double chunkProbability = (0.3333 * ((double)data.generationChance() / 2_000_000));
		double finalProb = noiseProbability * chunkProbability * (MAP_SIZE_IN_CHUNKS * MAP_SIZE_IN_CHUNKS);
		probability_map.put(material, (finalProb * 100));
	}

	/**
	 * Logs the probability map for all materials
	 */
	private static void logProbabilities() {
		for (Entry<MaterialInterface<?>, Double> entry : probability_map.entrySet()) {
			String formattedProb = new DecimalFormat("###.##").format(entry.getValue()) + "%";
			IGLib.IG_LOGGER.info(
					"Spawn Chance in a 64x64 chunk area for {} is {}",
					entry.getKey().getName(),
					formattedProb
			);
		}
	}

	// ===============================
	// Utility Methods
	// ===============================

	/**
	 * Initializes palette resources
	 */
	private static void initializePalettes() throws IOException {
		loadPaletteKey();
		loadOrePalettes();
		loadOreItemImage();
	}

	/**
	 * Loads the palette key image
	 */
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

	/**
	 * Loads ore palettes for all materials
	 */
	private static void loadOrePalettes() throws IOException {
		List<MaterialInterface<?>> toGenerate = IGLib.getGeneratedMaterials();
		Set<String> oreNames = toGenerate.stream()
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

	/**
	 * Loads the ore item image
	 */
	private static void loadOreItemImage() throws IOException {
		File oreItemImageFile = new File(BASE_PATH + "palette/item/powder/type_1.png");
		if (!oreItemImageFile.exists()) {
			throw new IOException("Ore item image (type_1.png) not found.");
		}
		oreItemImage = ImageIO.read(oreItemImageFile);
	}

	/**
	 * Maps ore item image to the appropriate palette
	 */
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

		for (int y = 0; y < oreItemImage.getHeight(); y++) {
			for (int x = 0; x < oreItemImage.getWidth(); x++) {
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

	/**
	 * Gets ore palette for the specified ore name
	 */
	private static BufferedImage getOrePalette(String oreName) {
		return ORE_PALETTES.getOrDefault(oreName.toLowerCase(), null);
	}

	/**
	 * Checks if two colors are similar
	 */
	private static boolean isColorClose(Color color1, Color color2) {
		int threshold = 3;
		return Math.abs(color1.getRed() - color2.getRed()) <= threshold &&
				Math.abs(color1.getGreen() - color2.getGreen()) <= threshold &&
				Math.abs(color1.getBlue() - color2.getBlue()) <= threshold;
	}

	/**
	 * Saves an image to disk
	 */
	private static void saveImage(BufferedImage image, GameTestHelper helper, File dir, String name) {
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File outputFile = new File(dir, name + ".png");
		try {
			ImageIO.write(image, "PNG", outputFile);
		} catch (IOException e) {
			helper.fail("Error saving the image: " + e.getMessage());
		}
	}

	/**
	 * Calculates the median time from the function times list
	 */
	private static long calculateMedian(List<Long> values) {
		if (values == null || values.isEmpty()) {
			return 0;
		}

		List<Long> sortedValues = new ArrayList<>(values);
		Collections.sort(sortedValues);

		int size = sortedValues.size();
		if (size % 2 == 0) {
			return (long)((sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2.0);
		} else {
			return sortedValues.get(size / 2);
		}
	}

	/**
	 * Measures execution time of a function
	 */
	public static <T> T timeFunction(Supplier<T> function, String functionName) {
		long startTime = System.nanoTime();
		T result = function.get();
		long endTime = System.nanoTime();

		long durationNanos = endTime - startTime;
		functionTimes.add(durationNanos);
		return result;
	}

	/**
	 * Prints a formatted time value
	 */
	private static void printFormattedTime(Long nanos) {
		String formattedTime;
		if (nanos < 1_000) {
			formattedTime = nanos + " ns";
		} else if (nanos < 1_000_000) {
			formattedTime = String.format("%.2f μs", nanos / 1000.0);
		} else if (nanos < 1_000_000_000) {
			formattedTime = String.format("%.2f ms", nanos / 1000000.0);
		} else {
			formattedTime = String.format("%.2f s", nanos / 1000000000.0);
		}

		IGLib.IG_LOGGER.info("Function Time: {}", formattedTime);
	}
}