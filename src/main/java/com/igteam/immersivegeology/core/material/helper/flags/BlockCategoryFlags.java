package com.igteam.immersivegeology.core.material.helper.flags;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import net.minecraft.util.BlockRenderLayer;

public enum BlockCategoryFlags implements IFlagType<BlockCategoryFlags>
{
	STORAGE_BLOCK(4),
	ORE_BLOCK(0),
	GEODE_BLOCK(0),
	DEFAULT_BLOCK(4),
	SLAB(4),
	DUST_BLOCK(1),
	SHEETMETAL_BLOCK(4),
	SHEETMETAL_SLAB(4),
	SHEETMETAL_STAIRS(4),
	FENCE(4),
	ENERGY_PIPE(4),
	HYDROVENT(0),
	STAIRS(4),
	FLUID(3),
	SLURRY(3),
	CLOUDY_SLURRY(3),
	SCAFFOLDING(4),
	EVAPORATE(0),
	EVAPORATE_CRYSTAL(0),
	ENGINEERING_BLOCK(4),
	ADVANCED_ENGINEERING_BLOCK(4),
	CRATE(4),
	MISC(4);

	private final int groupOrdinal;

	BlockCategoryFlags(int ordinal)
	{
		this.groupOrdinal = ordinal;
	}

	@Override
	public BlockCategoryFlags getValue()
	{
		return this;
	}

	@Override
	public ItemSubGroup getSubGroup()
	{
		return ItemSubGroup.values()[groupOrdinal];
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		if(this==ORE_BLOCK||this==EVAPORATE_CRYSTAL) return BlockRenderLayer.CUTOUT_MIPPED;
		if(this==ENERGY_PIPE) return BlockRenderLayer.TRANSLUCENT;
		return BlockRenderLayer.SOLID;
	}

	@Override
	public String getOreDictBase()
	{
		if(this==STORAGE_BLOCK) return "block";
		if(this==ORE_BLOCK) return "ore";
		return "";
	}

	public int getVariations()
	{
		return 1;
	}

	public boolean hasPalette()
	{
		return this==STORAGE_BLOCK;
	}
}
