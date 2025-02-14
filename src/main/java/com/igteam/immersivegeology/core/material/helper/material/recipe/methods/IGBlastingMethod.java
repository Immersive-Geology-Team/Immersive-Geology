/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.builders.BlastFurnaceRecipeBuilder;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGBlastingMethod extends IGRecipeMethod
{
	private TagKey<Item> input;
	private ItemStack output;
	private String name;
	private ItemStack slag;
	private int time;
	public IGBlastingMethod(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parentMaterial, stage){});
	}

	public IGBlastingMethod create(String method_name, TagKey<Item> input, ItemStack output){
		this.input = input;
		this.output = output;
		this.name = method_name;
		this.slag = new ItemStack(Ingredients.SLAG);
		this.time = 200;
		return this;
	}

	public IGBlastingMethod create(String method_name, TagKey<Item> input, ItemStack output, ItemStack slag){
		this.input = input;
		this.output = output;
		this.name = method_name;
		this.slag = slag;
		this.time = 200;
		return this;
	}

	public IGBlastingMethod create(String method_name, TagKey<Item> input, ItemStack output, int time){
		this.input = input;
		this.output = output;
		this.name = method_name;
		this.slag = new ItemStack(Ingredients.SLAG);
		this.time = time;
		return this;
	}

	public IGBlastingMethod create(String method_name, TagKey<Item> input, ItemStack output, ItemStack slag , int time){
		this.input = input;
		this.output = output;
		this.name = method_name;
		this.slag = slag;
		this.time = time;
		return this;
	}


	public IGBlastingMethod create(IFlagType<?> input_form, IFlagType<?> output_form, int time){
		this.input = parentMaterial.getItemTag(input_form);
		this.output = parentMaterial.getStack(output_form, 1);
		this.name = create_advanced_method_name(input_form, output_form);
		this.slag = parentMaterial.getStack(ItemCategoryFlags.SLAG, 1);
		this.time = time;
		return this;
	}

	public IGBlastingMethod create(IFlagType<?> input_form, IFlagType<?> output_form, IFlagType<?> slag_form, int time){
		this.input = parentMaterial.getItemTag(input_form);
		this.output = parentMaterial.getStack(output_form, 1);
		this.name = create_advanced_method_name(input_form, output_form);
		this.slag = parentMaterial.getStack(slag_form, 1);
		this.time = time;
		return this;
	}

	public IGBlastingMethod create(IFlagType<?> input_form, IFlagType<?> output_form, ItemStack slag, int time){
		this.input = parentMaterial.getItemTag(input_form);
		this.output = parentMaterial.getStack(output_form, 1);
		this.name = create_advanced_method_name(input_form, output_form);
		this.slag = slag;
		this.time = time;
		return this;
	}

	public IGBlastingMethod create(MaterialHelper input_material, IFlagType<?> input_form, MaterialHelper output_material,  IFlagType<?> output_form, MaterialHelper slag_material, IFlagType<?> slag_form, int time){
		this.input = input_material.getItemTag(input_form);
		this.output = output_material.getStack(output_form, 1);
		this.name = create_advanced_method_name(input_form, output_form);
		this.slag = slag_material.getStack(slag_form, 1);
		this.time = time;
		return this;
	}

	public IGBlastingMethod create(MaterialHelper input_material, IFlagType<?> input_form, MaterialHelper output_material, IFlagType<?> output_form, ItemStack slag, int time){
		this.input = input_material.getItemTag(input_form);
		this.output = output_material.getStack(output_form, 1);
		this.name = create_advanced_method_name(input_form, output_form);
		this.slag = slag;
		this.time = time;
		return this;
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.BLASTING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("blasting/blast_" + getName());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public ItemStack getIconStack()
	{
		return new ItemStack(IEMultiblocks.BLAST_FURNACE.getBlock());
	}

	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		Ingredient ingredient = Ingredient.of(input);
		ItemStack inputStack = ingredient.getItems()[0];
		renderItemStack(graphics, inputStack, x, y, mx, my);
//		renderMB(graphics, getIconStack(), x + 24, y, mx, my);
//		renderItemStack(graphics, output, x + 48, y, mx,my);
	}

	@Override
	public void renderDisplayStack(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		Ingredient ingredient = Ingredient.of(input);
		renderItemStack(graphics, ingredient.getItems()[0], x, y, mx, my);
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try {
			BlastFurnaceRecipeBuilder builder = BlastFurnaceRecipeBuilder.builder(output);
			builder.addInput(input);
			builder.addSlag(slag);
			builder.setTime(time);
			builder.build(consumer, getLocation());
			return true;
		}
		catch(Exception e)
		{
			IGLib.IG_LOGGER.info("Failed To build due to {}", e.getMessage());
			return false;
		}
	}
}
