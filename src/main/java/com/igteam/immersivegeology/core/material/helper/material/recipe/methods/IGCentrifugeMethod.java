/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.BallmillRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.CentrifugeRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGCentrifugeMethod extends IGRecipeMethod
{
	private int energy, time;

	private FluidTagInput input;
	private ItemStack output;
	private FluidStack primary_out, secondary_out;
	private String name;

	public IGCentrifugeMethod(MaterialHelper parent, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parent, stage){});
	}

	public IGCentrifugeMethod create(TagKey<Fluid> input_fluid_tag, int input_amount, MaterialInterface<?> output_material, IFlagType<?> item_output_form, int item_output_amount, Fluid primary_fluid_output, int primary_fluid_amount, Fluid secondary_fluid_output, int secondary_fluid_amount, int time, int energy) {
		return create(input_fluid_tag, input_amount, output_material.instance(), item_output_form, item_output_amount, primary_fluid_output, primary_fluid_amount, secondary_fluid_output, secondary_fluid_amount, time, energy);
	}

	public IGCentrifugeMethod create(TagKey<Fluid> input_fluid_tag, int input_amount, MaterialHelper output_material, IFlagType<?> item_output_form, int item_output_amount, Fluid primary_fluid_output, int primary_fluid_amount, Fluid secondary_fluid_output, int secondary_fluid_amount, int time, int energy) {
		Item item = item_output_form instanceof ItemCategoryFlags ? output_material.getItem((ItemCategoryFlags) item_output_form) : output_material.getItem((BlockCategoryFlags) item_output_form);
		return create(input_fluid_tag, input_amount, output_material, item, item_output_amount, primary_fluid_output, primary_fluid_amount, secondary_fluid_output, secondary_fluid_amount, time, energy);
	}

	public IGCentrifugeMethod create(TagKey<Fluid> input_fluid_tag, int input_amount, MaterialHelper output_material, Item output_item, int item_output_amount, Fluid primary_fluid_output, int primary_fluid_amount, Fluid secondary_fluid_output, int secondary_fluid_amount, int time, int energy) {

		String tag_name = input_fluid_tag.toString().toLowerCase();
		String serialized_tag_name = tag_name.substring(tag_name.lastIndexOf(":") + 1, tag_name.lastIndexOf("]"));

		this.name = serialized_tag_name + "_to_" + output_material.getProductionMaterial().getName() + "_centrifuge";
		this.input = new FluidTagInput(input_fluid_tag, input_amount);
		this.output = new ItemStack(output_item, item_output_amount);
		if (primary_fluid_output == null)
		{
			this.primary_out = FluidStack.EMPTY;
		}
		else
		{
			this.primary_out = new FluidStack(primary_fluid_output, primary_fluid_amount);
		}
		if (secondary_fluid_output == null)
		{
			this.secondary_out = FluidStack.EMPTY;
		}
		else
		{
			this.secondary_out = new FluidStack(secondary_fluid_output, secondary_fluid_amount);
		}
		this.time = time;
		this.energy = energy;

		return this;
	}

	@NotNull
	@Override
	public IGRecipeMethod.RecipeMethod getMethod()
	{
		return RecipeMethod.REFINING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("centrifuge/spin_" + getName());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public ItemStack getIconStack()
	{
		return IGMultiblockProvider.CENTRIFUGE.iconStack();
	}

	@Override
	public void basicRender(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{

	}

	@Override
	public void renderOutput(GuiGraphics graphics, ItemStack iconStack, int methodNameX, int methodNameY, int mx, int my)
	{

	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		IGLib.IG_LOGGER.info("Attempting to build Centrifuge Recipe");
		try
		{
			CentrifugeRecipeBuilder builder = CentrifugeRecipeBuilder.builder(output, input, energy, time, primary_out, secondary_out);
			builder.build(consumer, getLocation());
			IGLib.IG_LOGGER.info("Successful");
			return true;
		}
		catch(Exception e)
		{
			IGLib.IG_LOGGER.info("Failed Recipe for {}: {}", getName(), e.getMessage());
			return false;
		}
	}
}
