package com.igteam.immersivegeology.core.material.helper.material;

import com.igteam.immersivegeology.client.helper.IGVeinTextureType;
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreBlockMeta;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public interface MaterialHelper
{
	String getName();

	boolean hasFlag(IFlagType<?> category);

	Set<IFlagType<?>> getFlags();

	int getColor(IFlagType<?> flag, int secondaryColors);

	boolean acceptableStoneType(MaterialStone instance);

	boolean checkExistingImplementation(IFlagType<?> flag);

	boolean checkExistingImplementation(ModFlags mod, IFlagType<?> flag);

	void addExistingFlag(ModFlags mod, ItemCategoryFlags... flags);

	void addExistingFlag(ModFlags mod, BlockCategoryFlags... flags);

	default boolean acceptableStoneType(IStoneType stone)
	{
		return acceptableStoneType(stone.instance());
	}

	default boolean canTarnish()
	{
		return false;
	}

	default IGVeinTextureType getVeinTextureType()
	{
		return IGVeinTextureType.METALLIC;
	}

	default boolean hasStoneBackdrop()
	{
		return getVeinTextureType().hasStoneBackdrop();
	}

	default LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
	{
		return new LinkedHashSet<MaterialInterface<?>>();
	}

	default Set<MaterialHelper> getOriginMaterials()
	{
		return Collections.emptySet();
	}

	default Item getItem(ItemCategoryFlags flag)
	{
		if(flag==null)
		{
			IGLib.IG_LOGGER.error("Null flag passed to getItem for {}, defaulting to INGOT", getName());
			flag = ItemCategoryFlags.INGOT;
		}

		Item own = IGContent.getItem(flag.getRegistryKey(this));
		if(own!=null) return own;

		ItemStack borrowed = lookupOreDictionary(flag);
		if(!borrowed.isEmpty()) return borrowed.getItem();

		IGLib.IG_LOGGER.error("Attempting to get a missing item: {}", flag.getRegistryKey(this));
		return null;
	}

	default Block getBlock(BlockCategoryFlags flag)
	{
		Block own = IGContent.getBlock(flag.getRegistryKey(this));
		if(own!=null) return own;

		ItemStack borrowed = lookupOreDictionary(flag);
		if(!borrowed.isEmpty()&&borrowed.getItem() instanceof net.minecraft.item.ItemBlock)
			return ((net.minecraft.item.ItemBlock)borrowed.getItem()).getBlock();

		IGLib.IG_LOGGER.error("Attempting to get a missing block: {}", flag.getRegistryKey(this));
		return Blocks.AIR;
	}

	default ItemStack getStack(IFlagType<?> flag, int amount)
	{
		if(flag instanceof ItemCategoryFlags)
		{
			Item item = getItem((ItemCategoryFlags)flag);
			return item==null?ItemStack.EMPTY: new ItemStack(item, amount);
		}
		if(flag instanceof BlockCategoryFlags)
		{
			Block block = getBlock((BlockCategoryFlags)flag);
			return block==Blocks.AIR?ItemStack.EMPTY: new ItemStack(block, amount);
		}
		IGLib.IG_LOGGER.error("{} is not an item or block flag", flag.getName());
		return ItemStack.EMPTY;
	}

	default ItemStack lookupOreDictionary(IFlagType<?> flag)
	{
		String name = flag.getOreDictName(this);
		if(name.isEmpty()||!OreDictionary.doesOreNameExist(name)) return ItemStack.EMPTY;
		List<ItemStack> ores = OreDictionary.getOres(name, false);
		return ores.isEmpty()?ItemStack.EMPTY: ores.get(0).copy();
	}

	default Block getOreBlock(IStoneType stone)
	{
		return IGContent.getBlock(BlockCategoryFlags.ORE_BLOCK.getRegistryKey(this, stone.instance()));
	}

	default ItemStack getOreStack(IStoneType stone, OreRichness richness, MineralWeathering weathering, int amount)
	{
		Block block = getOreBlock(stone);
		if(block==null) return ItemStack.EMPTY;
		return new ItemStack(block, amount, OreBlockMeta.pack(richness, weathering));
	}

	default IGTag getItemTag(IFlagType<?> flag)
	{
		return new IGTag(flag==null?"": flag.getOreDictName(this));
	}

	default IGTag getFluidTag(BlockCategoryFlags type, MaterialHelper... helper)
	{
		StringBuilder name = new StringBuilder(type==null?"fluid": type.getName());
		name.append(getName());
		for(MaterialHelper extra : helper)
			if(extra!=null) name.append(extra.getName());
		return new IGTag(name.toString());
	}

	default IGTag getSlurryTagWith(BlockCategoryFlags type, MaterialHelper... helper)
	{
		return getFluidTag(type, helper);
	}

	Set<IGRecipeStage> logged_recipes = new java.util.HashSet<>();

	MaterialInterface<?> getPrimaryProduct();

	MaterialInterface<?> getSecondaryProduct();

	MaterialInterface<?> getTraceProduct(int index);
}
