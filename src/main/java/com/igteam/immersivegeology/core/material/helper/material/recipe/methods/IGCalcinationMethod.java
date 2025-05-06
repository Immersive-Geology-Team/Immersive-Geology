/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.RotaryKilnRecipeBuilder;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGCalcinationMethod extends IGRecipeMethod
{
	private int time, heat;

	private IngredientWithSize input;
	private ItemStack output;
	private String name;

	public IGCalcinationMethod(MaterialHelper parent, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parent, stage){});
	}

	public IGCalcinationMethod create(String name, ItemStack output, TagKey<Item> inputTag, int itemAmount, int time, int heat){
		this.name = name;
		this.output = output;
		this.input = new IngredientWithSize(inputTag, itemAmount);
		this.time = time;
		this.heat = heat;
		return this;
	}

	public IGCalcinationMethod create(IFlagType<?> output_form, IFlagType<?> input_form, int itemAmount, int time, int heat){
		this.name = create_advanced_method_name(input_form, output_form);
		this.output = parentMaterial.getStack(output_form, 1);
		this.input = new IngredientWithSize(parentMaterial.getItemTag(input_form), itemAmount);
		this.time = time;
		this.heat = heat;
		return this;
	}

	public IGCalcinationMethod create(IFlagType<?> input_form, IFlagType<?> output_form, MaterialHelper output_material, int itemAmount, int time, int heat){
		this.name = create_advanced_method_name(input_form, output_form);
		this.output = output_material.getStack(output_form, 1);
		this.input = new IngredientWithSize(parentMaterial.getItemTag(input_form), itemAmount);
		this.time = time;
		this.heat = heat;
		return this;
	}

	public IGCalcinationMethod create(IFlagType<?> input_form, int input_amount, IFlagType<?> output_form, MaterialHelper output_material, int output_amount, int time, int heat){
		this.name = create_advanced_method_name(input_form, output_form);
		this.output = output_material.getStack(output_form, output_amount);
		this.input = new IngredientWithSize(parentMaterial.getItemTag(input_form), input_amount);
		this.time = time;
		this.heat = heat;
		return this;
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.CALCINATION;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("calcination/decompose_" + getName());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public ItemStack getIconStack()
	{
		return IGMultiblockProvider.ROTARYKILN.iconStack();
	}

	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		renderItemStack(graphics, input.getRandomizedExampleStack(0), x + 25, y + 2, mx, my);
		renderItemStack(graphics, output, x + 59, y + 2, mx, my);
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
			RotaryKilnRecipeBuilder builder = RotaryKilnRecipeBuilder.builder(output);
			builder.addInput(input);
			builder.setTime(time);
			builder.setHeat(heat);
			builder.build(consumer, getLocation());
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
