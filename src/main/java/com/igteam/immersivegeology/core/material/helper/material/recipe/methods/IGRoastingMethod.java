/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.RevFurnaceRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGRoastingMethod extends IGRecipeMethod
{
	public IGRoastingMethod(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parentMaterial, stage){});
	}

	private ItemStack itemResult;
	private IngredientWithSize itemInput;
	private TagKey<Item> itemTag;
	private int time, waste_amount;
	private String name;

	public void create(String name, TagKey<Item> itemInput, int inputAmount, ItemStack output, int time, int waste_amount){
		this.name = name;

		this.itemTag = itemInput;
		this.itemResult = output;
		this.itemInput = new IngredientWithSize(itemInput, inputAmount);

		this.time = time;
		this.waste_amount = waste_amount;
	}

	public void create(IFlagType<?> input, int inputAmount, IFlagType<?> output, int outputAmount, int time, int waste_amount){
		this.name = create_basic_method_name(input, output);

		this.itemTag = parentMaterial.getItemTag(input);
		this.itemResult = parentMaterial.getStack(output, outputAmount);
		this.itemInput = new IngredientWithSize(itemTag, inputAmount);

		this.time = time;
		this.waste_amount = waste_amount;
	}

	@Override
	public @NotNull RecipeMethod getMethod()
	{
		return RecipeMethod.ROASTING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("roasting/roast_" + getName());
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try {
			RevFurnaceRecipeBuilder builder = RevFurnaceRecipeBuilder.builder(itemResult);
			builder.addInput(itemInput);
			builder.setWasteAmount(waste_amount);
			builder.setTime(time);
			builder.build(consumer, getLocation());
			return true;
		} catch(Exception e)
		{
			IGLib.IG_LOGGER.error("Exception Building Immersive Geology Roasting Recipe: {}", e.getMessage());
		}
		return false;
	}
}
