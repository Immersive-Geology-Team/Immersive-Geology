/*
 * ${USER}
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

public class BloomeryRecipe extends MultiblockRecipe
{
	public static RegistryObject<IERecipeSerializer<BloomeryRecipe>> SERIALIZER;
	public static final CachedRecipeList<BloomeryRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.BLOOMERY);

	protected <T extends Recipe<?>> BloomeryRecipe(ResourceLocation id)
	{
		super(LAZY_EMPTY, IGRecipeTypes.BLOOMERY, id);
	}

	@Override
	protected IERecipeSerializer<?> getIESerializer()
	{
		return null;
	}

	@Override
	public int getMultipleProcessTicks()
	{
		return 0;
	}
}
