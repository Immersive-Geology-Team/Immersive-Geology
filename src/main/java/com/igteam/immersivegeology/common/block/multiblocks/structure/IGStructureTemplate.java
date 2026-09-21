package com.igteam.immersivegeology.common.block.multiblocks.structure;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class IGStructureTemplate
{
	private final int sizeX;
	private final int sizeY;
	private final int sizeZ;
	private final String[] palette;
	private final int[][][] states;

	private IGStructureTemplate(int sizeX, int sizeY, int sizeZ, String[] palette, int[][][] states)
	{
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.palette = palette;
		this.states = states;
	}

	public int sizeX()
	{
		return sizeX;
	}

	public int sizeY()
	{
		return sizeY;
	}

	public int sizeZ()
	{
		return sizeZ;
	}

	public String getBlockId(int x, int y, int z)
	{
		if(x < 0||y < 0||z < 0||x >= sizeX||y >= sizeY||z >= sizeZ) return "minecraft:air";
		int index = states[x][y][z];
		return index < 0?"minecraft:air": palette[index];
	}

	public static IGStructureTemplate load(ResourceLocation location)
	{
		String path = "/data/"+location.getNamespace()+"/structures/"+location.getPath()+".nbt";
		try(InputStream stream = IGStructureTemplate.class.getResourceAsStream(path))
		{
			if(stream==null)
			{
				IGLib.IG_LOGGER.error("Structure template not found: {}", path);
				return null;
			}
			return parse(CompressedStreamTools.readCompressed(stream));
		} catch(IOException exception)
		{
			IGLib.IG_LOGGER.error("Failed reading structure template {}: {}", path, exception.getMessage());
			return null;
		}
	}

	private static IGStructureTemplate parse(NBTTagCompound root)
	{
		NBTTagList sizeList = root.getTagList("size", 3);
		int sizeX = sizeList.getIntAt(0);
		int sizeY = sizeList.getIntAt(1);
		int sizeZ = sizeList.getIntAt(2);

		NBTTagList paletteList = root.getTagList("palette", 10);
		List<String> names = new ArrayList<>();
		for(int i = 0; i < paletteList.tagCount(); i++)
			names.add(paletteList.getCompoundTagAt(i).getString("Name"));

		int[][][] states = new int[sizeX][sizeY][sizeZ];
		for(int[][] plane : states)
			for(int[] row : plane)
				java.util.Arrays.fill(row, -1);

		NBTTagList blocks = root.getTagList("blocks", 10);
		for(int i = 0; i < blocks.tagCount(); i++)
		{
			NBTTagCompound entry = blocks.getCompoundTagAt(i);
			NBTTagList pos = entry.getTagList("pos", 3);
			int x = pos.getIntAt(0);
			int y = pos.getIntAt(1);
			int z = pos.getIntAt(2);
			if(x < 0||y < 0||z < 0||x >= sizeX||y >= sizeY||z >= sizeZ) continue;
			states[x][y][z] = entry.getInteger("state");
		}

		return new IGStructureTemplate(sizeX, sizeY, sizeZ, names.toArray(new String[0]), states);
	}
}
