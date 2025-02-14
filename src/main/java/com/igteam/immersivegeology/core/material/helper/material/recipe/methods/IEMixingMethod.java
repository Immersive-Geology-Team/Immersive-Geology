/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.builders.MixerRecipeBuilder;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IEMixingMethod extends IGRecipeMethod
{
	private int energy, input_fluid_amount;
	private FluidStack fluid_result;
	private TagKey<Fluid> input_fluid;
	private String name;
	private TagKey<Item> input;

	public IEMixingMethod(MaterialHelper parent, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parent, stage){});
	}

	public IEMixingMethod create(IFlagType<?> input, TagKey<Fluid> input_fluid, int input_fluid_amount, Fluid slurryWith, int fluid_out_amount)
	{
		this.name = create_basic_method_name(BlockCategoryFlags.FLUID);
		this.fluid_result = new FluidStack(slurryWith, fluid_out_amount);
		this.input_fluid = input_fluid;
		this.input = parentMaterial.getItemTag(input);
		this.input_fluid_amount = input_fluid_amount;
		this.energy = 1600;
		return this;
	}

	public IEMixingMethod create(TagKey<Item> input, TagKey<Fluid> input_fluid, int input_fluid_amount, int fluid_out_amount) {
		this.fluid_result = new FluidStack(parentMaterial.getFluid(BlockCategoryFlags.FLUID), fluid_out_amount);
		this.input_fluid = input_fluid;
		this.name = create_basic_method_name(BlockCategoryFlags.FLUID);
		this.input = input;
		this.input_fluid_amount = input_fluid_amount;
		this.energy = 1600;
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
		return toRL("mixer/mix_" + getName());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public ItemStack getIconStack()
	{
		return new ItemStack(IEMultiblocks.MIXER.getBlock().asItem());
	}

	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		Ingredient ingredient = Ingredient.of(input);
		renderItemStack(graphics, ingredient.getItems()[0], x, y, mx, my);
//		renderMB(graphics, getIconStack(), x + 24, y, mx, my);
//		renderFluidStack(graphics, fluid_result.getFluid(), x+48,y,mx,my);
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
			MixerRecipeBuilder builder = MixerRecipeBuilder.builder(fluid_result);
			builder.addFluidTag(input_fluid, input_fluid_amount);
			builder.addInput(input);
			builder.setEnergy(energy);
			builder.build(consumer, getLocation());
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}


}
