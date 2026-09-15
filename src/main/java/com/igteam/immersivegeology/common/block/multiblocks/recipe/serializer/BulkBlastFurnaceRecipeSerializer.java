/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BulkBlastFurnaceLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BulkBlastFurnaceRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class BulkBlastFurnaceRecipeSerializer extends IERecipeSerializer<BulkBlastFurnaceRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.BULK_BLAST_FURNACE.iconStack();
	}

	@Override
	public BulkBlastFurnaceRecipe readFromJson(ResourceLocation id, JsonObject json, IContext context)
	{
		IngredientWithSize oreInput = GsonHelper.isValidNode(json, "ore")
				?IngredientWithSize.deserialize(GsonHelper.getAsJsonObject(json, "ore")): null;
		FluidTagInput meltInput = GsonHelper.isValidNode(json, "melt_input")
				?FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "melt_input")): null;
		FluidStack melt = ApiUtils.jsonDeserializeFluidStack(GsonHelper.getAsJsonObject(json, "melt"));
		FluidStack richMelt = GsonHelper.isValidNode(json, "rich_melt")
				?ApiUtils.jsonDeserializeFluidStack(GsonHelper.getAsJsonObject(json, "rich_melt")): FluidStack.EMPTY;
		float cokeRatio = GsonHelper.getAsFloat(json, "coke_ratio");
		float richCokeRatio = GsonHelper.getAsFloat(json, "rich_coke_ratio", 0);
		float fluxRatio = GsonHelper.getAsFloat(json, "flux_ratio");
		float minYield = GsonHelper.getAsFloat(json, "min_yield");
		float maxYield = GsonHelper.getAsFloat(json, "max_yield");
		int heat = GsonHelper.getAsInt(json, "heat");
		int time = GsonHelper.getAsInt(json, "time");
		validate(id, oreInput, meltInput, melt, richMelt, cokeRatio, richCokeRatio, fluxRatio, minYield, maxYield, heat, time);
		return new BulkBlastFurnaceRecipe(id, oreInput, meltInput, melt, richMelt, cokeRatio, richCokeRatio, fluxRatio, minYield, maxYield, heat, time);
	}

	private static void validate(
			ResourceLocation id, @Nullable IngredientWithSize oreInput, @Nullable FluidTagInput meltInput,
			FluidStack melt, FluidStack richMelt, float cokeRatio, float richCokeRatio, float fluxRatio,
			float minYield, float maxYield, int heat, int time
	)
	{
		if((oreInput==null)==(meltInput==null))
			throw new JsonSyntaxException("Bulk Blast Furnace recipe "+id+" must declare exactly one of \"ore\" or \"melt_input\"");
		if(meltInput!=null&&meltInput.getAmount() <= 0)
			throw new JsonSyntaxException("Bulk Blast Furnace recipe "+id+" has a melt input of zero");
		if(melt.isEmpty()||melt.getAmount() <= 0)
			throw new JsonSyntaxException("Bulk Blast Furnace recipe "+id+" produces no melt");
		if(cokeRatio <= 0)
			throw new JsonSyntaxException("Bulk Blast Furnace recipe "+id+" has a coke ratio of "+cokeRatio+", it must be above zero");
		if(!richMelt.isEmpty()&&richCokeRatio <= cokeRatio)
			throw new JsonSyntaxException("Bulk Blast Furnace recipe "+id+" has a rich coke ratio of "+richCokeRatio+", it must sit above the lean ratio "+cokeRatio);
		if(richCokeRatio > 0&&richMelt.isEmpty())
			throw new JsonSyntaxException("Bulk Blast Furnace recipe "+id+" declares a rich coke ratio but no rich melt");
		if(fluxRatio <= 0)
			throw new JsonSyntaxException("Bulk Blast Furnace recipe "+id+" has a flux ratio of "+fluxRatio+", it must be above zero");
		if(minYield < 0||maxYield > 1||minYield > maxYield)
			throw new JsonSyntaxException("Bulk Blast Furnace recipe "+id+" has a yield range of "+minYield+".."+maxYield+", it must sit inside 0..1");
		if(heat < 0||heat > BulkBlastFurnaceLogic.MAX_HEAT)
			throw new JsonSyntaxException("Bulk Blast Furnace recipe "+id+" requires a heat of "+heat+", it must be between 0 and "+BulkBlastFurnaceLogic.MAX_HEAT);
		if(time <= 0)
			throw new JsonSyntaxException("Bulk Blast Furnace recipe "+id+" has a time of "+time+", it must be at least 1 tick");
	}

	@Override
	public @Nullable BulkBlastFurnaceRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
	{
		IngredientWithSize oreInput = buffer.readBoolean()?IngredientWithSize.read(buffer): null;
		FluidTagInput meltInput = buffer.readBoolean()?FluidTagInput.read(buffer): null;
		FluidStack melt = FluidStack.readFromPacket(buffer);
		FluidStack richMelt = FluidStack.readFromPacket(buffer);
		float cokeRatio = buffer.readFloat();
		float richCokeRatio = buffer.readFloat();
		float fluxRatio = buffer.readFloat();
		float minYield = buffer.readFloat();
		float maxYield = buffer.readFloat();
		int heat = buffer.readInt();
		int time = buffer.readInt();
		return new BulkBlastFurnaceRecipe(id, oreInput, meltInput, melt, richMelt, cokeRatio, richCokeRatio, fluxRatio, minYield, maxYield, heat, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, BulkBlastFurnaceRecipe recipe)
	{
		buffer.writeBoolean(recipe.oreInput!=null);
		if(recipe.oreInput!=null) recipe.oreInput.write(buffer);
		buffer.writeBoolean(recipe.meltInput!=null);
		if(recipe.meltInput!=null) recipe.meltInput.write(buffer);
		recipe.meltPerUnit.writeToPacket(buffer);
		recipe.richMelt.writeToPacket(buffer);
		buffer.writeFloat(recipe.cokeRatio);
		buffer.writeFloat(recipe.richCokeRatio);
		buffer.writeFloat(recipe.fluxRatio);
		buffer.writeFloat(recipe.minYield);
		buffer.writeFloat(recipe.maxYield);
		buffer.writeInt(recipe.getHeatRequired());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
}
