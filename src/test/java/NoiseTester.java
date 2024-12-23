/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise3D;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class NoiseTester
{
	public static void main(String[] args) {
		// Base settings
		final int WIDTH = 128;          // Image width
		final int HEIGHT = 128;         // Image height
		final int FEATURE_SIZE_MIN = 10; // Minimum feature size
		final int FEATURE_SIZE_MAX = 50; // Maximum feature size
		final int FEATURE_SIZE_STEP = 10; // Step for feature size
		final int WARPED_FEATURE_SIZE_MIN = 5; // Minimum warped feature size
		final int WARPED_FEATURE_SIZE_MAX = 30; // Maximum warped feature size
		final int WARPED_FEATURE_SIZE_STEP = 5; // Step for warped feature size
		final int OCTAVES_MIN = 1;       // Minimum octaves
		final int OCTAVES_MAX = 4;       // Maximum octaves
		final String OUTPUT_DIR = "noise_images";

		// Loop through settings
		for(int seed = 1; seed < 5; seed++)
		{
			for(int featureSize = FEATURE_SIZE_MIN; featureSize <= FEATURE_SIZE_MAX; featureSize += FEATURE_SIZE_STEP)
			{
				generateAndSaveImage(WIDTH, HEIGHT, featureSize, seed, 2, OUTPUT_DIR);
			}
		}
		System.out.println("Batch image generation complete!");
	}

	private static void generateAndSaveImage(int width, int height, int featureSize, int seed, int octaves, String outputDir) {
		INoise3D noiseGen = getiNoise3D(featureSize, seed, octaves);

		// Create a BufferedImage to store the noise map
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// Generate noise values and map them to image pixels
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// Generate a noise value
				double value = noiseGen.noise(x, y, 0);

				// Map the noise value to a grayscale color
				int grayscale = (int) ((value + 1) / 2 * 255); // Normalize to [0, 255]
				grayscale = Math.max(0, Math.min(255, grayscale)); // Clamp values

				// Set the pixel color
				int rgb = (grayscale << 16) | (grayscale << 8) | grayscale;
				image.setRGB(x, y, rgb);
			}
		}

		// Save the image to a file
		try {

			String filename = String.format("noise_f%d_o%d.png", featureSize, octaves);

			File outputFile = new File(outputDir + "/seed_" + String.valueOf(seed), filename);
			if (!outputFile.exists()) {
				outputFile.mkdirs();
			}
			outputFile = new File(outputDir + "/seed_" + String.valueOf(seed), filename);
			ImageIO.write(image, "png", outputFile);
			System.out.println("Saved: " + filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static @NotNull INoise3D getiNoise3D(int featureSize, int seed, int octaves)
	{
		SimplexNoise3D simplex = new SimplexNoise3D(seed);
		SimplexNoise3D warpSimplex = new SimplexNoise3D(seed-1);

		// Warp noise generator for spacing
		INoise3D warp = (x, y, z) -> warpSimplex
				.octaves(2, 1f)
				.sinWarp(2,1)
				.flattened(-1,1)
				.bias(-.5f)
				.noise(x / 24, y / 24, z / 24);

		float spreadFactor = (90 - (float)featureSize/ 1000);
		// Primary noise generator
		return (x, y, z) -> simplex
				.bias(-0.5f + (Math.max(0, Math.min(0.5f, (float)featureSize/ 100))))
				.flattened(-1,1)
				.octaves(octaves, 1f)
				.add(warp)
				.noise(x / 24, y /24, z /24);
	}
}
