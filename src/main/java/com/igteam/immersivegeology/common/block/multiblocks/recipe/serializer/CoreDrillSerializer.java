/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CoreDrillRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class CoreDrillSerializer extends IERecipeSerializer<CoreDrillRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, CoreDrillRecipe> CODECS = DualCompositeMapCodecs.composite(
			IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input"), CoreDrillRecipe::getInput,
			IEDualCodecs.FLUID_STACK.map(FluidStack::getFluid, fluid -> new FluidStack(fluid, 1)).fieldOf("fluidResult"), CoreDrillRecipe::getOutput,
			CoreDrillRecipe::new
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, CoreDrillRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.CRYSTALLIZER.iconStack();
	}
}
