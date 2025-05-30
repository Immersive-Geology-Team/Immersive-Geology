/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.IndustrialSluiceRecipeBuilder;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;

public class IGTrommelMethod extends IGRecipeMethod
{
	private int energy, time, water;
	private IngredientWithSize input;
	private ItemStack output;
	private NonNullList<StackWithChance> byproducts;
	private String name;

	public IGTrommelMethod(MaterialHelper parent, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parent, stage){});
	}

	public void create(String name, TagKey<Item> input_item, int input_amount, ItemStack primary_output_item, int water_used, int energy_used, int time_taken, StackWithChance... byproducts) {
		this.name = name;
		this.energy = energy_used;
		this.time = time_taken;
		this.water = water_used;
		this.input = new IngredientWithSize(input_item, input_amount);
		this.output = primary_output_item;

		this.byproducts = NonNullList.create();
		this.byproducts.addAll(Arrays.asList(byproducts));
	}

	@NotNull
	@Override
	public IGRecipeMethod.RecipeMethod getMethod()
	{
		return RecipeMethod.PELLETIZE;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("trommel/wash_" + getName());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public ItemStack getIconStack()
	{
		return IGMultiblockProvider.TROMMEL.iconStack();
	}

	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{

	}

	@Override
	public void renderDisplayStack(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{

	}

	@Override
	public void renderFinalStack(GuiGraphics graphics, ManualScreen screen, int baseX, int baseY, int mx, int my)
	{

	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try
		{
			IndustrialSluiceRecipeBuilder builder = IndustrialSluiceRecipeBuilder.builder(output);
			builder.addInput(input);
			builder.setWater(water);
			builder.setEnergy(energy);
			builder.setTime(time);
			builder.setByproducts(byproducts);
			builder.build(consumer, getLocation());
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
