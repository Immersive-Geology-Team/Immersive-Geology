/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BulkBlastFluxRecipe extends IESerializableRecipe
{
	public static RegistryObject<IERecipeSerializer<BulkBlastFluxRecipe>> SERIALIZER;
	public static final CachedRecipeList<BulkBlastFluxRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.BULK_BLAST_FLUX);

	public final Ingredient input;
	public final int fluxValue;

	public BulkBlastFluxRecipe(ResourceLocation id, Ingredient input, int fluxValue)
	{
		super(LAZY_EMPTY, IGRecipeTypes.BULK_BLAST_FLUX, id);
		this.input = input;
		this.fluxValue = fluxValue;
	}

	public static int getFluxValue(@Nullable Level level, ItemStack stack)
	{
		if(stack.isEmpty()) return 0;
		for(BulkBlastFluxRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.input.test(stack)) return recipe.fluxValue;
		return 0;
	}

	public static boolean isValidFlux(@Nullable Level level, ItemStack stack)
	{
		return getFluxValue(level, stack) > 0;
	}

	@Override
	protected IERecipeSerializer<BulkBlastFluxRecipe> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access)
	{
		return ItemStack.EMPTY;
	}
}
