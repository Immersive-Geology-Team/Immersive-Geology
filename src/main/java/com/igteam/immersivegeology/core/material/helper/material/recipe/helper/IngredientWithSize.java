package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

import net.minecraft.item.ItemStack;

public class IngredientWithSize
{
	private final Object ingredient;
	private final int count;

	public IngredientWithSize(Object ingredient, int count)
	{
		this.ingredient = ingredient;
		this.count = count;
	}

	public static IngredientWithSize of(ItemStack stack)
	{
		return new IngredientWithSize(stack, stack==null?0: stack.getCount());
	}

	public static IngredientWithSize of(Object ingredient)
	{
		return new IngredientWithSize(ingredient, 1);
	}

	public Object getIngredient()
	{
		return ingredient;
	}

	public int getCount()
	{
		return count;
	}
}
