/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CentrifugeRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CentrifugeRecipeSerializer extends IERecipeSerializer<CentrifugeRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.CENTRIFUGE.iconStack();
	}

	@Override
	public CentrifugeRecipe readFromJson(ResourceLocation resourceLocation, JsonObject json, IContext iContext)
	{
		FluidTagInput input = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "fluid_input"));
		Lazy<ItemStack> output = readOutput(json.get("item_output"));
		FluidStack primary_fluid_output = ApiUtils.jsonDeserializeFluidStack(GsonHelper.getAsJsonObject(json, "primary_fluid_out"));
		FluidStack secondary_fluid_output = ApiUtils.jsonDeserializeFluidStack(GsonHelper.getAsJsonObject(json, "secondary_fluid_out"));
		int energy = GsonHelper.getAsInt(json, "energy");
		int time = GsonHelper.getAsInt(json, "time");

		return new CentrifugeRecipe(resourceLocation, input, output, () -> primary_fluid_output, () -> secondary_fluid_output, energy, time);
	}

	@Override
	public @Nullable CentrifugeRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		FluidTagInput input = FluidTagInput.read(buffer);
		Lazy<ItemStack> output = readLazyStack(buffer);
		FluidStack primaryFluidOutput = buffer.readFluidStack();
		FluidStack secondaryFluidOutput = buffer.readFluidStack();
		int energy = buffer.readInt();
		int time = buffer.readInt();
		return new CentrifugeRecipe(resourceLocation, input, output, () -> primaryFluidOutput, () -> secondaryFluidOutput, energy, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CentrifugeRecipe recipe)
	{
		recipe.fluidIn.write(buffer);
		writeLazyStack(buffer, recipe.itemOutput);
		buffer.writeFluidStack(recipe.primaryFluidOutput.get());
		buffer.writeFluidStack(recipe.secondaryFluidOutput.get());
		buffer.writeInt(recipe.getTotalProcessEnergy());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
}
