/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import blusunrize.immersiveengineering.api.utils.FastEither;
import blusunrize.immersiveengineering.api.utils.TagUtils;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class TurbineFuel extends IESerializableRecipe
{
	public static RegistryObject<IERecipeSerializer<TurbineFuel>> SERIALIZER;
	public static final CachedRecipeList<TurbineFuel> RECIPES;
	private final FastEither<TagKey<Fluid>, List<Fluid>> fluids;
	private final int burnTime;
	private final float outputRatio;

	public TurbineFuel(ResourceLocation id, TagKey<Fluid> fluids, float outputRatio, int burnTime) {
		super(LAZY_EMPTY, IGRecipeTypes.TURBINE_FUEL, id);
		this.fluids = FastEither.left(fluids);
		this.burnTime = burnTime;
		this.outputRatio = outputRatio;
	}

	public TurbineFuel(ResourceLocation id, List<Fluid> fluids, float outputRatio, int burnTime) {
		super(LAZY_EMPTY, IGRecipeTypes.TURBINE_FUEL, id);
		this.fluids = FastEither.right(fluids);
		this.burnTime = burnTime;
		this.outputRatio = outputRatio;
	}

	public List<Fluid> getFluids() {
		return this.fluids.map((t) -> {
			return TagUtils.elementStream(BuiltInRegistries.FLUID, t).toList();
		}, Function.identity());
	}

	public int getBurnTime() {
		return this.burnTime;
	}

	public float getOutputRatio()
	{
		return outputRatio;
	}

	protected IERecipeSerializer<?> getIESerializer() {
		return SERIALIZER.get();
	}

	@Nonnull
	public ItemStack getResultItem(@NotNull RegistryAccess access) {
		return ItemStack.EMPTY;
	}

	public boolean matches(Fluid in) {
		return this.fluids.isLeft() ? in.is(this.fluids.leftNonnull()) : (this.fluids.rightNonnull()).contains(in);
	}

	public static TurbineFuel getRecipeFor(Level level, Fluid in, @Nullable TurbineFuel hint) {
		if (hint != null && hint.matches(in)) {
			return hint;
		} else {
			Iterator<TurbineFuel> var3 = RECIPES.getRecipes(level).iterator();

			TurbineFuel fuel;
			do {
				if (!var3.hasNext()) {
					return null;
				}

				fuel = (TurbineFuel)var3.next();
			} while(!fuel.matches(in));

			return fuel;
		}
	}

	public static SortedMap<Component, Integer> getManualFuelList(Level level) {
		SortedMap<Component, Integer> map = new TreeMap<>(Comparator.comparing(Component::getString, Comparator.naturalOrder()));

		for(TurbineFuel recipe : RECIPES.getRecipes(level))
		{
			for(Fluid f : recipe.getFluids())
			{
				map.put(f.getFluidType().getDescription(), recipe.getBurnTime());
			}
		}

		return map;
	}

	static {
		RECIPES = new CachedRecipeList(IGRecipeTypes.TURBINE_FUEL);
	}
}
