/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.recipe;

import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import javax.annotation.Nonnull;

public class IGMoldShapingRecipe extends ShapelessRecipe
{
	public IGMoldShapingRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients)
	{
		super(group, category, result, ingredients);
	}

	public static boolean isMoldBlank(ItemStack stack)
	{
		return stack.getItem() instanceof IGFlagItem flagItem
				&&flagItem.getFlag().getValue()==ItemCategoryFlags.MOLD_BLANK;
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingInput inv)
	{
		NonNullList<ItemStack> remaining = NonNullList.withSize(inv.size(), ItemStack.EMPTY);
		for(int slot = 0; slot < remaining.size(); slot++)
		{
			ItemStack stack = inv.getItem(slot);
			if(stack.isEmpty()||isMoldBlank(stack)) continue;
			remaining.set(slot, stack.copyWithCount(1));
		}
		return remaining;
	}

	@Nonnull
	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return IGRecipeSerializers.MOLD_SHAPING_SERIALIZER.get();
	}
}
