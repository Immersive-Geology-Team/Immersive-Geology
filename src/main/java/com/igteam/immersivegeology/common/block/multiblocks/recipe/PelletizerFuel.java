/*
 * Muddykat
 * Copyright (c) 2024
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

import java.util.Iterator;

public class PelletizerFuel extends IESerializableRecipe
{
	public static RegistryObject<IERecipeSerializer<PelletizerFuel>> SERIALIZER;
	public static final CachedRecipeList<PelletizerFuel> RECIPES;
	public final Ingredient input;
	public final int burnTime;

    public PelletizerFuel(ResourceLocation id, Ingredient input, int burnTime) {
		super(LAZY_EMPTY, IGRecipeTypes.PELLETIZER_FUEL, id);
		this.input = input;
		this.burnTime = burnTime;
	}

	public static int getPelletizerFuelTime(Level level, ItemStack stack) {
		Iterator var2 = RECIPES.getRecipes(level).iterator();

		PelletizerFuel e;
		do {
			if (!var2.hasNext()) {
				return 0;
			}

			e = (PelletizerFuel)var2.next();
		} while(!e.input.test(stack));

		return e.burnTime;
	}

	public static boolean isValidPelletizerFuel(Level level, ItemStack stack) {
		return getPelletizerFuelTime(level, stack) > 0;
	}

	protected IERecipeSerializer<PelletizerFuel> getIESerializer() {
		return (IERecipeSerializer)SERIALIZER.get();
	}

	public ItemStack getResultItem(RegistryAccess access) {
		return ItemStack.EMPTY;
	}

	static {
		RECIPES = new CachedRecipeList(IGRecipeTypes.PELLETIZER_FUEL);
	}
}
