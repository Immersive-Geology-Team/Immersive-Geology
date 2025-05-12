/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class CoreDrillRecipe extends MultiblockRecipe
{
	public static RegistryObject<IERecipeSerializer<CoreDrillRecipe>> SERIALIZER;
	public static final CachedRecipeList<CoreDrillRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.COREDRILL);

	FluidTagInput input;
	Fluid output;

	public <T extends Recipe<?>> CoreDrillRecipe(ResourceLocation id, FluidTagInput input, Fluid output)
	{
		super(LAZY_EMPTY, IGRecipeTypes.COREDRILL, id);
		this.input = input;
		this.output = output;
	}

	@Nullable
	public static CoreDrillRecipe get(Level level, FluidStack fluid)
	{
		for(CoreDrillRecipe r : RECIPES.getRecipes(level))
		{
			if(r.input.test(fluid))
			{
				return r;
			}
		}
		return null;
	}

	public FluidTagInput getInput()
	{
		return input;
	}

	public Fluid getOutput()
	{
		return output;
	}

	@Override
	protected IERecipeSerializer<?> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public int getMultipleProcessTicks()
	{
		return 1;
	}
}
