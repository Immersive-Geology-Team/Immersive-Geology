/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.IJEIRecipe;
import blusunrize.immersiveengineering.api.crafting.IMultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.IndustrialSluiceRecipe;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class IGGeoRecipe extends IESerializableRecipe implements IJEIRecipe
{
	public final GeologyMaterial material;
	public static RegistryObject<IERecipeSerializer<IGGeoRecipe>> SERIALIZER;
	public static final CachedRecipeList<IGGeoRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.GEOHINT);
	protected <T extends Recipe<?>> IGGeoRecipe(ResourceLocation id, GeologyMaterial material)
	{
		super(LAZY_EMPTY, IGRecipeTypes.GEOHINT, id);
		this.material = material;
	}

	@Override
	protected IERecipeSerializer<?> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public @NotNull ItemStack getResultItem(RegistryAccess registryAccess)
	{
		return material.getOreIcon();
	}
}
