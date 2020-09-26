package com.igteam.immersivegeology.api.materials;

import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.blocks.IGOreBearingBlock;
import com.igteam.immersivegeology.common.blocks.metal.IGDustBlock;
import com.igteam.immersivegeology.common.blocks.metal.IGSheetmetalBlock;
import com.igteam.immersivegeology.common.blocks.metal.IGStorageBlock;
import com.igteam.immersivegeology.common.blocks.plant.IGLogBlock;
import com.igteam.immersivegeology.common.blocks.plant.IGRockMossBlock;
import com.igteam.immersivegeology.common.items.IGBaseItem;
import com.igteam.immersivegeology.common.items.IGMaterialItem;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
import com.igteam.immersivegeology.common.items.IGMaterialYieldItem;
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
	GENERATED_ORE(UseCategory.DUMMY), //This one is for ore blocks
	GENERATED_CHUNKS(UseCategory.DUMMY), //This one is for chunks
	ORE_BEARING(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.raw)
	{
		@Override
		public IGBaseBlock[] getBlocks(com.igteam.immersivegeology.api.materials.Material material)
		{
			List<IGBaseBlock> list = new ArrayList<>();
			//Filter materials for World Generation acceptable ores and iterate, add to the list new ore blocks with stone mat + mineral mat
			EnumMaterials.filterByUseType(GENERATED_ORE).forEach(enumMaterials -> list.add(new IGOreBearingBlock(material, this, enumMaterials.material)));
			return list.toArray(new IGBaseBlock[]{});
		}

		@Nonnull
		@Override
		public String getModelPath()
		{
			return getName()+"/";
		}
	},
	CHUNK(UseCategory.RESOURCE_ITEM, ItemSubGroup.raw, 162)
	{
		@Override
		public IGMaterialItem[] getItems(com.igteam.immersivegeology.api.materials.Material material)
		{
			List<IGMaterialItem> list = new ArrayList<>();
			list.add(new IGMaterialYieldItem(this, getStoneYield(), getOreYield(), material));
			return list.toArray(new IGMaterialItem[]{});
		}
	},
	ORE_CHUNK(UseCategory.RESOURCE_ITEM, ItemSubGroup.raw, 18, 144)
	{
		@Override
		public IGMaterialItem[] getItems(com.igteam.immersivegeology.api.materials.Material material)
		{
			List<IGMaterialItem> list = new ArrayList<>();
			//Filter materials for those having rock chunks only and iterate, add to the list new ore blocks with stone mat + mineral mat
			EnumMaterials.filterByUseType(GENERATED_CHUNKS).forEach(enumMaterials -> list.add(new IGMaterialYieldItem(this, getStoneYield(), getOreYield(), material, enumMaterials.material)));
			return list.toArray(new IGMaterialItem[]{});
		}
	},
	POLISHED_CHUNK(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	ROCK_BIT(UseCategory.RESOURCE_ITEM, ItemSubGroup.raw, 40)
	{
		@Override
		public IGMaterialItem[] getItems(com.igteam.immersivegeology.api.materials.Material material)
		{
			List<IGMaterialItem> list = new ArrayList<>();
			list.add(new IGMaterialYieldItem(this, getStoneYield(), getOreYield(), material));
			return list.toArray(new IGMaterialItem[]{});
		}
	},
	ORE_BIT(UseCategory.RESOURCE_ITEM, ItemSubGroup.raw, 8, 32)
	{
		@Override
		public IGMaterialItem[] getItems(com.igteam.immersivegeology.api.materials.Material material)
		{
			List<IGMaterialItem> list = new ArrayList<>();
			//Filter materials for minerals only and iterate, add to the list new ore blocks with stone mat + mineral mat
			EnumMaterials.filterByUseType(GENERATED_CHUNKS).forEach(enumMaterials -> list.add(new IGMaterialYieldItem(this, getStoneYield(), getOreYield(), material, enumMaterials.material)));
			return list.toArray(new IGMaterialItem[]{});
		}
	},
	//Metal/crystal items
	GEAR(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	INGOT(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	PLATE(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	ROD(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	WIRE(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	ROUGH_INGOT(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	ROUGH_PLATE(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	ROUGH_ROD(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	ROUGH_GEAR(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	ROUGH_WIRE(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	NUGGET(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	DUST(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	TINY_DUST(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	RAW_CRYSTAL(UseCategory.RESOURCE_ITEM, ItemSubGroup.raw),
	CRYSTAL(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),

	//Organic stuff
	STICK(UseCategory.RESOURCE_ITEM, ItemSubGroup.raw),
	PLANK(UseCategory.RESOURCE_ITEM, ItemSubGroup.processed),
	PLANKS(UseCategory.RESOURCE_BLOCK, ItemSubGroup.processed),
	LOG(UseCategory.RESOURCE_BLOCK, ItemSubGroup.raw)
	{
		@Override
		public IGBaseBlock[] getBlocks(com.igteam.immersivegeology.api.materials.Material material)
		{
			IGLogBlock log = new IGLogBlock(material);
			return new IGLogBlock[]{log};
		}
	},
	STRIPPED_LOG(UseCategory.RESOURCE_BLOCK, ItemSubGroup.processed),

	//Tool System, Experimental / Not sure
	HAMMER_HEAD(UseCategory.TOOLPART_ITEM),
	PICKAXE_HEAD(UseCategory.TOOLPART_ITEM),
	SHOVEL_HEAD(UseCategory.TOOLPART_ITEM),
	HOE_HEAD(UseCategory.TOOLPART_ITEM),
	AXE_HEAD(UseCategory.TOOLPART_ITEM),
	BROADSWORD_BLADE(UseCategory.TOOLPART_ITEM),
	HANDLE(UseCategory.TOOLPART_ITEM),
	BINDING(UseCategory.TOOLPART_ITEM),

	BUCKET(UseCategory.STORAGE_ITEM),

	//Blocks

	//Metals
	STORAGE_BLOCK(UseCategory.RESOURCE_BLOCK, Material.IRON, ItemSubGroup.processed)
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

	MOSS_ROCK(UseCategory.RESOURCE_BLOCK, Material.ROCK, ItemSubGroup.raw){
		@Override
		public IGBaseBlock[] getBlocks(com.igteam.immersivegeology.api.materials.Material material)
		{
			return new IGRockMossBlock[]{new IGRockMossBlock(material)};
		}
	},
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
	private int stoneYeild;
	private int oreYield;

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
	
	MaterialUseType(UseCategory category, ItemSubGroup sub, int stoneYeild)
	{
		this(category, sub, stoneYeild, 0);
	}

	MaterialUseType(UseCategory category, ItemSubGroup sub, int stoneYeild, int oreYield)
	{
		this.category = category;
		this.subGroup = sub;
		this.blockMaterial = Material.AIR;
		this.stoneYeild = stoneYeild;
		this.oreYield = oreYield;
	}

	MaterialUseType(UseCategory category, Material mat, ItemSubGroup sub)
	{
		this.category = category;
		this.subGroup = sub;
		this.blockMaterial = mat;
	}

	public int getStoneYield()
	{
		return this.stoneYeild;
	}

	public int getOreYield()
	{
		return this.oreYield;
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
		//Used as a tag / marker for categories with no items
		DUMMY,
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
