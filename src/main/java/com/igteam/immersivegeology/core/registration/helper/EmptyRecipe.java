/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.registration.helper;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import com.igteam.immersivegeology.core.registration.IGRecipeSerializers;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

public class EmptyRecipe extends IESerializableRecipe
{
	protected <T extends Recipe<?>> EmptyRecipe(ResourceLocation id)
	{
		super(LAZY_EMPTY, IGRecipeTypes.EMPTY, id);
	}

	@Override
	protected IERecipeSerializer<?> getIESerializer()
	{
		return IGRecipeSerializers.EMPTY_SERIALIZER.get();
	}

	@Override
	public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess)
	{
		return ItemStack.EMPTY;
	}
}
