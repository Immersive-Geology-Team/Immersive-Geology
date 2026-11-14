/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BulkBlastFurnaceLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BulkBlastFurnaceRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.mojang.serialization.DataResult;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public class BulkBlastFurnaceRecipeSerializer extends IERecipeSerializer<BulkBlastFurnaceRecipe>
{
	private static final DualMapCodec<RegistryFriendlyByteBuf, BulkBlastFurnaceRecipe> BASE_CODECS = DualCompositeMapCodecs.composite(
			IngredientWithSize.CODECS.optionalFieldOf("ore"), r -> Optional.ofNullable(r.oreInput),
			IEDualCodecs.SIZED_FLUID_INGREDIENT.optionalFieldOf("melt_input"), r -> Optional.ofNullable(r.meltInput),
			IEDualCodecs.FLUID_STACK.fieldOf("melt"), r -> r.meltPerUnit,
			IEDualCodecs.FLUID_STACK.optionalFieldOf("rich_melt", FluidStack.EMPTY), r -> r.richMelt,
			DualCodecs.FLOAT.fieldOf("coke_ratio"), r -> r.cokeRatio,
			DualCodecs.FLOAT.optionalFieldOf("rich_coke_ratio", 0f), r -> r.richCokeRatio,
			DualCodecs.FLOAT.fieldOf("flux_ratio"), r -> r.fluxRatio,
			DualCodecs.FLOAT.fieldOf("min_yield"), r -> r.minYield,
			DualCodecs.FLOAT.fieldOf("max_yield"), r -> r.maxYield,
			DualCodecs.INT.fieldOf("heat"), BulkBlastFurnaceRecipe::getHeatRequired,
			DualCodecs.INT.fieldOf("time"), BulkBlastFurnaceRecipe::getTotalProcessTime,
			(oreInput, meltInput, melt, richMelt, cokeRatio, richCokeRatio, fluxRatio, minYield, maxYield, heat, time) ->
					new BulkBlastFurnaceRecipe(oreInput.orElse(null), meltInput.orElse(null), melt, richMelt,
							cokeRatio, richCokeRatio, fluxRatio, minYield, maxYield, heat, time)
	);

	public static final DualMapCodec<RegistryFriendlyByteBuf, BulkBlastFurnaceRecipe> CODECS = new DualMapCodec<>(
			BASE_CODECS.mapCodec().validate(BulkBlastFurnaceRecipeSerializer::validate),
			BASE_CODECS.streamCodec()
	);

	private static DataResult<BulkBlastFurnaceRecipe> validate(BulkBlastFurnaceRecipe recipe)
	{
		if((recipe.oreInput==null)==(recipe.meltInput==null))
			return DataResult.error(() -> "Bulk Blast Furnace recipe must declare exactly one of \"ore\" or \"melt_input\"");
		if(recipe.meltInput!=null&&recipe.meltInput.amount() <= 0)
			return DataResult.error(() -> "Bulk Blast Furnace recipe has a melt input of zero");
		if(recipe.meltPerUnit.isEmpty()||recipe.meltPerUnit.getAmount() <= 0)
			return DataResult.error(() -> "Bulk Blast Furnace recipe produces no melt");
		if(recipe.cokeRatio <= 0)
			return DataResult.error(() -> "Bulk Blast Furnace recipe has a coke ratio of "+recipe.cokeRatio+", it must be above zero");
		if(!recipe.richMelt.isEmpty()&&recipe.richCokeRatio <= recipe.cokeRatio)
			return DataResult.error(() -> "Bulk Blast Furnace recipe has a rich coke ratio of "+recipe.richCokeRatio+", it must sit above the lean ratio "+recipe.cokeRatio);
		if(recipe.richCokeRatio > 0&&recipe.richMelt.isEmpty())
			return DataResult.error(() -> "Bulk Blast Furnace recipe declares a rich coke ratio but no rich melt");
		if(recipe.fluxRatio <= 0)
			return DataResult.error(() -> "Bulk Blast Furnace recipe has a flux ratio of "+recipe.fluxRatio+", it must be above zero");
		if(recipe.minYield < 0||recipe.maxYield > 1||recipe.minYield > recipe.maxYield)
			return DataResult.error(() -> "Bulk Blast Furnace recipe has a yield range of "+recipe.minYield+".."+recipe.maxYield+", it must sit inside 0..1");
		if(recipe.getHeatRequired() < 0||recipe.getHeatRequired() > BulkBlastFurnaceLogic.MAX_HEAT)
			return DataResult.error(() -> "Bulk Blast Furnace recipe requires a heat of "+recipe.getHeatRequired()+", it must be between 0 and "+BulkBlastFurnaceLogic.MAX_HEAT);
		if(recipe.getTotalProcessTime() <= 0)
			return DataResult.error(() -> "Bulk Blast Furnace recipe has a time of "+recipe.getTotalProcessTime()+", it must be at least 1 tick");
		return DataResult.success(recipe);
	}

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, BulkBlastFurnaceRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.BULK_BLAST_FURNACE.iconStack();
	}
}
