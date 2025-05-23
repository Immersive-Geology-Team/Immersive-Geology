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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalExchangerRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class GeothermalExchangerRecipeSerializer extends IERecipeSerializer<GeothermalExchangerRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.GEOTHERMAL_EXCHANGER.iconStack();
	}

	@Override
	public GeothermalExchangerRecipe readFromJson(ResourceLocation resourceLocation, JsonObject json, IContext iContext)
	{
		FluidStack fluid_output = ApiUtils.jsonDeserializeFluidStack(GsonHelper.getAsJsonObject(json, "fluidResult"));
		FluidTagInput input = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "input"));
		int energy = GsonHelper.getAsInt(json, "energy");
		int time = GsonHelper.getAsInt(json, "time");
		return new GeothermalExchangerRecipe(resourceLocation, input, ()->fluid_output, energy, time);
	}

	@Override
	public @Nullable GeothermalExchangerRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		FluidStack fluid_output = buffer.readFluidStack();
		FluidTagInput input = FluidTagInput.read(buffer);
		int energy = buffer.readInt();
		int time = buffer.readInt();
		return new GeothermalExchangerRecipe(resourceLocation, input, ()->fluid_output, energy, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, GeothermalExchangerRecipe recipe)
	{
		buffer.writeFluidStack(recipe.fluidOutput.get());
		recipe.fluidIn.write(buffer);
		buffer.writeInt(recipe.getTotalProcessEnergy());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
}
