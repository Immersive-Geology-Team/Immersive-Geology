/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.CrystallizerRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGCrystallizationMethod extends IGRecipeMethod
{
	private ItemStack itemResult;
	private Lazy<FluidTagInput> fluidInput;
	private TagKey<Fluid> fluid_tag;
	private int time;
	private int energy;
	private String name;
	private FluidStack fluid_out;

	public IGCrystallizationMethod(MaterialHelper material, IGStageDesignation stage)
	{
		super(new IGRecipeStage(material, stage){});
	}

	public IGCrystallizationMethod create(MaterialInterface<?> slurry_base, IFlagType<?> output_form)
	{
		this.name = create_advanced_method_name(ItemCategoryFlags.CRYSTAL);
		if(slurry_base.instance() instanceof MaterialChemical chemical){
			this.itemResult = parentMaterial.getProductionMaterial().getStack(output_form, 1);
			this.fluid_tag = chemical.getFluidTag(BlockCategoryFlags.SLURRY, parentMaterial.getProductionMaterial());
			this.fluidInput = () -> new FluidTagInput(fluid_tag, IGLib.SLURRY_TO_CRYSTAL_MB);
			this.time = 300;
			this.fluid_out = new FluidStack(chemical.getFluid(BlockCategoryFlags.FLUID), IGLib.ACID_RECOVERED_FROM_SLURRY);
			this.energy = 38400;
			if(fluid_tag == null) throw new RuntimeException("Fluid Tag Returned Was Null, IDK why.");
		} else {
			throw new RuntimeException("Slurry Base Chemical IS Not of Chemical Type");
		}

		return this;
	}

	public IGCrystallizationMethod create(MaterialInterface<?> slurry_base, MaterialInterface<?> slurry_product)
	{
		this.name = create_advanced_method_name(ItemCategoryFlags.CRYSTAL);
		if(slurry_base.instance() instanceof MaterialChemical chemical){
			this.itemResult = slurry_product.getStack(ItemCategoryFlags.CRYSTAL, 1);
			this.fluid_tag = chemical.getFluidTag(BlockCategoryFlags.SLURRY, slurry_product);
			this.fluidInput = () -> new FluidTagInput(fluid_tag, IGLib.SLURRY_TO_CRYSTAL_MB);
			this.time = 300;
			this.fluid_out = new FluidStack(chemical.getFluid(BlockCategoryFlags.FLUID), IGLib.ACID_RECOVERED_FROM_SLURRY);
			this.energy = 38400;
			if(fluid_tag == null) throw new RuntimeException("Fluid Tag Returned Was Null, IDK why.");
		} else {
			throw new RuntimeException("Slurry Base Chemical IS Not of Chemical Type");
		}
		return this;
	}

	public IGCrystallizationMethod create(String name, ItemStack output, MaterialInterface<?> slurry_base, MaterialInterface<?> slurry_product, int fluidAmount, int time, int energy)
	{
		this.name = name;
		if(slurry_base.instance() instanceof MaterialChemical chemical){
			this.itemResult = output;
			this.fluid_tag = chemical.getFluidTag(BlockCategoryFlags.SLURRY, slurry_product);
			this.fluidInput = () -> new FluidTagInput(fluid_tag, fluidAmount);
			this.time = time;
			this.fluid_out = new FluidStack(chemical.getFluid(BlockCategoryFlags.FLUID), IGLib.ACID_RECOVERED_FROM_SLURRY);
			this.energy = energy;
			if(fluid_tag == null) throw new RuntimeException("Fluid Tag Returned Was Null, IDK why.");
		} else {
			throw new RuntimeException("Slurry Base Chemical IS Not of Chemical Type");
		}
		return this;
	}

	public IGCrystallizationMethod create(String name, ItemStack output, FluidStack fluid_out, TagKey<Fluid> fluidTag, int fluidAmount, int time, int energy)
	{
		this.name = name;
		if(fluidTag == null) throw new RuntimeException("Fluid Tag is NULL... why? for Method: " + name);

		this.itemResult = output;
		this.fluid_tag = fluidTag;
		this.fluidInput = () -> new FluidTagInput(fluidTag, fluidAmount);
		this.fluid_out = fluid_out;
		this.time = time;
		this.energy = energy;
		return this;
	}

	public IGCrystallizationMethod create(String name, ItemStack output, FluidStack fluid_out, MaterialInterface<?> slurry_base, MaterialInterface<?> slurry_product, int fluidAmount, int time, int energy)
	{
		this.name = name;
		if(slurry_base.instance() instanceof MaterialChemical chemical){
			this.itemResult = output;
			this.fluid_tag = chemical.getFluidTag(BlockCategoryFlags.SLURRY, slurry_product);
			this.fluidInput = () -> new FluidTagInput(fluid_tag, fluidAmount);
			this.fluid_out = fluid_out;
			this.time = time;
			this.energy = energy;
			if(fluid_tag == null) throw new RuntimeException("Fluid Tag Returned Was Null, IDK why.");
		} else {
			throw new RuntimeException("Slurry Base Chemical IS Not of Chemical Type");
		}
		return this;
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.CRYSTALLIZATION;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("crystallizer/crystallize_" + getName());
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public ItemStack getIconStack()
	{
		return IGMultiblockProvider.CRYSTALLIZER.iconStack();
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
		IGLib.IG_LOGGER.info("Attempting to build Crystallizer Method {}", this.name);
		try
		{
			CrystallizerRecipeBuilder builder = CrystallizerRecipeBuilder.builder(this.itemResult, this.fluid_out).addInput(this.fluidInput.get()).setEnergy(energy).setTime(time);
			builder.build(consumer, getLocation());
			return true;
		} catch(Exception e)
		{
			IGLib.IG_LOGGER.error("Exception in Crystallizer Recipe Builder: {}", e.getMessage());
			IGLib.IG_LOGGER.error("Fluid Tag was {}", fluid_tag.toString());
			return false;
		}
	}
}
