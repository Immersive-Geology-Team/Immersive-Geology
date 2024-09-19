/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class RevFurnaceRecipeBuilder extends IEFinishedRecipe<RevFurnaceRecipeBuilder>
{
	protected RevFurnaceRecipeBuilder()
	{
		super(RevFurnaceRecipe.SERIALIZER.get());
	}

	public static RevFurnaceRecipeBuilder builder(Item result)
	{
		return new RevFurnaceRecipeBuilder().addResult(result);
	}

	public static RevFurnaceRecipeBuilder builder(ItemStack result)
	{
		return new RevFurnaceRecipeBuilder().addResult(result);
	}

	public static RevFurnaceRecipeBuilder builder(TagKey<Item> result, int count)
	{
		return new RevFurnaceRecipeBuilder().addResult(new IngredientWithSize(result, count));
	}

	public RevFurnaceRecipeBuilder setWasteAmount(int amount)
	{
		return this.addWriter((jsonObject) -> {
			jsonObject.addProperty("waste", amount);
		});
	}
}
