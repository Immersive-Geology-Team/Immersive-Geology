/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGBasicSmeltingMethod extends IGRecipeMethod
{
	public IGBasicSmeltingMethod(IGRecipeStage stage)
	{
		super(stage);
	}

	private ItemLike input, output;
	private int smeltingTime;
	private float xp;

	public IGBasicSmeltingMethod(MaterialHelper material, IGStageDesignation stage)
	{
		super(new IGRecipeStage(material, stage){});
	}

	public IGBasicSmeltingMethod create(ItemLike input, ItemLike output){
		this.input = input;
		this.output = output;
		this.smeltingTime = 100;
		this.xp = 1;
		return this;
	}

	public IGBasicSmeltingMethod create(IFlagType<?> input, IFlagType<?> output){
		this.input = parentMaterial.getStack(input, 1).getItem();
		this.output = parentMaterial.getStack(output, 1).getItem();
		this.smeltingTime = 100;
		this.xp = 1;
		return this;
	}

	public IGBasicSmeltingMethod create(IFlagType<?> input, IFlagType<?> output, int time){
		this.input = parentMaterial.getStack(input, 1).getItem();
		this.output = parentMaterial.getStack(output, 1).getItem();
		this.smeltingTime = time;
		this.xp = 1;
		return this;
	}

	public IGBasicSmeltingMethod create(IFlagType<?> input, IFlagType<?> output, int time, int xp){
		this.input = parentMaterial.getStack(input, 1).getItem();
		this.output = parentMaterial.getStack(output, 1).getItem();
		this.smeltingTime = time;
		this.xp = xp;
		return this;
	}

	public void setTimeAndXP(int smeltingTime, float xp){
		this.smeltingTime = smeltingTime;
		this.xp = xp;
	}


	@Override
	public ItemStack getIconStack()
	{
		return new ItemStack(Blocks.FURNACE);
	}

	@Override
	public void basicRender(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		renderItemStack(graphics, new ItemStack(input.asItem()), x, y, mx, my);
//		renderMB(graphics, getIconStack(), x + 24, y, mx, my);
//		renderItemStack(graphics, new ItemStack(output.asItem()), x + 48, y, mx,my);
	}

	@Override
	public void renderOutput(GuiGraphics graphics, ItemStack iconStack, int methodNameX, int methodNameY, int mx, int my)
	{

	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.BASIC_SMELTING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL(input.asItem().getDescriptionId() + "_to_" + output.asItem().getDescriptionId() + "_from_blasting");
	}

	@Override
	public String getName()
	{
		return input.asItem().getDescriptionId() + "_to_" + output.asItem().getDescriptionId() + "_from_blasting";
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try
		{
			SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, output, xp, smeltingTime).unlockedBy("has_"+input.asItem().getDescriptionId(), InventoryChangeTrigger.TriggerInstance.hasItems(input)).save(consumer, getLocation());
			return true;
		} catch(Exception exception)
		{
			return false;
		}
	}
}
