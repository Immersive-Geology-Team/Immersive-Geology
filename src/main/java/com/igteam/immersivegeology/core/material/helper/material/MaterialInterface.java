package com.igteam.immersivegeology.core.material.helper.material;

import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.IGTag;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Set;

public interface MaterialInterface<T extends GeologyMaterial>
{
	T instance();

	default String getName()
	{
		return instance().getName();
	}

	default Set<IFlagType<?>> getFlags()
	{
		return instance().getFlags();
	}

	default boolean hasFlag(IFlagType<?> category)
	{
		return instance().hasFlag(category);
	}

	default int getColor(IFlagType<?> flag, int secondaryColors)
	{
		return instance().getColor(flag, secondaryColors);
	}

	default boolean canTarnish()
	{
		return instance().canTarnish();
	}

	default Item getItem(ItemCategoryFlags flag)
	{
		return instance().getItem(flag);
	}

	default Block getBlock(BlockCategoryFlags flag)
	{
		return instance().getBlock(flag);
	}

	default ItemStack getStack(IFlagType<?> flag)
	{
		return instance().getStack(flag, 1);
	}

	default ItemStack getStack(IFlagType<?> flag, int amount)
	{
		return instance().getStack(flag, amount);
	}

	default IGTag getItemTag(IFlagType<?> flag)
	{
		return instance().getItemTag(flag);
	}

	default IGTag getFluidTag()
	{
		return instance().getFluidTag();
	}

	default IGTag getFluidTag(BlockCategoryFlags flag)
	{
		return instance().getFluidTag(flag);
	}

	default IGTag getFluidTag(BlockCategoryFlags flag, MaterialInterface<?>... materials)
	{
		return instance().getFluidTag(flag, materials);
	}

	default IGTag getSlurryTagWith(BlockCategoryFlags flag, MaterialInterface<?>... materials)
	{
		return getFluidTag(flag, materials);
	}

	default net.minecraftforge.fluids.Fluid getFluid(BlockCategoryFlags flag)
	{
		return null;
	}

	default net.minecraftforge.fluids.FluidStack getSlurryWith(MaterialInterface<?> other, int amount)
	{
		return null;
	}

	default Block getOreBlock(IStoneType stone)
	{
		return instance().getOreBlock(stone);
	}

	default ItemStack getOreStack(IStoneType stone, OreRichness richness, MineralWeathering weathering, int amount)
	{
		return instance().getOreStack(stone, richness, weathering, amount);
	}
}
