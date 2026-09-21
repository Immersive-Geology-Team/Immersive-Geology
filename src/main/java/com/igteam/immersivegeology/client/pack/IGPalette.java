package com.igteam.immersivegeology.client.pack;

import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class IGPalette
{
	private IGPalette()
	{
	}

	public static int[] readPaletteRow(InputStream stream) throws IOException
	{
		BufferedImage image = ImageIO.read(stream);
		if(image==null) throw new IOException("Unreadable palette image");

		int[] row = new int[image.getWidth()];
		for(int x = 0; x < image.getWidth(); x++) row[x] = image.getRGB(x, 0);
		return row;
	}

	public static Map<Integer, Integer> buildMapping(int[] key, int[] target)
	{
		Map<Integer, Integer> mapping = new HashMap<>();
		int count = Math.min(key.length, target.length);
		for(int i = 0; i < count; i++) mapping.put(key[i]&0xFFFFFF, target[i]&0xFFFFFF);
		return mapping;
	}

	public static byte[] applyPalette(InputStream source, Map<Integer, Integer> mapping) throws IOException
	{
		BufferedImage image = ImageIO.read(source);
		if(image==null) throw new IOException("Unreadable source image");

		BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < image.getHeight(); y++)
			for(int x = 0; x < image.getWidth(); x++)
			{
				int argb = image.getRGB(x, y);
				int alpha = argb >>> 24;
				if(alpha==0)
				{
					out.setRGB(x, y, 0);
					continue;
				}

				int rgb = argb&0xFFFFFF;
				Integer replaced = mapping.get(rgb);
				if(replaced==null) replaced = nearest(mapping, rgb);
				out.setRGB(x, y, (alpha << 24)|replaced);
			}

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ImageIO.write(out, "PNG", bytes);
		return bytes.toByteArray();
	}

	private static int nearest(Map<Integer, Integer> mapping, int rgb)
	{
		int target = luminance(rgb);
		int best = rgb;
		int bestDistance = Integer.MAX_VALUE;
		for(Map.Entry<Integer, Integer> entry : mapping.entrySet())
		{
			int distance = Math.abs(luminance(entry.getKey())-target);
			if(distance < bestDistance)
			{
				bestDistance = distance;
				best = entry.getValue();
			}
		}
		return best;
	}

	private static int luminance(int rgb)
	{
		int r = (rgb >> 16)&0xFF;
		int g = (rgb >> 8)&0xFF;
		int b = rgb&0xFF;
		return (r*299+g*587+b*114)/1000;
	}

	public static ResourceLocation texturePath(String path)
	{
		return new ResourceLocation("immersivegeology", "textures/"+path+".png");
	}
}
