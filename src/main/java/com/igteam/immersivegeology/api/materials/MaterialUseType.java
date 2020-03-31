package com.igteam.immersivegeology.api.materials;

import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.items.IGMaterialItem;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;
import java.util.function.Function;
import com.igteam.immersivegeology.client.menu.helper.*;
/**
 * Created by Pabilo8 on 25-03-2020.
 */
public enum MaterialUseType implements IStringSerializable
{
	//Mineral items
	ROCK(ItemSubGroup.raw),

	//Metal items
	INGOT(ItemSubGroup.processed),
	PLATE(ItemSubGroup.processed),
	DUST(ItemSubGroup.processed),
	TINY_DUST(ItemSubGroup.processed),
	ROD(ItemSubGroup.processed),
	NUGGET(ItemSubGroup.processed),
	GEAR(ItemSubGroup.processed),
	WIRE(ItemSubGroup.processed),

	//Tool System, Experimental / Not sure
	PICKAXE(UseCategory.TOOL),
	SHOVEL(UseCategory.TOOL),
	HOE(UseCategory.TOOL),
	AXE(UseCategory.TOOL),
	BROADSWORD(UseCategory.TOOL),

	//Blocks

	//Metals
	STORAGE(UseCategory.BLOCK,ItemSubGroup.processed),
	SHEETMETAL(UseCategory.BLOCK,ItemSubGroup.processed),
	DUST_BLOCK(UseCategory.BLOCK,ItemSubGroup.processed),

	//Minerals / metals
	POOR_ORE(UseCategory.BLOCK,ItemSubGroup.raw),
	NORMAL_ORE(UseCategory.BLOCK,ItemSubGroup.raw),
	RICH_ORE(UseCategory.BLOCK,ItemSubGroup.raw),

	//Stones
	DIRT(UseCategory.BLOCK,ItemSubGroup.raw),
	GRAVEL(UseCategory.BLOCK,ItemSubGroup.raw),
	SAND(UseCategory.BLOCK,ItemSubGroup.raw),
	COBBLESTONE(UseCategory.BLOCK,ItemSubGroup.raw),
	SMOOTH_STONE(UseCategory.BLOCK,ItemSubGroup.raw),

	POLISHED_STONE(UseCategory.BLOCK,ItemSubGroup.processed),
	SMALL_BRICKS(UseCategory.BLOCK,ItemSubGroup.processed),
	NORMAL_BRICKS(UseCategory.BLOCK,ItemSubGroup.processed),
	ROAD_BRICKS(UseCategory.BLOCK,ItemSubGroup.processed),
	PILLAR(UseCategory.BLOCK,ItemSubGroup.processed),

	//Fluids
	FLUIDS(UseCategory.BLOCK,ItemSubGroup.misc);
	
	ItemSubGroup subGroup;
	UseCategory category;
	//Add item, block function - the default values are a resource item and a resource block
	Function<MaterialUseType, IGMaterialItem> itemFunction = (materialUseType) -> new IGMaterialResourceItem(this);
	//TODO: add blocks
	Function<MaterialUseType, IGMaterialBlock> blockFunction = (materialUseType) -> null;

	MaterialUseType(ItemSubGroup group){
		this.category = UseCategory.RESOURCE_ITEM;
		this.subGroup = group;
	}
	
	MaterialUseType()
	{
		this.category = UseCategory.RESOURCE_ITEM;
		this.subGroup = ItemSubGroup.raw;
	}

	MaterialUseType(UseCategory category)
	{
		this.category = category;
	}
	
	MaterialUseType(UseCategory category, ItemSubGroup sub)
	{
		this.category = category;
		this.subGroup = sub;
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

	public ItemSubGroup getSubGroup() {
		// TODO Auto-generated method stub
		return subGroup;
	}
}
