/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.SqueezerRecipeBuilder;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.lib.manual.ManualUtils;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class IESqueezingMethod extends IGRecipeMethod
{
	public IESqueezingMethod(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parentMaterial, stage) {});
	}

	private ItemStack output;
	private IngredientWithSize input;
	private int energy, time;
	private FluidStack fluidResult;
	private String name;

	public IESqueezingMethod create(String method_name, IngredientWithSize input, ItemStack output, FluidStack fluidResult, int energy, int time){
		this.input = input;
		this.output = output;
		this.name = method_name;
		this.energy = energy;
		this.time = time;
		this.fluidResult = fluidResult;
		return this;
	}

	public IESqueezingMethod create(IFlagType<?> input_form, IFlagType<?> output_form, FluidStack fluidResult, int energy, int time){
		this.input = IngredientWithSize.of(parentMaterial.getStack(input_form, 1));
		this.output = parentMaterial.getStack(output_form, 1);
		this.name = create_advanced_method_name(input_form, output_form);
		this.energy = energy;
		this.time = time;
		this.fluidResult = fluidResult;
		return this;
	}

	public IESqueezingMethod create(IFlagType<?> input_form, MaterialHelper output_material, IFlagType<?> output_form, int output_amount, FluidStack fluidResult, int energy, int time){
		this.input = IngredientWithSize.of(parentMaterial.getStack(input_form, 1));
		this.output = output_material.getStack(output_form, output_amount);
		this.name = create_advanced_method_name(input_form, output_form);
		this.energy = energy;
		this.time = time;
		this.fluidResult = fluidResult;
		return this;
	}

	public IESqueezingMethod create(MaterialHelper input_material, IFlagType<?> input_form, int input_amount, MaterialHelper output_material, IFlagType<?> output_form, int output_amount, FluidStack fluidResult, int energy, int time){
		this.input = IngredientWithSize.of(input_material.getStack(input_form, input_amount));
		this.output = output_material.getStack(output_form, output_amount);
		this.name = create_advanced_method_name(input_form, output_form);
		this.energy = energy;
		this.time = time;
		this.fluidResult = fluidResult;
		return this;
	}

	public IESqueezingMethod create(IFlagType<?> input_form, MaterialHelper output_material, IFlagType<?> output_form, FluidStack fluidResult, int energy, int time){
		this.input = IngredientWithSize.of(parentMaterial.getStack(input_form, 1));
		this.output = output_material.getStack(output_form, 1);
		this.name = create_advanced_method_name(input_form, output_form);
		this.energy = energy;
		this.time = time;
		this.fluidResult = fluidResult;
		return this;
	}

	public IESqueezingMethod create(MaterialHelper input_material, IFlagType<?> input_form, MaterialHelper output_material, IFlagType<?> output_form, FluidStack fluidResult, int energy, int time){
		this.input = IngredientWithSize.of(input_material.getStack(input_form, 1));
		this.output = output_material.getStack(output_form, 1);
		this.name = create_advanced_method_name(input_form, output_form);
		this.energy = energy;
		this.time = time;
		this.fluidResult = fluidResult;
		return this;
	}

	public IESqueezingMethod create(String method_name, ItemStack input, ItemStack output,FluidStack fluidResult, int energy, int time){
		this.input = IngredientWithSize.of(input);
		this.output = output;
		this.name = method_name;
		this.energy = energy;
		this.time = time;
		this.fluidResult = fluidResult;
		return this;
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.CRUSHING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("crushing/crush_" + Objects.requireNonNull(getName()));
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public ItemStack getIconStack()
	{
		return IEMultiblocks.SQUEEZER.getBlock().asItem().getDefaultInstance();
	}

	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
//		renderItemStack(graphics, input.getRandomizedExampleStack(0), x + 25, y + 11, mx, my);
//		renderItemStack(graphics, output, x + 59, y + 2, mx,my);
//		if(secondary != null && !secondary.hasNoMatchingItems())
//		{
//			renderItemStack(graphics, secondary.getRandomizedExampleStack(0), x + 59, y + 20, mx,my);
//			ManualUtils.drawSplitString(graphics, screen.getManual().fontRenderer(), List.of(String.format("%.1f", (chance * 100)) + "%"), x+79, y+24, 0xff777777);
//		}
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
			SqueezerRecipeBuilder.builder()
					.addResult(output)
					.addFluid(fluidResult)
					.setTime(time)
					.setEnergy(energy)
					.addInput(input)
					.build(consumer, getLocation());

			return true;
		}
		catch(Exception e)
		{
			IGLib.IG_LOGGER.error("Exception Building IG Crushing Recipe: {}", e.getMessage());
			return false;
		}
	}
}
