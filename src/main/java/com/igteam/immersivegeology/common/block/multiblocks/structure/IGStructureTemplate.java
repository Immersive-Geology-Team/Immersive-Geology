package com.igteam.immersivegeology.common.block.multiblocks.structure;

import com.google.common.base.Optional;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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
	private final IBlockState[] palette;
	private final String[] paletteNames;
	private final int[][][] states;

	private IGStructureTemplate(int sizeX, int sizeY, int sizeZ, IBlockState[] palette, String[] paletteNames,
								int[][][] states)
	{
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.palette = palette;
		this.paletteNames = paletteNames;
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

	private int indexAt(int x, int y, int z)
	{
		if(x < 0||y < 0||z < 0||x >= sizeX||y >= sizeY||z >= sizeZ) return -1;
		return states[x][y][z];
	}

	public IBlockState getBlockState(int x, int y, int z)
	{
		int index = indexAt(x, y, z);
		return index < 0?null: palette[index];
	}

	public String getBlockId(int x, int y, int z)
	{
		int index = indexAt(x, y, z);
		return index < 0?"minecraft:air": paletteNames[index];
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
		List<IBlockState> resolved = new ArrayList<>();
		List<String> names = new ArrayList<>();
		for(int i = 0; i < paletteList.tagCount(); i++)
		{
			NBTTagCompound entry = paletteList.getCompoundTagAt(i);
			String name = entry.getString("Name");
			names.add(name);
			resolved.add(resolveState(name, entry.getCompoundTag("Properties")));
		}

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

		return new IGStructureTemplate(sizeX, sizeY, sizeZ,
				resolved.toArray(new IBlockState[0]), names.toArray(new String[0]), states);
	}

	private static IBlockState resolveState(String name, NBTTagCompound properties)
	{
		if(name==null||name.isEmpty()||"minecraft:air".equals(name)) return null;

		Block block = Block.REGISTRY.getObject(new ResourceLocation(name));
		if(block==null||block==Blocks.AIR)
		{
			IGLib.IG_LOGGER.warn("Structure template references unknown block {}", name);
			return null;
		}

		IBlockState state = block.getDefaultState();
		if(properties==null) return state;

		for(String key : properties.getKeySet())
		{
			IProperty<?> property = block.getBlockState().getProperty(key);
			if(property==null)
			{
				IGLib.IG_LOGGER.warn("Block {} has no property {}", name, key);
				continue;
			}
			state = applyProperty(state, property, properties.getString(key), name);
		}
		return state;
	}

	private static <T extends Comparable<T>> IBlockState applyProperty(IBlockState state, IProperty<T> property,
																	   String value, String name)
	{
		Optional<T> parsed = property.parseValue(value);
		if(!parsed.isPresent())
		{
			IGLib.IG_LOGGER.warn("Block {} cannot parse {}={}", name, property.getName(), value);
			return state;
		}
		return state.withProperty(property, parsed.get());
	}
}
