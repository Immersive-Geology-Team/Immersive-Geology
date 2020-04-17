package com.igteam.immersivegeology.api.materials;

import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.blocks.metal.IGDustBlock;
import com.igteam.immersivegeology.common.blocks.metal.IGSheetmetalBlock;
import com.igteam.immersivegeology.common.blocks.metal.IGStorageBlock;
import com.igteam.immersivegeology.common.items.IGBaseItem;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public enum MaterialUseType implements IStringSerializable
{
	//Mineral items
	ROCK(ItemSubGroup.raw),
	CHUNK(ItemSubGroup.raw),

	//Metal/crystal items
	INGOT(ItemSubGroup.processed),
	PLATE(ItemSubGroup.processed),
	DUST(ItemSubGroup.processed),
	TINY_DUST(ItemSubGroup.processed),
	ROD(ItemSubGroup.processed),
	NUGGET(ItemSubGroup.processed),
	GEAR(ItemSubGroup.processed),
	WIRE(ItemSubGroup.processed),
	RAW_CRYSTAL(ItemSubGroup.raw),
	CRYSTAL(ItemSubGroup.processed),

	//Tool System, Experimental / Not sure
	PICKAXE(UseCategory.TOOL),
	SHOVEL(UseCategory.TOOL),
	HOE(UseCategory.TOOL),
	AXE(UseCategory.TOOL),
	BROADSWORD(UseCategory.TOOL),
	BUCKET(UseCategory.TOOL),

	//Blocks

	//Metals
	STORAGE(UseCategory.RESOURCE_BLOCK, Material.IRON, ItemSubGroup.processed)
			{
				@Override
				public IGBaseBlock getBlock(com.igteam.immersivegeology.api.materials.Material material)
				{
					return new IGStorageBlock(material);
				}
			},
	SHEETMETAL(UseCategory.RESOURCE_BLOCK, Material.IRON, ItemSubGroup.processed)
			{
				@Override
				public IGBaseBlock getBlock(com.igteam.immersivegeology.api.materials.Material material)
				{
					IGBaseBlock sheetmetal = new IGSheetmetalBlock(material);
					IGContent.addSlabFor(sheetmetal);
					return sheetmetal;
				}

			},
	DUST_BLOCK(UseCategory.RESOURCE_BLOCK, Material.SAND, ItemSubGroup.processed)
			{
				@Override
				public IGBaseBlock getBlock(com.igteam.immersivegeology.api.materials.Material material)
				{
					return new IGDustBlock(material);
				}
			},

	//Minerals / metals
	POOR_ORE(UseCategory.BLOCK, Material.IRON, ItemSubGroup.raw),
	NORMAL_ORE(UseCategory.BLOCK, Material.IRON, ItemSubGroup.raw),
	RICH_ORE(UseCategory.BLOCK, Material.IRON, ItemSubGroup.raw),

	//Stones
	DIRT(UseCategory.BLOCK, Material.ROCK, ItemSubGroup.raw),
	GRAVEL(UseCategory.BLOCK, Material.EARTH, ItemSubGroup.raw),
	SAND(UseCategory.BLOCK, Material.EARTH, ItemSubGroup.raw),
	COBBLESTONE(UseCategory.BLOCK, Material.ROCK, ItemSubGroup.raw),
	SMOOTH_STONE(UseCategory.BLOCK, Material.ROCK, ItemSubGroup.raw),

	ROUGH_BRICKS(UseCategory.BLOCK, Material.ROCK, ItemSubGroup.processed),
	POLISHED_STONE(UseCategory.BLOCK, Material.ROCK, ItemSubGroup.processed),
	SMALL_BRICKS(UseCategory.BLOCK, Material.ROCK, ItemSubGroup.processed),
	NORMAL_BRICKS(UseCategory.BLOCK, Material.ROCK, ItemSubGroup.processed),
	ROAD_BRICKS(UseCategory.BLOCK, Material.ROCK, ItemSubGroup.processed),
	PILLAR(UseCategory.BLOCK, Material.ROCK, ItemSubGroup.processed),

	//Fluids
	FLUIDS(UseCategory.BLOCK, ItemSubGroup.misc);

	private Material blockMaterial;
	private ItemSubGroup subGroup;
	private UseCategory category;

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

	MaterialUseType(ItemSubGroup group)
	{
		this.category = UseCategory.RESOURCE_ITEM;
		this.subGroup = group;
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

	public ItemSubGroup getSubGroup()
	{
		return subGroup;
	}

	public Material getMaterial()
	{
		return blockMaterial;
	}

	//Default to resource, allows for using custom classes
	public IGBaseItem getItem(com.igteam.immersivegeology.api.materials.Material material)
	{
		return new IGMaterialResourceItem(material, this);
	}

	//Default to resource, allows for using custom classes
	public IGBaseBlock getBlock(com.igteam.immersivegeology.api.materials.Material material)
	{
		return new IGMaterialBlock(material, this);
	}

	public enum UseCategory
	{
		//Used for ingots, plates, etc.
		RESOURCE_ITEM,
		//Will be used for tools
		TOOL,
		//Will be used for non-resource items
		ITEM,
		//Will be used for material blocks
		RESOURCE_BLOCK,
		//Will be used for non material blocks
		BLOCK,

	}
}
