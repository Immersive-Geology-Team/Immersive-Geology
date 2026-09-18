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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import javax.annotation.Nonnull;

public class IGMoldShapingRecipe extends ShapelessRecipe
{
	public IGMoldShapingRecipe(ResourceLocation id, String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients)
	{
		super(id, group, category, result, ingredients);
	}

	public static boolean isMoldBlank(ItemStack stack)
	{
		return stack.getItem() instanceof IGFlagItem flagItem
				&&flagItem.getFlag().getValue()==ItemCategoryFlags.MOLD_BLANK;
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingContainer inv)
	{
		NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
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
