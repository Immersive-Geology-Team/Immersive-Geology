package com.igteam.immersivegeology.api.materials;

import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.blocks.IGOreBearingBlock;
import com.igteam.immersivegeology.common.blocks.metal.IGDustBlock;
import com.igteam.immersivegeology.common.blocks.metal.IGSheetmetalBlock;
import com.igteam.immersivegeology.common.blocks.metal.IGStorageBlock;
import com.igteam.immersivegeology.common.items.IGBaseItem;
import com.igteam.immersivegeology.common.items.IGMaterialItem;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public enum MaterialUseType implements IStringSerializable
{
	//Mineral items
	ROCK(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.raw),
	ORE_BEARING(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.raw)
			{
				@Override
				public IGBaseBlock[] getBlocks(com.igteam.immersivegeology.api.materials.Material material)
				{
					List<IGBaseBlock> list = new ArrayList<>();
					//Filter materials for minerals only and iterate, add to the list new ore blocks with stone mat + mineral mat
					EnumMaterials.filterMinerals().forEach(enumMaterials -> list.add(new IGOreBearingBlock(material, this, enumMaterials.material)));
					return list.toArray(new IGBaseBlock[]{});
				}

				@Nonnull
				@Override
				public String getModelPath()
				{
					return getName()+"/";
				}
			},
	CHUNK(ItemSubGroup.raw),
	ORE_CHUNK(ItemSubGroup.raw)
	{
		@Override
		public IGMaterialItem[] getItems(com.igteam.immersivegeology.api.materials.Material material)
		{
			List<IGMaterialItem> list = new ArrayList<>();
			//Filter materials for minerals only and iterate, add to the list new ore blocks with stone mat + mineral mat
			EnumMaterials.filterMinerals().forEach(enumMaterials -> list.add(new IGMaterialResourceItem(this, material, enumMaterials.material)));
			return list.toArray(new IGMaterialItem[]{});
		}
	},
	POLISHED_CHUNK(ItemSubGroup.processed),
	//Metal/crystal items
	GEAR(ItemSubGroup.processed),
	INGOT(ItemSubGroup.processed),
	PLATE(ItemSubGroup.processed),
	ROD(ItemSubGroup.processed),
	WIRE(ItemSubGroup.processed),
	ROUGH_INGOT(ItemSubGroup.processed),
	ROUGH_PLATE(ItemSubGroup.processed),
	ROUGH_ROD(ItemSubGroup.processed),
	ROUGH_GEAR(ItemSubGroup.processed),
	ROUGH_WIRE(ItemSubGroup.processed),
	NUGGET(ItemSubGroup.processed),
	DUST(ItemSubGroup.processed),
	TINY_DUST(ItemSubGroup.processed),
	RAW_CRYSTAL(ItemSubGroup.raw),
	CRYSTAL(ItemSubGroup.processed),

	//Tool System, Experimental / Not sure
	PICKAXE_HEAD(UseCategory.TOOLPART_ITEM),
	SHOVEL_HEAD(UseCategory.TOOLPART_ITEM),
	HOE_HEAD(UseCategory.TOOLPART_ITEM),
	AXE_HEAD(UseCategory.TOOLPART_ITEM),
	BROADSWORD_BLADE(UseCategory.TOOLPART_ITEM),

	BUCKET(UseCategory.STORAGE_ITEM),

	//Blocks

	//Metals
	STORAGE(UseCategory.RESOURCE_BLOCK, Material.IRON, ItemSubGroup.processed)
			{
				@Override
				public IGBaseBlock[] getBlocks(com.igteam.immersivegeology.api.materials.Material material)
				{
					IGStorageBlock storage = new IGStorageBlock(material);
					/*
					BlockIGSlab slab = IGContent.addMaterialSlabFor(storage);
					((IGBlockMaterialItem)slab.itemBlock).material = material;
					((IGBlockMaterialItem)slab.itemBlock).subtype = this;
					((IGBlockMaterialItem)slab.itemBlock).isSlab = true;
					 */
					return new IGStorageBlock[]{storage};
				}
			},
	SHEETMETAL(UseCategory.RESOURCE_BLOCK, Material.IRON, ItemSubGroup.processed)
			{
				@Override
				public IGBaseBlock[] getBlocks(com.igteam.immersivegeology.api.materials.Material material)
				{
					IGMaterialBlock sheetmetal = new IGSheetmetalBlock(material);
					return new IGMaterialBlock[]{sheetmetal};
				}

			},
	DUST_BLOCK(UseCategory.RESOURCE_BLOCK, Material.SAND, ItemSubGroup.processed)
			{
				@Override
				public IGBaseBlock[] getBlocks(com.igteam.immersivegeology.api.materials.Material material)
				{
					return new IGDustBlock[]{new IGDustBlock(material)};
				}
			},
	//Stones
	DIRT(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.raw),
	GRAVEL(UseCategory.RESOURCE_BLOCK, Material.EARTH, ItemSubGroup.raw),
	SAND(UseCategory.RESOURCE_BLOCK, Material.EARTH, ItemSubGroup.raw),
	COBBLESTONE(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.raw),

	ROUGH_BRICKS(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.processed),
	POLISHED_STONE(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.processed),
	SMALL_BRICKS(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.processed),
	NORMAL_BRICKS(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.processed),
	ROAD_BRICKS(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.processed),
	PILLAR(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.processed),
	COLUMN(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.processed),
	TILES(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.processed),

	//Fluids
	FLUIDS(UseCategory.BLOCK, Material.WATER, ItemSubGroup.misc);

	/**
	 * MaterialUseType is mark a use case for a material, i.e. an Iron Ingot is a use case for Iron
	 * It has custom group constructors for subtypes, override that, don't declare more UseCategories if not needed
	 */

	private Material blockMaterial;
	private ItemSubGroup subGroup;
	private UseCategory category;

	/**
	 * Constructors, default is resource item in "raw" creative subtab
	 */

	MaterialUseType(UseCategory category)
	{
		this(category, Material.ROCK, ItemSubGroup.raw);
	}

	MaterialUseType(ItemSubGroup group)
	{
		this(UseCategory.RESOURCE_ITEM, Material.AIR, group);
	}

	MaterialUseType(UseCategory category, ItemSubGroup sub)
	{
		this(category, Material.AIR, sub);
	}

	MaterialUseType(UseCategory category, Material mat, ItemSubGroup sub)
	{
		this.category = category;
		this.subGroup = sub;
		this.blockMaterial = mat;
	}

	/**
	 * @return item category, used to differentiate between resources, tools and other items
	 */
	public UseCategory getCategory()
	{
		return category;
	}

	/**
	 * @return name of the use type (lowercase)
	 */
	@Override
	@Nonnull
	public String getName()
	{
		return this.toString().toLowerCase(Locale.ENGLISH);
	}

	/**
	 * @return parent model path for the part, used by data generators, relative to items/ or blocks/
	 */
	@Nonnull
	public String getModelPath()
	{
		return "";
	}

	/**
	 * @return name used by forge tags (plural, lowercase)
	 */
	@Nonnull
	public String getTagName()
	{
		String name = getName();
		return name.charAt(name.length()-1)=='s'?name: name+"s";
	}

	/**
	 * @return item subgroup in creative inventory
	 */
	public ItemSubGroup getSubGroup()
	{
		return subGroup;
	}

	/**
	 * @return @return vanilla block material (stone, dirt, metal, etc.)
	 */
	public Material getMaterial()
	{
		return blockMaterial;
	}

	/**
	 * @return items (default only one item) created by this type
	 */
	//Default to resource, allows for using custom classes
	public IGBaseItem[] getItems(com.igteam.immersivegeology.api.materials.Material material)
	{
		return new IGMaterialResourceItem[]{new IGMaterialResourceItem(this, material)};
	}

	/**
	 * @return blocks (default only one block) created by this type
	 */
	//Default to resource, allows for using custom classes
	public IGBaseBlock[] getBlocks(com.igteam.immersivegeology.api.materials.Material material)
	{
		return new IGMaterialBlock[]{new IGMaterialBlock(this, material)};
	}

	/**
	 * Defines use category, don't add new unless really needed
	 */
	public enum UseCategory
	{
		//Used for ingots, plates, etc.
		RESOURCE_ITEM,
		//Will be used for modular tool system parts
		TOOLPART_ITEM,
		//Will be used for storage items such as buckets, cans
		STORAGE_ITEM,
		//Will be used for non-resource items
		ITEM,
		//Used for material blocks
		RESOURCE_BLOCK,
		//Will be used for non material blocks
		BLOCK
	}
}
