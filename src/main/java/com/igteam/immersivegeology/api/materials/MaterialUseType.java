package com.igteam.immersivegeology.api.materials;

import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.items.IGMaterialItem;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;
import java.util.function.Function;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public enum MaterialUseType implements IStringSerializable
{
	//Mineral items
	ROCK(),

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
	PICKAXE(UseCategory.TOOL),
	SHOVEL(UseCategory.TOOL),
	HOE(UseCategory.TOOL),
	AXE(UseCategory.TOOL),
	BROADSWORD(UseCategory.TOOL),

	//Blocks

	//Metals
	STORAGE(UseCategory.BLOCK),
	SHEETMETAL(UseCategory.BLOCK),
	DUST_BLOCK(UseCategory.BLOCK),

	//Minerals / metals
	POOR_ORE(UseCategory.BLOCK),
	NORMAL_ORE(UseCategory.BLOCK),
	RICH_ORE(UseCategory.BLOCK),

	//Stones
	DIRT(UseCategory.BLOCK),
	GRAVEL(UseCategory.BLOCK),
	SAND(UseCategory.BLOCK),
	COBBLESTONE(UseCategory.BLOCK),
	SMOOTH_STONE(UseCategory.BLOCK),

	POLISHED_STONE(UseCategory.BLOCK),
	SMALL_BRICKS(UseCategory.BLOCK),
	NORMAL_BRICKS(UseCategory.BLOCK),
	ROAD_BRICKS(UseCategory.BLOCK),
	PILLAR(UseCategory.BLOCK),

	//Fluids
	FLUIDS(UseCategory.BLOCK);

	UseCategory category;
	//Add item, block function - the default values are a resource item and a resource block
	Function<MaterialUseType, IGMaterialItem> itemFunction = (materialUseType) -> new IGMaterialResourceItem(this);
	//TODO: add blocks
	Function<MaterialUseType, IGMaterialBlock> blockFunction = (materialUseType) -> null;

	MaterialUseType()
	{
		this.category = UseCategory.RESOURCE_ITEM;
	}

	MaterialUseType(UseCategory category)
	{
		this.category = category;
	}

	//Use, when the material will return a non-standard item or block
	MaterialUseType(UseCategory category, Function<MaterialUseType, IGMaterialItem> supplier_i, Function<MaterialUseType, IGMaterialBlock> supplier_b)
	{
		this.category = category;
		this.itemFunction = supplier_i;
		this.blockFunction = supplier_b;
	}

	public UseCategory getCategory()
	{
		return category;
	}

	@Override
	public String getName()
	{
		return this.toString().toLowerCase(Locale.ENGLISH);
	}

	public IGMaterialItem createItem()
	{
		return itemFunction.apply(this);
	}

	public enum UseCategory
	{
		//Used for ingots, plates, etc.
		RESOURCE_ITEM,
		//Will be used for tools
		TOOL,
		//Will be used for non-resource items
		ITEM,
		//Will be used for blocks
		BLOCK
	}
}
