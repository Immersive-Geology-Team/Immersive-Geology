/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.RegistryObject;

public class CoreDrillRecipe extends MultiblockRecipe
{
	public static RegistryObject<IERecipeSerializer<CoreDrillRecipe>> SERIALIZER;
	//TODO implement core drill recipes
	public static final CachedRecipeList<CoreDrillRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.COREDRILL);

	protected <T extends Recipe<?>> CoreDrillRecipe(Lazy<ItemStack> outputDummy, TypeWithClass<T> type, ResourceLocation id)
	{
		super(outputDummy, type, id);
	}

	@Override
	protected IERecipeSerializer<?> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public int getMultipleProcessTicks()
	{
		return 4;
	}
}
