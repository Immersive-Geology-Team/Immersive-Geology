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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryAlloyRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FoundryAlloyRecipeSerializer extends IERecipeSerializer<FoundryAlloyRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.FOUNDRY.iconStack();
	}

	@Override
	public FoundryAlloyRecipe readFromJson(ResourceLocation id, JsonObject json, IContext context)
	{
		JsonArray array = GsonHelper.getAsJsonArray(json, "inputs");
		List<FluidTagInput> inputs = new ArrayList<>(array.size());
		for(int i = 0; i < array.size(); i++) inputs.add(FluidTagInput.deserialize(array.get(i)));
		FluidStack output = ApiUtils.jsonDeserializeFluidStack(GsonHelper.getAsJsonObject(json, "result"));
		int energy = GsonHelper.getAsInt(json, "energy");
		int time = GsonHelper.getAsInt(json, "time");
		validate(id, inputs, output, energy, time);
		return new FoundryAlloyRecipe(id, inputs, output, energy, time);
	}

	private static void validate(ResourceLocation id, List<FluidTagInput> inputs, FluidStack output, int energy, int time)
	{
		if(inputs.size() < 2)
			throw new JsonSyntaxException("Foundry alloying recipe "+id+" needs at least two fluid inputs to merge");
		for(FluidTagInput input : inputs)
			if(input.getAmount() <= 0)
				throw new JsonSyntaxException("Foundry alloying recipe "+id+" has an input part of zero, every part of the ratio must be above zero");
		if(output.isEmpty()||output.getAmount() <= 0)
			throw new JsonSyntaxException("Foundry alloying recipe "+id+" produces no alloy");
		if(energy < 0)
			throw new JsonSyntaxException("Foundry alloying recipe "+id+" has a negative energy cost");
		if(time <= 0)
			throw new JsonSyntaxException("Foundry alloying recipe "+id+" has a time of "+time+", it must be at least 1 tick");
	}

	@Override
	public @Nullable FoundryAlloyRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
	{
		int count = buffer.readVarInt();
		List<FluidTagInput> inputs = new ArrayList<>(count);
		for(int i = 0; i < count; i++) inputs.add(FluidTagInput.read(buffer));
		FluidStack output = FluidStack.readFromPacket(buffer);
		int energy = buffer.readInt();
		int time = buffer.readInt();
		return new FoundryAlloyRecipe(id, inputs, output, energy, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FoundryAlloyRecipe recipe)
	{
		buffer.writeVarInt(recipe.alloyInputs.size());
		for(FluidTagInput input : recipe.alloyInputs) input.write(buffer);
		recipe.alloyOutput.writeToPacket(buffer);
		buffer.writeInt(recipe.getTotalProcessEnergy());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
}
