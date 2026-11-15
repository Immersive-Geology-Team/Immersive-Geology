/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BulkBlastFluxRecipe;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class BulkBlastFluxSerializer extends IERecipeSerializer<BulkBlastFluxRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, BulkBlastFluxRecipe> CODECS = DualCompositeMapCodecs.composite(
			DualCodecs.INGREDIENT.fieldOf("input"), r -> r.input,
			DualCodecs.INT.optionalFieldOf("flux_value", 1), r -> r.fluxValue,
			BulkBlastFluxRecipe::new
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, BulkBlastFluxRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return MetalEnum.Calcium.getStack(ItemCategoryFlags.METAL_OXIDE);
	}
}
