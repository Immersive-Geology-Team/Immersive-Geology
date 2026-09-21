package com.igteam.immersivegeology.core.material.helper.flags;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;

public enum ItemCategoryFlags implements IFlagType<ItemCategoryFlags>
{
	INGOT(1),
	WIRE(1),
	GEAR(1),
	ROD(1),
	CLAY(0),
	POWDER(2),
	GRIT(2),
	FUEL(2),
	SLAG(2),
	PELLET(2),
	OXIDE_PELLET(2),
	POWDERED_SLAG(2),
	PLATE(1),
	POOR_ORE(0),
	NORMAL_ORE(0),
	RICH_ORE(0),
	NUGGET(1),
	CRYSTAL(3),
	COMPOUND_DUST(3),
	CRUSHED_ORE(2),
	METAL_OXIDE(3),
	DIRTY_CRUSHED_ORE(2),
	BUCKET(3),
	CLEAN_FLASK(3),
	CLOUDY_FLASK(3),
	MECHANICAL_COMPONENT(1),
	MOLD_BLANK(1),
	MOLD_PLATE(1),
	MOLD_GEAR(1),
	MOLD_ROD(1),
	MOLD_WIRE(1),
	MOLD_BLOCK(1),
	MOLD_INGOT(1),
	MOLD_NUGGET(1),
	HAMMER(4),
	DRILL_HEAD(4),
	MISC(4),
	BLUEPRINT(4),
	SKIN_COMPONENT(4),
	TOOL_HOE(4),
	SEDIMENT(0);

	private final int groupOrdinal;

	ItemCategoryFlags(int group)
	{
		this.groupOrdinal = group;
	}

	@Override
	public ItemCategoryFlags getValue()
	{
		return this;
	}

	@Override
	public ItemSubGroup getSubGroup()
	{
		return ItemSubGroup.values()[groupOrdinal];
	}

	@Override
	public String getOreDictBase()
	{
		switch(this)
		{
			case INGOT:
			case POWDER:
			case GRIT:
			case GEAR:
			case NUGGET:
			case PLATE:
			case ROD:
			case WIRE:
			case PELLET:
			case CRYSTAL:
				return IFlagType.super.getOreDictBase();
			default:
				return "";
		}
	}

	public int getVariations()
	{
		switch(this)
		{
			case INGOT:
				return 13;
			case GEAR:
			case NUGGET:
				return 6;
			case RICH_ORE:
			case NORMAL_ORE:
			case POOR_ORE:
				return 7;
			case CRUSHED_ORE:
				return 5;
			case PLATE:
			case DIRTY_CRUSHED_ORE:
				return 2;
			default:
				return 1;
		}
	}

	public boolean hasPalette()
	{
		switch(this)
		{
			case INGOT:
			case GEAR:
			case CRUSHED_ORE:
			case POOR_ORE:
			case NORMAL_ORE:
			case RICH_ORE:
			case NUGGET:
			case DIRTY_CRUSHED_ORE:
			case PLATE:
			case SLAG:
			case GRIT:
			case POWDER:
			case METAL_OXIDE:
			case COMPOUND_DUST:
			case DRILL_HEAD:
			case TOOL_HOE:
			case POWDERED_SLAG:
				return true;
			default:
				return false;
		}
	}

	public boolean isMold()
	{
		switch(this)
		{
			case MOLD_PLATE:
			case MOLD_GEAR:
			case MOLD_ROD:
			case MOLD_WIRE:
			case MOLD_BLOCK:
			case MOLD_INGOT:
			case MOLD_NUGGET:
				return true;
			default:
				return false;
		}
	}
}
