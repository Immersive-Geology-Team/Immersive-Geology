/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IGHydrojetMethod extends IGRecipeMethod
{

	public IGHydrojetMethod(IGRecipeStage stage)
	{
		super(stage);
	}

	public IGHydrojetMethod(MaterialHelper material, IGStageDesignation stage)
	{
		super(new IGRecipeStage(material, stage){});
	}

	@NotNull
	@Override
	public RecipeMethod getMethod()
	{
		return RecipeMethod.CUTTING;
	}

	@Override
	public ResourceLocation getLocation()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "";
	}

	@Override
	public boolean build(Consumer<FinishedRecipe> consumer)
	{
		return false;
	}

	@Override
	public void basicRender(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my)
	{

	}

	@Override
	public void renderOutput(GuiGraphics graphics, ItemStack iconStack, int methodNameX, int methodNameY, int mx, int my)
	{

	}
}
