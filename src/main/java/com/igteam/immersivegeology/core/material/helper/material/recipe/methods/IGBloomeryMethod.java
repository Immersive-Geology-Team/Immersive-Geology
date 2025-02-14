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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.BloomeryRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGBloomeryMethod extends IGRecipeMethod
{
	public IGBloomeryMethod(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parentMaterial, stage){});
	}

	private int time;
	private String name;
	private IngredientWithSize itemInput;
	private ItemStack itemResult;

	public IGBloomeryMethod create(IFlagType<?> input, int inputAmount, MaterialHelper outputMaterial, IFlagType<?> output, int outputAmount, int time){
		this.name = input.getName().toLowerCase() + "_" + parentMaterial.getName().toLowerCase() + "_to_" + output.getName().toLowerCase() + "_" + outputMaterial.getName().toLowerCase();
		this.itemResult = outputMaterial.getStack(output, outputAmount);
		this.itemInput = new IngredientWithSize(parentMaterial.getItemTag(input), inputAmount);
		this.time = time;
		return this;
	}

	public IGBloomeryMethod create(IFlagType<?> input, int inputAmount, IFlagType<?> output, int outputAmount, int time){
		this.name = create_advanced_method_name(input, output);

		MaterialInterface<?> outputMaterial = parentMaterial.getPrimaryProduct();
		this.itemResult = outputMaterial.getStack(output, outputAmount);
		this.itemInput = new IngredientWithSize(parentMaterial.getItemTag(input), inputAmount);
		this.time = time;
		return this;
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.BLOOMERY;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("roasting/bloomery_" + getName());
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public ItemStack getIconStack()
	{
		return IGMultiblockProvider.BLOOMERY.iconStack();
	}

	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		renderItemStack(graphics, itemInput.getRandomizedExampleStack(0), x, y, mx, my);
//		renderMB(graphics, getIconStack(), x + 24, y, mx, my);
//		renderItemStack(graphics, itemResult, x + 48, y, mx,my);
	}

	@Override
	public void renderDisplayStack(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{

	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		IGLib.IG_LOGGER.info("Attempting Build for Bloomery Method {}", name);
		try {
			BloomeryRecipeBuilder builder = BloomeryRecipeBuilder.builder(itemResult);
			builder.addInput(itemInput);
			builder.setTime(time);
			builder.build(consumer, getLocation());
			return true;
		} catch(Exception e)
		{
			IGLib.IG_LOGGER.error("Exception Building Immersive Geology Crude Bloomery Recipe: {}", e.getMessage());
			return false;
		}
	}
}
