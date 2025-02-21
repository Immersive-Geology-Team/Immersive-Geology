/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.PelletizerRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
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
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGPelletizerMethod extends IGRecipeMethod
{
	private int energy, time;
	private IngredientWithSize input;
	private ItemStack output;
	private String name;

	public IGPelletizerMethod(MaterialHelper parent, IGStageDesignation stage)
	{
		super(new IGRecipeStage(parent, stage){});
	}

	public IGPelletizerMethod create() {
		this.name = create_advanced_method_name(ItemCategoryFlags.POWDER, ItemCategoryFlags.PELLET);
		this.output = parentMaterial.getStack(ItemCategoryFlags.PELLET, 1); //TODO Later Configurable
		this.input = new IngredientWithSize(parentMaterial.getItemTag(ItemCategoryFlags.POWDER), 1);//TODO Later Configurable
		this.time = IGLib.PELLETIZER_DEFAULT_TIME;//TODO Later Configurable
		this.energy = 4800;//TODO Later Configurable
		return this;
	}

	public IGPelletizerMethod create(MaterialHelper output_material) {
		this.name = create_advanced_method_name(ItemCategoryFlags.POWDER, ItemCategoryFlags.PELLET);
		this.output = output_material.getStack(ItemCategoryFlags.PELLET, 1);
		this.input = new IngredientWithSize(parentMaterial.getItemTag(ItemCategoryFlags.POWDER), 1);
		this.time = IGLib.PELLETIZER_DEFAULT_TIME;
		this.energy = 4800;
		return this;
	}

	public IGPelletizerMethod create(IFlagType<?> input_form) {
		this.name = create_advanced_method_name(input_form, ItemCategoryFlags.PELLET);
		this.output = parentMaterial.getStack(ItemCategoryFlags.PELLET, 1);
		this.input = new IngredientWithSize(parentMaterial.getItemTag(input_form), 1);
		this.time = IGLib.PELLETIZER_DEFAULT_TIME;
		this.energy = 4100;
		return this;
	}

	public IGPelletizerMethod create(IFlagType<?> input_form, MaterialHelper output_material) {
		this.name = create_advanced_method_name(input_form, ItemCategoryFlags.PELLET);
		this.output = output_material.getStack(ItemCategoryFlags.PELLET, 1);
		this.input = new IngredientWithSize(parentMaterial.getItemTag(input_form), 1);
		this.time = IGLib.PELLETIZER_DEFAULT_TIME;
		this.energy = 4800;
		return this;
	}

	public IGPelletizerMethod create(IFlagType<?> input_form, IFlagType<?> output_form)
	{
		this.name = create_advanced_method_name(input_form, output_form);
		this.output = parentMaterial.getStack(output_form, 1);
		this.input = new IngredientWithSize(parentMaterial.getItemTag(input_form), 1);
		this.time = IGLib.PELLETIZER_DEFAULT_TIME;
		this.energy = 4800;
		return this;
	}
	@NotNull
	@Override
	public IGRecipeMethod.RecipeMethod getMethod()
	{
		return RecipeMethod.PELLETIZE;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return toRL("pelletizer/pelletize_" + getName());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public ItemStack getIconStack()
	{
		return IGMultiblockProvider.PELLETIZER.iconStack();
	}

	@Override
	public void render(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		renderItemStack(graphics, input.getRandomizedExampleStack(0), x + 34, y + 3, mx, my);
		renderItemStack(graphics, output, x + 68, y + 3, mx, my);
		renderFluidStack(graphics, ChemicalEnum.BindingAgent.getFluid(BlockCategoryFlags.FLUID), x + 16, y + 3, 16,16, mx, my);
	}

	@Override
	public void renderDisplayStack(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{
		renderItemStack(graphics, input.getRandomizedExampleStack(0), x, y, mx, my);
	}

	@Override
	public void renderFinalStack(GuiGraphics graphics, ManualScreen screen, int baseX, int baseY, int mx, int my)
	{

	}

	@Override
	public boolean hasAdditionalInputRenders()
	{
		return true;
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		try
		{
			PelletizerRecipeBuilder builder = PelletizerRecipeBuilder.builder(output);
			builder.addInput(input);
			builder.setEnergy(energy);
			builder.setTime(time);
			builder.build(consumer, getLocation());
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
