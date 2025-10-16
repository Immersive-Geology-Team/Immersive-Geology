/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.BasicChemicalRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.ChemicalRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class IGChemicalMethod extends IGRecipeMethod
{
	private ItemStack itemOutput;
	private FluidStack fluidOutput;
	private IngredientWithSize itemIn;
	private FluidTagInput fluidInA, fluidInB, fluidInC;
	private int energy, time;
	private String name;

	public IGChemicalMethod(MaterialHelper material, IGStageDesignation stage)
	{
		super(new IGRecipeStage(material, stage){});
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.CHEMICAL;
	}

	public IGChemicalMethod create(String name, ItemStack itemOutput, FluidStack fluidOutput, IngredientWithSize itemIn, FluidTagInput fluidInA, FluidTagInput fluidInB, FluidTagInput fluidInC, int time, int energy){
		this.name = name;
		this.itemOutput = itemOutput;
		this.fluidOutput = fluidOutput;
		this.itemIn = itemIn;
		this.fluidInA = fluidInA;
		this.fluidInB = fluidInB;
		this.fluidInC = fluidInC;
		this.energy = energy;
		this.time = time;
		return this;
	}

	public IGChemicalMethod create(IFlagType<?> inputFlag, IFlagType<?> outputFlag, ItemStack itemOutput, FluidStack fluidOutput, IngredientWithSize itemIn, FluidTagInput fluidInA, FluidTagInput fluidInB, FluidTagInput fluidInC, int time, int energy){
		this.name = create_basic_method_name(inputFlag, outputFlag);
		this.itemOutput = itemOutput;
		this.fluidOutput = fluidOutput;
		this.itemIn = itemIn;
		this.fluidInA = fluidInA;
		this.fluidInB = fluidInB;
		this.fluidInC = fluidInC;
		this.energy = energy;
		this.time = time;
		return this;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("chemical_reactor/leach_" + getName());
	}

	@Override
	public ItemStack getIconStack()
	{
		return IGMultiblockProvider.CHEMICAL_REACTOR.iconStack();
	}

	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		if(fluidInA != null && (fluidInA.getRandomizedExampleStack(0) != null)) renderFluidStack(graphics, fluidInA.getRandomizedExampleStack(0), x + 15,y + 3,16,16,mx,my);
		if(fluidInB != null && (fluidInB.getRandomizedExampleStack(0) != null)) renderFluidStack(graphics, fluidInB.getRandomizedExampleStack(0), x + 15,y + 23,16,16,mx,my);
		if(fluidInC != null && (fluidInC.getRandomizedExampleStack(0) != null)) renderFluidStack(graphics, fluidInC.getRandomizedExampleStack(0), x + 15,y + 43,16,16,mx,my);

		if(itemIn != null && !itemIn.hasNoMatchingItems()) renderItemStack(graphics, itemIn.getRandomizedExampleStack(0), x + 34, y + 23, mx, my);

		if(itemOutput != null && !itemOutput.isEmpty()) renderItemStack(graphics, itemOutput, x + 70, y + 14, mx, my);
		if(fluidOutput != null && !fluidOutput.isEmpty()) renderFluidStack(graphics, fluidOutput, x + 70,y + 33,16,16,mx,my);
	}

	@Override
	public void renderFinalStack(GuiGraphics graphics, ManualScreen screen, int baseX, int baseY, int mx, int my)
	{

	}

	@Override
	public void renderDisplayStack(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		if(itemIn!=null && !itemIn.hasNoMatchingItems())
		{
			renderItemStack(graphics, itemIn.getRandomizedExampleStack(0), x, y, mx, my);
		}
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try
		{
			ChemicalRecipeBuilder builder = ChemicalRecipeBuilder.builder(itemOutput, fluidOutput, itemIn, fluidInA, fluidInB, fluidInC);
			builder.setEnergy(energy);
			builder.setTime(time);
			builder.build(consumer, getLocation());

			int nullCount = 0;
			if (fluidInA == null) nullCount++;
			if (fluidInB == null) nullCount++;
			if (fluidInC == null) nullCount++;

			// If at least one fluid is null
			if (nullCount >= 1)
			{
				List<FluidTagInput> tempFluids = new ArrayList<>(2);

				if(fluidInA!=null) tempFluids.add(fluidInA);
				if(fluidInB!=null) tempFluids.add(fluidInB);
				if(fluidInC!=null) tempFluids.add(fluidInC);

				int damage = ChemicalEnum.getChemicalDamage(tempFluids.get(0)) + ((nullCount == 1) ? ChemicalEnum.getChemicalDamage(tempFluids.get(0)) : 0);


				BasicChemicalRecipeBuilder.builder(itemOutput,
								fluidOutput,
								itemIn,
								tempFluids.get(0),
								nullCount == 2 ? null : tempFluids.get(1),
								damage)
						.setEnergy(energy / 2)
						.setTime(time)
						.build(consumer, toRL("small_chemical_reactor/leach_" + getName()));
			}

			return true;
		}
		catch(Exception e)
		{
			IGLib.IG_LOGGER.error("Error: {}", e.getMessage());
			return false;
		}
	}
}
