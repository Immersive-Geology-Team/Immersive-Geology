/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.gametest.tests;

import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.MapColor.Brightness;

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

	private static final int CHUNK_SIZE = 16;  // Minecraft chunk size in blocks
	private static final int MAP_SIZE_IN_CHUNKS = 16;
	private static final int IMAGE_SIZE = CHUNK_SIZE * MAP_SIZE_IN_CHUNKS;  // Size for 32x32 chunks
	private static MinecraftServer server;
	private static long seed;
	// GameTest method that captures the spawn chunks
	@GameTest
	public static void captureSpawnChunks(GameTestHelper helper) {

		Level level = helper.getLevel();
		if (level == null) {
			helper.fail("Level not found.");
			return;
		}
		server = level.getServer();
		seed = helper.getLevel().getSeed();
		// Get the spawn position (spawn point)
		BlockPos spawnPos = level.getSharedSpawnPos();
		int spawnChunkX = spawnPos.getX() / CHUNK_SIZE;
		int spawnChunkZ = spawnPos.getZ() / CHUNK_SIZE;

		// Create a BufferedImage to store the top-down image
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		int size = MAP_SIZE_IN_CHUNKS / 2;
		// Loop through a grid of 16x16 chunks around the spawn point
		for (int chunkX = -size; chunkX < size; chunkX++) {
			for (int chunkZ = -size; chunkZ < size; chunkZ++) {
				ChunkPos chunkPos = new ChunkPos(spawnChunkX + chunkX, spawnChunkZ + chunkZ);
				LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);

				// Render the chunk to the image
				renderChunkToImage(chunk, g2d, chunkX + size, chunkZ + size);
			}
		}

		// Render the Mineral Deposits
		for(Entry<BlockPos, GeologyMaterial> entry : toRender.entrySet())
		{
			GeologyMaterial material = entry.getValue();
			BlockPos p = entry.getKey();
			g2d.getFontRenderContext();
			g2d.setColor(Color.RED);
			int chunkXPos = p.getX();
			int chunkZPos = p.getZ();
			g2d.setStroke(new BasicStroke(1));  // Border width
			g2d.drawRect(chunkXPos-CHUNK_SIZE, chunkZPos-CHUNK_SIZE, CHUNK_SIZE * 3, CHUNK_SIZE*3);
			if(material != null)
			{
				String oreName = material.getName();
				BufferedImage testImage = mapOreItemImageToPalette(oreName);
				g2d.drawImage(testImage, chunkXPos, chunkZPos, null);
			}
		}

		// Save the image to a file
		File outputFile = new File(server.getServerDirectory(), "spawn_chunks.png");
		try {
			ImageIO.write(image, "PNG", outputFile);
			helper.succeed();
		} catch (IOException e) {
			helper.fail("Error saving the image: " + e.getMessage());
		}

		g2d.dispose();
	}

	public static HashMap<BlockPos, GeologyMaterial> toRender = new HashMap<>();

	private static void renderChunkToImage(LevelChunk chunk, Graphics2D g2d, int chunkXOffset, int chunkZOffset) {
		final int blockSize = 1;  // Size of each block in pixels for the image
		boolean foundFeature = false;  // Flag to mark if we found the feature
		ChunkPos chunkPos = chunk.getPos();
		// Loop through the blocks in the chunk and render them
		int midPoint = CHUNK_SIZE / 2;
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int z = 0; z < CHUNK_SIZE; z++) {
				// Get block position for the chunk
				BlockPos blockPos = new BlockPos(x, chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z), z);
				BlockState blockState = chunk.getBlockState(blockPos);
				Holder<Biome> biomeHolder = chunk.getNoiseBiome(x, chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z), z);
				Biome biome = biomeHolder.get();
				int col = blockState.is(Blocks.WATER) ? biome.getWaterColor() : blockState.getBlock().defaultMapColor().calculateRGBColor(Brightness.NORMAL);
				Color blockColor = new Color(col);

				foundFeature = isCustomOreFeaturePresent(biomeHolder,chunkPos);

				// Calculate position in the image
				int xPos = (chunkXOffset * CHUNK_SIZE + x) * blockSize;
				int zPos = (chunkZOffset * CHUNK_SIZE + z) * blockSize;

				if(foundFeature && x == midPoint && z == midPoint) toRender.put(new BlockPos(xPos, 0, zPos), mineralEntry);
				// Set the color and render the block
				g2d.setColor(blockColor);
				g2d.fillRect(xPos, zPos, blockSize, blockSize);
			}
		}
	}
	private static boolean isCustomOreFeaturePresent(Holder<Biome> biomeHolder, ChunkPos pos) {
		// Iterate over the placed features in the biome's generation settings
		Biome biome = biomeHolder.get();
		List<HolderSet<PlacedFeature>> features = biome.getGenerationSettings().features();
		for (HolderSet<PlacedFeature> featureSet : features) {
			for (Holder<PlacedFeature> featureHolder : featureSet) {
				PlacedFeature placedFeature = featureHolder.value();
				// Check if the feature matches the custom ore feature and its configuration
				if (isCustomOreFeature(placedFeature, pos, biomeHolder)) {
					return true;  // Custom ore feature found
				}
			}
		}
		return false;  // No custom ore feature found in the biome
	}
	private static GeologyMaterial mineralEntry;
	private static boolean isCustomOreFeature(PlacedFeature placedFeature, ChunkPos pos, Holder<Biome> biome) {
		// Ensure we check if the placed feature is of type IGOreFeature
		ConfiguredFeature<?, ?> feature = placedFeature.feature().get();
		if (feature.config() instanceof IGOreFeatureConfig igConfig) {
			boolean canSpawn = igConfig.canSpawn() && igConfig.canSpawnAt(pos.getWorldPosition(), (p) -> biome) && igConfig.canPlaceVein(pos, seed, biome);

			if(canSpawn) mineralEntry = igConfig.entry().instance();
			return canSpawn;  // This feature is a custom ore feature with the correct config
		}

		return false;  // Not a custom ore feature
	}

	private static final String BASE_PATH = "../src/main/resources/assets/immersivegeology/textures/";
	private static final Map<String, BufferedImage> ORE_PALETTES = new HashMap<>();
	private static final Map<Integer, Color> PALETTE_KEY_COLORS = new HashMap<>();
	private static BufferedImage oreItemImage;  // To store the ore "powder" item texture

	// Method to load all the necessary files (palette key and specific ore palettes)
	static {
		try {
			// Load the palette key (this contains the 8x1 image for the ore colors)
			loadPaletteKey();
			loadOrePalettes();
			loadOreItemImage();

		} catch (IOException e) {
			e.printStackTrace();  // Handle the loading error gracefully
		}
	}

	// Loads the palette key image
	private static void loadPaletteKey() throws IOException {
		File paletteKeyFile = new File(BASE_PATH + "palette/palette_key.png");
		if (paletteKeyFile.exists()) {
			BufferedImage paletteKeyImage = ImageIO.read(paletteKeyFile);
			// Iterate through the key image to map the colors
			for (int i = 0; i < paletteKeyImage.getWidth(); i++) {
				Color color = new Color(paletteKeyImage.getRGB(i, 0));
				PALETTE_KEY_COLORS.put(i, color);
			}
		} else {
			throw new IOException("Palette key file not found.");
		}
	}


	private static void loadOreItemImage() throws IOException {
		File oreItemImageFile = new File(BASE_PATH + "palette/item/powder/type_1.png");
		if (oreItemImageFile.exists()) {
			oreItemImage = ImageIO.read(oreItemImageFile);
		} else {
			throw new IOException("Ore item image (type_1.png) not found.");
		}
	}
	private static BufferedImage mapOreItemImageToPalette(String name) {
		if (oreItemImage == null) {
			return null;
		}

		// Create a new image to store the modified ore image
		BufferedImage modifiedOreItemImage = new BufferedImage(oreItemImage.getWidth(), oreItemImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < oreItemImage.getHeight()-1; y++) {
			for (int x = 0; x < oreItemImage.getWidth()-1; x++) {
				Color pixelColor = new Color(oreItemImage.getRGB(x, y), true);

				// Check if this pixel matches any color in the palette key
				for (Integer key : PALETTE_KEY_COLORS.keySet()) {
					Color paletteKeyColor = PALETTE_KEY_COLORS.get(key);
					if (isColorClose(pixelColor, paletteKeyColor)) {
						BufferedImage orePalette = getOrePalette(name);  // Get the corresponding ore palette
						if (orePalette != null) {
							Color replacementColor = new Color(orePalette.getRGB(key,0));
							modifiedOreItemImage.setRGB(x, y, replacementColor.getRGB());  // Set the modified color
						}
						break;
					}
				}
			}
		}
		return modifiedOreItemImage;
	}

	private static Color getColorFromOrePalette(BufferedImage orePalette, Color pixelColor) {
		int width = orePalette.getWidth();
		for(int x = 0; x < width; x++)
		{
			Color c = new Color(orePalette.getRGB(x,0));
			if(isColorClose(c, pixelColor))
			{
				return c;
			}
		}

		return pixelColor;
	}

	// Helper method to check if two colors are close (within some threshold)
	private static boolean isColorClose(Color color1, Color color2) {
		int threshold = 3;  // Allow for small color differences
		int rDiff = Math.abs(color1.getRed() - color2.getRed());
		int gDiff = Math.abs(color1.getGreen() - color2.getGreen());
		int bDiff = Math.abs(color1.getBlue() - color2.getBlue());
		return rDiff <= threshold && gDiff <= threshold && bDiff <= threshold;
	}

	// Loads ore palettes from the "palettes" directory
	private static void loadOrePalettes() throws IOException {
		// Example: Load palettes for different ores, this could be dynamic based on ores available
		Set<String> oreNames = Arrays.stream(MineralEnum.values()).map(e -> e.getName().toLowerCase()).collect(Collectors.toSet());
		for (String ore : oreNames) {

			File orePaletteFile = new File(BASE_PATH + "palette/palettes/"+ore+"/pristine.png");
			if (orePaletteFile.exists()) {
				BufferedImage orePaletteImage = ImageIO.read(orePaletteFile);
				ORE_PALETTES.put(ore, orePaletteImage);
			} else {
				System.err.println("Ore palette for " + ore + " not found.");
			}
		}
	}

	// Get the image corresponding to an ore palette (used for rendering ore features)
	private static BufferedImage getOrePalette(String oreName) {
		return ORE_PALETTES.getOrDefault(oreName, null);  // Return null if not found
	}
}

