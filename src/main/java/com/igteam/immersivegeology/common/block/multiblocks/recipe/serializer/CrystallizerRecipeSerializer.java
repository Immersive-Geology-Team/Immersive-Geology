/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CrystallizerRecipeSerializer extends IERecipeSerializer<CrystallizerRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, CrystallizerRecipe> CODECS = DualCompositeMapCodecs.composite(
			IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input"), r -> r.fluidIn,
			TagOutput.CODECS.fieldOf("result"), r -> r.itemOutput,
			IEDualCodecs.FLUID_STACK.fieldOf("fluidResult"), r -> r.fluidOutput.get(),
			DualCodecs.INT.fieldOf("energy"), CrystallizerRecipe::getTotalProcessEnergy,
			DualCodecs.INT.fieldOf("time"), CrystallizerRecipe::getTotalProcessTime,
			(input, output, fluidOutput, energy, time) ->
					new CrystallizerRecipe(input, output, Lazy.of(() -> fluidOutput), energy, time)
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, CrystallizerRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.CRYSTALLIZER.iconStack();
	}
}
