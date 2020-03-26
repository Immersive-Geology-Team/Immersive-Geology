package com.igteam.immersivegeology.api.materials;

import net.minecraft.util.IStringSerializable;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public enum MaterialUseType implements IStringSerializable
{
	//Mineral items
	ROCK,

	//Metal items
	INGOT,
	PLATE,
	DUST,
	TINY_DUST,
	ROD,
	NUGGET,
	GEAR,
	WIRE,

	//Tool System, Experimental / Not sure
	PICKAXE,
	SHOVEL,
	HOE,
	AXE,
	BROADSWORD,

	//Blocks

	//Metals
	STORAGE,
	SHEETMETAL,
	DUST_BLOCK,

	//Minerals / metals
	POOR_ORE,
	NORMAL_ORE,
	RICH_ORE,

	//Stones
	DIRT,
	GRAVEL,
	SAND,
	COBBLESTONE,
	SMOOTH_STONE,

	POLISHED_STONE,
	SMALL_BRICKS,
	NORMAL_BRICKS,
	ROAD_BRICKS,
	PILLAR,

	//Fluids
	FLUIDS;

	@Override
	public String getName()
	{
		return this.toString().toLowerCase();
	}
}
