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
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.Vein;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
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
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
public class SpawnChunkCapture {
	// Constants
	private static final int CHUNK_SIZE = 16;
	private static final int MAP_SIZE_IN_CHUNKS = 64;
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
			checkMineralDistributionProps(helper);

			BufferedImage image = generateMapImage(level, spawnPos);
			saveImage(image, helper, server.getServerDirectory(), "World Map");
			helper.succeed();
		} catch (Exception e) {
			helper.fail("Error generating map: " + e.getMessage());
		}
	}
	public static HashMap<MaterialInterface<?>, Double> probability_map = new HashMap<>();
	private static void checkMineralDistributionProps(GameTestHelper helper) throws IOException
	{
		File dir = Path.of(server.getServerDirectory().getPath()+"/noise_maps/").toFile();
		int processed = 0;
		int total_materials = IGLib.getGeneratedMaterials().size();
		for(MaterialInterface<?> material : IGLib.getGeneratedMaterials())
		{
			IWorldGenConfig data = material.getConfig();
			if(data == null)
			{
				continue;
			}
			OreConfig rConfig = IGServerConfig.ORES.ores.get(data);
			if(rConfig == null)
			{
				continue;
			}
			if(data.getVeinSize() == 0) continue;
			String name = data.name().toLowerCase()+"chunk_noise";
			File outputFile = new File(dir, name+".png");
			if(!outputFile.exists())
			{
				BufferedImage noiseDistribution = pregenNoiseDistribution(new IGOreFeatureConfig(data, IGOreFeatureConfig.hash(data.name()), data.getMinSpawnTemp(), data.getMaxSpawnTemp(), data.getMinDownfall(), data.getMaxDownfall()));
				saveImage(noiseDistribution, helper, dir, name);
			}
			File cachedFile = new File(dir, name+".png");
			BufferedImage cached = ImageIO.read(cachedFile);
			int width = cached.getWidth();
			int height = cached.getHeight();
			int black = 0;
			for(int x = 0; x < width; x++)
			{
				for(int y = 0; y < height; y++)
				{
					Color packed_color = new Color(cached.getRGB(x, y));
					if(packed_color.equals(Color.BLACK)) black++;
				}
			}
			int total = (width*height);
			float noise_probability = 1-((float)black/total);
			double chunk_probability = (0.3333*((double)data.generationChance()/2_000_000));
			double finalProb = noise_probability*chunk_probability*(MAP_SIZE_IN_CHUNKS * MAP_SIZE_IN_CHUNKS);
			probability_map.put(material, (finalProb * 100));
			float process = ((float) processed / total_materials) * 100;
			IGLib.IG_LOGGER.info("Processing {}%", process);
			processed++;
		}
		probability_map.forEach((m,p) -> {
			IGLib.IG_LOGGER.info("Spawn Chance in a 64x64 chunk area for {} is {}", m.getName(), new DecimalFormat("###.##").format(p) + "%");
		});
	}

	private static BufferedImage generateMapImage(Level level, BlockPos spawnPos) {
		int spawnChunkX = (spawnPos.getX() / CHUNK_SIZE);
		int spawnChunkZ = (spawnPos.getZ() / CHUNK_SIZE) - 64;

		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		configureGraphics(g2d);

		renderChunks(level, g2d, spawnChunkX, spawnChunkZ);
		renderMineralDeposits(g2d);

		g2d.dispose();
		return image;
	}

	private static BufferedImage pregenNoiseDistribution(IGOreFeatureConfig config) {
		BufferedImage image = new BufferedImage(MAP_SIZE_IN_CHUNKS, MAP_SIZE_IN_CHUNKS, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		configureGraphics(g2d);
		renderNoiseDistribution(g2d, config);
		g2d.dispose();
		return image;
	}

	private static void renderNoiseDistribution(Graphics2D g2d, IGOreFeatureConfig config) {
		int size = MAP_SIZE_IN_CHUNKS;
		OreConfig rConfig = IGServerConfig.ORES.ores.get(config.entry());
		int y = config.entry().getMinY() + 8;
		if(rConfig == null) return;
		for (int chunkX = -size; chunkX < size; chunkX++) {
			for (int chunkZ = -size; chunkZ < size; chunkZ++) {
				RandomSource random = new XoroshiroRandomSource(seed ^ (long)chunkX * 61728364132L, config.seed() ^ (long)chunkZ * 16298364123L);
				Vein vein = IGOreFeature.createVein(random, rConfig, config.seed());
				INoise3D noiseGen = vein.getNoise();
				boolean hasSpawn = false;
				for (int x = 0; x < CHUNK_SIZE; x++)
				{
					for(int z = 0; z < CHUNK_SIZE; z++)
					{
						int xPos = (chunkX * CHUNK_SIZE) + x;
						int zPos = (chunkZ * CHUNK_SIZE) + z;
						double noise = noiseGen.noise(xPos, y, zPos);

						if (noise > NOISE_THRESHOLD) {
							hasSpawn = true;
							break;
						}
					}
					if(hasSpawn) break;
				}
				if(hasSpawn)
				{
					ChunkPos pos = new ChunkPos(chunkX, chunkZ);
					int bestY = config.findOptimalYLevel(vein, pos.getMiddleBlockPosition(0), rConfig.maxY.get(), rConfig.minY.get());
					boolean goodVein = config.isVeinWorthwhile(pos, bestY, vein);
					if(!goodVein) hasSpawn = false;
				}
				g2d.setColor(hasSpawn ? Color.white : Color.BLACK);
				g2d.drawRect(chunkX, chunkZ, 1, 1); // Use relative coordinates for drawing
			}
		}
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
						: blockState.getBlock().defaultMapColor().calculateRGBColor(Brightness.HIGH);
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
			Integer bestY = timeFunction(() -> feature.findOptimalYLevel(vein, p, chunk.getHeight(Heightmap.Types.WORLD_SURFACE, 8, 8), rConfig.minY.get()), "Best Y Analysis");
			int bestYSection = chunk.getSectionIndex(bestY);
			boolean goodVein = timeFunction(() -> feature.isVeinWorthwhile(chunkPos, bestY, vein), "Worthy Vein Check");
			if(!goodVein) continue;
			if(chunk.getSection(bestYSection).hasOnlyAir()) continue;
			if(!chunk.getSection(bestYSection).maybeHas((s) -> s.is(Blocks.STONE))) continue;

			int chunkXPos = p.getX() - CHUNK_SIZE;
			int chunkZPos = p.getZ() - CHUNK_SIZE;
			int depositWidth = CHUNK_SIZE * 3;
			int depositHeight = CHUNK_SIZE * 3;
			g2d.setColor(Color.RED);
			g2d.setStroke(new BasicStroke(1));
			g2d.drawRect(chunkXPos, chunkZPos, depositWidth, depositHeight);
			renderDepositDetails(g2d, feature, p, chunkXPos, chunkZPos, depositWidth, depositHeight, bestY, vein);

			String oreName = material.getName();
			BufferedImage materialImage = mapOreItemImageToPalette(oreName);
			if (materialImage != null) {
				g2d.drawImage(materialImage, chunkXPos-8, chunkZPos, null);
			}
			g2d.drawString(oreName, chunkXPos+8, chunkZPos);
		}
		printFormatedTime(calculateMedian(functionTimes));
	}

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

	private static final List<Long> functionTimes = new ArrayList<>();
	public static <T> T timeFunction(Supplier<T> function, String functionName) {
		long startTime = System.nanoTime();
		T result = function.get();
		long endTime = System.nanoTime();

		long durationNanos = endTime - startTime;

		functionTimes.add(durationNanos);
		return result;
	}

	private static void printFormatedTime(Long nanos)
	{

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

	private static void renderDepositDetails(Graphics2D g2d, IGOreFeatureConfig feature, BlockPos p,
											 int chunkXPos, int chunkZPos, int width, int height, int bestY, Vein vein) {

		// Constants for noise thresholds
		final double NOISE_THRESHOLD = 0.3;
		final double NOISE_THRESHOLD_MEDIUM = 0.6;
		final double NOISE_THRESHOLD_HIGH = 0.8;

		ChunkPos chunkPos = new ChunkPos(p);
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
		g2d.drawString(bestY+"",  chunkXPos,  chunkZPos-16);
	}

	private static void saveImage(BufferedImage image, GameTestHelper helper, File dir, String name) {
		File outputFile = new File(dir, name + ".png");
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


