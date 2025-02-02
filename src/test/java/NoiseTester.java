import com.igteam.immersivegeology.common.world.features.helper.GenerationBandedNoise;
import com.igteam.immersivegeology.common.world.features.helper.GenerationTubedNoise;
import com.igteam.immersivegeology.common.world.features.helper.IGenerationPattern;
import com.igteam.immersivegeology.common.world.noise.INoise3D;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class NoiseTester {
	public static void main(String[] args) {
		// Generate the noise images and compile them into a .gif
		new NoiseTester().generateGif(false);
		new NoiseTester().generateGif(true);
	}


	public void generateGif(boolean sliceY) {
		// List to hold generated images
		List<BufferedImage> images = new ArrayList<>();
		IGenerationPattern handler = new GenerationTubedNoise();
		// Example 3D noise generator (replace with your actual noise generator)
		INoise3D noise = handler.getiNoise3D(24, 1);

		// Generate 128 images and store them in the list
		for (int i = 0; i < 128; i++) {
			BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
			for (int x = 0; x < 128; x++) {
				for (int z = 0; z < 128; z++) {
					// Get noise value between -1 and 1
					float value = sliceY ? noise.noise(x, i, z) : noise.noise(x, z, i);

					// Normalize value from [-1, 1] to [0, 255]
					int grayValue = (int)(((value + 1) / 2) * 255);
					if(grayValue < 0) grayValue = 0;
					if(grayValue > 255)
					{
						grayValue = 255;
					}
					// Create grayscale color (R = G = B = grayValue)
					Color color = new Color(grayValue, grayValue, grayValue);
					image.setRGB(x, z, color.getRGB());
				}
			}
			images.add(image);
		}

		// Save images as a GIF
		try {
			File outputGif = new File("output_noise_" + (sliceY ? "sliced_y" : "sliced_z") + ".gif");
			createGif(images, outputGif);
			System.out.println("GIF saved to: " + outputGif.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Create GIF from list of images
	private void createGif(List<BufferedImage> images, File outputFile) throws IOException {
		// Create an output stream to save the GIF
		try (GifSequenceWriter writer = new GifSequenceWriter(
				ImageIO.createImageOutputStream(outputFile),
				BufferedImage.TYPE_INT_RGB,
				100, true)) {
			// Add all images to the GIF
			for (BufferedImage image : images) {
				writer.writeToSequence(image);
			}
		}
	}

	// GifSequenceWriter (to write the .gif file)
	public class GifSequenceWriter implements AutoCloseable {
		private ImageWriter writer;
		private ImageWriteParam param;
		private ImageOutputStream output;

		public GifSequenceWriter(ImageOutputStream output, int imageType, int timeBetweenFramesMS, boolean loopContinuously) throws IOException {
			this.output = output;

			writer = ImageIO.getImageWritersByFormatName("gif").next();
			param = writer.getDefaultWriteParam();
			writer.setOutput(output);
			writer.prepareWriteSequence(null);

			// Use DirectColorModel and SinglePixelPackedSampleModel for compatibility
			ColorModel colorModel = new DirectColorModel(24, 0x00FF0000, 0x0000FF00, 0x000000FF);
			SampleModel sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, 128, 128, new int[]{0x00FF0000, 0x0000FF00, 0x000000FF});
			ImageTypeSpecifier typeSpecifier = new ImageTypeSpecifier(colorModel, sampleModel);

			// Add loop extension if required (GIF loop control via ImageWriteParam)
			if (loopContinuously) {
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionType("LZW");
				param.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
			}
		}

		public void writeToSequence(BufferedImage img) throws IOException {
			writer.writeToSequence(new IIOImage(img, null, null), param);
		}

		@Override
		public void close() throws IOException {
			writer.endWriteSequence();
			output.close();
		}
	}
}