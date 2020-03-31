package com.igteam.immersivegeology.api.materials;

import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.items.IGMaterialItem;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;

import net.minecraft.block.material.Material;
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
	CHUNK(ItemSubGroup.raw),

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
	STORAGE(UseCategory.BLOCK,Material.IRON,ItemSubGroup.processed),
	SHEETMETAL(UseCategory.MATERIAL_BLOCK,Material.IRON,ItemSubGroup.processed),
	DUST_BLOCK(UseCategory.BLOCK,Material.SAND,ItemSubGroup.processed),

	//Minerals / metals
	POOR_ORE(UseCategory.MATERIAL_BLOCK,Material.IRON,ItemSubGroup.raw),
	NORMAL_ORE(UseCategory.MATERIAL_BLOCK,Material.IRON,ItemSubGroup.raw),
	RICH_ORE(UseCategory.MATERIAL_BLOCK,Material.IRON,ItemSubGroup.raw),

	//Stones
	DIRT(UseCategory.BLOCK,Material.ROCK,ItemSubGroup.raw),
	GRAVEL(UseCategory.BLOCK,Material.EARTH,ItemSubGroup.raw),
	SAND(UseCategory.BLOCK,Material.EARTH,ItemSubGroup.raw),
	COBBLESTONE(UseCategory.BLOCK,Material.ROCK,ItemSubGroup.raw),
	SMOOTH_STONE(UseCategory.BLOCK,Material.ROCK,ItemSubGroup.raw),

	POLISHED_STONE(UseCategory.BLOCK,Material.ROCK,ItemSubGroup.processed),
	SMALL_BRICKS(UseCategory.BLOCK,Material.ROCK,ItemSubGroup.processed),
	NORMAL_BRICKS(UseCategory.BLOCK,Material.ROCK,ItemSubGroup.processed),
	ROAD_BRICKS(UseCategory.BLOCK,Material.ROCK,ItemSubGroup.processed),
	PILLAR(UseCategory.BLOCK,Material.ROCK,ItemSubGroup.processed),

	//Fluids
	FLUIDS(UseCategory.BLOCK,ItemSubGroup.misc);
	
	private Material blockMaterial;
	private ItemSubGroup subGroup;
	private UseCategory category;
	//Add item, block function - the default values are a resource item and a resource block
	Function<MaterialUseType, IGMaterialItem> itemFunction = (materialUseType) -> new IGMaterialResourceItem(this);
	//TODO: add blocks
	Function<MaterialUseType, IGMaterialBlock> materialBlockFunction = (materialUseType) -> new IGMaterialBlock(this);
	
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
	
	MaterialUseType(UseCategory category, Material mat, ItemSubGroup sub)
	{
		this.category = category;
		this.subGroup = sub;
		this.blockMaterial = mat;
	}

//	//Use, when the material will return a non-standard item or block
//	MaterialUseType(UseCategory category, Function<MaterialUseType, IGMaterialItem> supplier_i, Function<MaterialUseType, IGMaterialBlock> supplier_b)
//	{
//		this.category = category;
//		this.itemFunction = supplier_i;
//		this.materialBlockFunction = supplier_b;
//	}

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
	
	public IGMaterialBlock createMaterialBlock()
	{
		return materialBlockFunction.apply(this);
	}

	public enum UseCategory
	{
		//Used for ingots, plates, etc.
		RESOURCE_ITEM,
		//Will be used for tools
		TOOL,
		//Will be used for non-resource items
		ITEM,
		//Will be used for non material blocks
		BLOCK,
		MATERIAL_BLOCK	
	}

	public ItemSubGroup getSubGroup() {
		// TODO Auto-generated method stub
		return subGroup;
	}
	
	public Material getMaterial() {
		return blockMaterial;
	}
}
