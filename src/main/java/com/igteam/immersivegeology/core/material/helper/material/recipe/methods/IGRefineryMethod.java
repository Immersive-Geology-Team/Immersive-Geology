/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.builders.RefineryRecipeBuilder;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.lib.manual.ManualUtils;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGRefineryMethod extends IGRecipeMethod
{
	public IGRefineryMethod(IGRecipeStage stage)
	{
		super(stage);
	}

	FluidStack output;
	Item catalyst;
	TagKey<Fluid> fluid_input_a, fluid_input_b;
	int input_fluid_a_amount, input_fluid_b_amount;
	String name;
	public IGRefineryMethod(MaterialHelper parent, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parent, stage){});
	}

	public IGRefineryMethod create(String name, TagKey<Fluid> fluid_input_a, int a_amount, TagKey<Fluid> fluid_input_b, int b_amount, Item catalyst, FluidStack output)
	{
		this.output = output;
		this.fluid_input_a = fluid_input_a;
		this.fluid_input_b = fluid_input_b;
		this.input_fluid_a_amount = a_amount;
		this.input_fluid_b_amount = b_amount;
		this.catalyst = catalyst;
		this.name = name;

		return this;
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.SYNTHESIS;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("refinery/synthesis_" + getName());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public ItemStack getIconStack()
	{
		return new ItemStack(IEMultiblocks.REFINERY.getBlock());
	}

	@Override
	public void basicRender(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		renderItemStack(graphics, new ItemStack(catalyst), x, y, mx, my);
		//renderMB(graphics, getIconStack(), x + 24, y, mx, my);

		FluidTank tank = new FluidTank(1000);
		tank.setFluid(output);

		FluidInfoArea fluid = new FluidInfoArea(tank, new Rect2i(x, y, 16, 16), 0,0,0,0,  IGLib.makeTextureLocation("reverberation_furnace"));
		fluid.draw(graphics);
		render_x_space = 48;
	}

	@Override
	public void renderOutput(GuiGraphics graphics, ItemStack iconStack, int methodNameX, int methodNameY, int mx, int my)
	{

	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try
		{
			RefineryRecipeBuilder builder = RefineryRecipeBuilder.builder(output);
			builder.addCatalyst(catalyst);
			builder.addInput(new FluidTagInput(fluid_input_a, input_fluid_a_amount));
			builder.addInput(new FluidTagInput(fluid_input_b, input_fluid_b_amount));
			builder.setEnergy(80);
			builder.setTime(1);
			builder.build(consumer, getLocation());
			return true;
		} catch(Exception e)
		{
			IGLib.IG_LOGGER.error(e.getLocalizedMessage());
			return false;
		}
	}
}
