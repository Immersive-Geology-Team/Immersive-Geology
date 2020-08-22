package com.igteam.immersivegeology.common.items;

import blusunrize.immersiveengineering.api.tool.ITool;
import blusunrize.immersiveengineering.common.items.IEItemInterfaces.IColouredItem;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Set;

public abstract class IGModularToolItem extends IGBaseItem implements ITool, IColouredItem
{
	public IGModularToolItem(String name)
	{
		super(name);
	}

	public static Material getToolMaterial(ItemStack stack, boolean head)
	{
		return EnumMaterials.filterByName(ItemNBTHelper.getString(stack, head?"head_material": "handle_material")).material;
	}

	@Override
	public boolean isTool(ItemStack itemStack)
	{
		return !itemStack.getToolTypes().isEmpty();
	}

	@Override
	public boolean hasCustomItemColours()
	{
		return true;
	}

	@Override
	public int getColourForIEItem(ItemStack stack, int pass)
	{
		//0 for head, 1 for handle
		return getColorCache(stack, pass==0);
	}

	//true for head, false for handle
	public int getColorCache(ItemStack stack, boolean head)
	{
		String tagName = head?"head_color": "handle_color";
		//Adds quick access to the color, greatly improves performance
		if(!ItemNBTHelper.hasKey(stack, tagName))
			stack.getOrCreateTag().putInt(tagName, getToolMaterial(stack, head).getColor(0));
		return ItemNBTHelper.getInt(stack, tagName);
	}

	@Override
	public Set<ToolType> getToolTypes(ItemStack stack)
	{
		ImmutableSet<ToolType> toolTypes = null;
		return toolTypes;
	}

	@Override
	public int getHarvestLevel(ItemStack p_getHarvestLevel_1_, ToolType p_getHarvestLevel_2_, @Nullable PlayerEntity p_getHarvestLevel_3_, @Nullable BlockState p_getHarvestLevel_4_)
	{
		return super.getHarvestLevel(p_getHarvestLevel_1_, p_getHarvestLevel_2_, p_getHarvestLevel_3_, p_getHarvestLevel_4_);
	}
}
