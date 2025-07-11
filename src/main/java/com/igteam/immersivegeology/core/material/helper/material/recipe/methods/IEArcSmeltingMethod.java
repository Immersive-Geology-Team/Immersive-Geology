/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.ArcFurnaceRecipeBuilder;
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
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class IEArcSmeltingMethod extends IGRecipeMethod
{
	public IEArcSmeltingMethod(IGRecipeStage stage)
	{
		super(stage);
	}

	public IEArcSmeltingMethod(MaterialHelper material, IGStageDesignation stage)
	{
		super(new IGRecipeStage(material, stage){});
	}

	public IEArcSmeltingMethod create(String method_name, TagKey<Item> input, int inputAmount, ItemStack output, ItemStack iSlag, IngredientWithSize... additives){
		this.input = new IngredientWithSize(input, inputAmount);
		this.output = output;
		this.slag = iSlag;
		this.additives = asList(additives);
		this.method_name = method_name;
		return this;
	}



	public IEArcSmeltingMethod create(String method_name, Item input, int inputAmount, ItemStack output, ItemStack iSlag, IngredientWithSize... additives)
	{
		this.input = new IngredientWithSize(Ingredient.of(input), inputAmount);
		this.output = output;
		this.slag = iSlag;
		this.additives = asList(additives);
		this.method_name = method_name;
		return this;
	}

	public IEArcSmeltingMethod create(String method_name, TagKey<Item> input, int inputAmount, ItemStack output, ItemStack iSlag){
		this.input = new IngredientWithSize(input, inputAmount);
		this.output = output;
		this.slag = iSlag;
		this.additives = List.of();
		this.method_name = method_name;
		return this;
	}

	public IEArcSmeltingMethod create(IFlagType<?> input_form, int inputAmount, IFlagType<?> output_form, int output_amount, int slag_amount){
		this.input = new IngredientWithSize(parentMaterial.getItemTag(input_form), inputAmount);
		this.output = parentMaterial.getStack(output_form, output_amount);
		this.slag = slag_amount == 0 ? ItemStack.EMPTY : parentMaterial.getStack(ItemCategoryFlags.SLAG, slag_amount);
		this.additives = List.of();
		this.method_name = create_advanced_method_name(input_form, output_form);
		this.setTimeAndEnergy(400, 204800);
		return this;
	}


	public IEArcSmeltingMethod create(IFlagType<?> input_form, int inputAmount, IFlagType<?> output_form, int output_amount, int slag_amount, IngredientWithSize... additives){
		this.input = new IngredientWithSize(parentMaterial.getItemTag(input_form), inputAmount);
		this.output = parentMaterial.getStack(output_form, output_amount);
		this.slag = slag_amount == 0 ? ItemStack.EMPTY : parentMaterial.getStack(ItemCategoryFlags.SLAG, slag_amount);
		this.additives = List.of(additives);
		this.method_name = create_advanced_method_name(input_form, output_form);
		this.setTimeAndEnergy(400, 204800);
		return this;
	}

	public IEArcSmeltingMethod create(MaterialHelper input_mat, IFlagType<?> input_form, int inputAmount, IFlagType<?> output_form, int output_amount, int slag_amount, IngredientWithSize... additives)
	{
		this.input = new IngredientWithSize(input_mat.getItemTag(input_form), inputAmount);
		this.output = parentMaterial.getStack(output_form, output_amount);
		this.slag = slag_amount == 0 ? ItemStack.EMPTY : new ItemStack(Ingredients.SLAG.asItem(), slag_amount);
		this.additives = List.of(additives);
		this.method_name = create_advanced_method_name(input_form, output_form);
		return this;
	}

	public IEArcSmeltingMethod addExtras(TagKey<Item> extra, Float chance)
	{
		secondaries.add(Pair.of(extra, chance));
		return this;
	}

	public IEArcSmeltingMethod setTimeAndEnergy(int time, int energy){
		this.time = time;
		this.energy = energy;
		return this;
	}

	private String method_name;
	private IngredientWithSize input;

	private ItemStack slag, output;
	private List<IngredientWithSize> additives;
	int energy, time;

	List<Pair<TagKey<Item>, Float>> secondaries = new ArrayList<>();
	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.ARC_SMELTING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("arc_smelting/arc_" + getName());
	}

	@Override
	public String getName()
	{
		return method_name;
	}

	@Override
	public ItemStack getIconStack()
	{
		return new ItemStack(IEMultiblocks.ARC_FURNACE.getBlock().asItem());
	}

	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		renderItemStack(graphics, input.getRandomizedExampleStack(0), x + 25, y + 2, mx, my);

		for(IngredientWithSize ingredient : additives)
		{
			renderItemStack(graphics, ingredient.getRandomizedExampleStack(0), (x + 7), (y + 2) + (18 * additives.indexOf(ingredient)), mx, my);
		}
		renderItemStack(graphics, output, x + 59,y + 2,mx,my);
		if(!slag.isEmpty()) renderItemStack(graphics, slag, x + 59,y + 20,mx,my);
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
			ArcFurnaceRecipeBuilder builder = ArcFurnaceRecipeBuilder.builder(output);
			if(!slag.isEmpty()) builder.addSlag(slag);
			builder.setEnergy(energy);
			builder.setTime(time);
			builder.addIngredient("input", input);
			additives.forEach(builder::addMultiInput);

			for(Pair<TagKey<Item>, Float> entry : secondaries)
			{
				builder.addSecondary(entry.getFirst(), entry.getSecond());
			}

			builder.build(consumer, getLocation());
			return true;
		} catch(Exception e)
		{
			IGLib.IG_LOGGER.error(e.getLocalizedMessage());
			return false;
		}
	}
}
