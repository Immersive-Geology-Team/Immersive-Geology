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
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BasicChemicalRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BasicChemicalRecipeSerializer extends IERecipeSerializer<BasicChemicalRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.SMALL_CHEMICAL_REACTOR.iconStack();
	}

	@Override
	public BasicChemicalRecipe readFromJson(ResourceLocation resourceLocation, JsonObject json, IContext iContext)
	{
		Lazy<ItemStack> output = readOutput(GsonHelper.getAsJsonObject(json, "result"));
		ItemStack itemOut;
		try
		{
			itemOut = output.get();
		}  catch(Exception e)
		{
			itemOut = ItemStack.EMPTY;
		}

		FluidStack fluidOut = ApiUtils.jsonDeserializeFluidStack(GsonHelper.getAsJsonObject(json, "fluidResult"));
		IngredientWithSize itemInput =IngredientWithSize.deserialize(json.get("itemInput"));
		Set<FluidTagInput> fluidSet = new HashSet<>();

		if(GsonHelper.isValidNode(json, "fluidInputA")) fluidSet.add(FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "fluidInputA")));
		if(GsonHelper.isValidNode(json, "fluidInputB")) fluidSet.add(FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "fluidInputB")));

		int energy = GsonHelper.getAsInt(json, "energy");
		int time = GsonHelper.getAsInt(json, "time");
		int damage_per_second = GsonHelper.getAsInt(json, "damage_per_second");

		return new BasicChemicalRecipe(resourceLocation, itemInput, fluidSet, itemOut, fluidOut, damage_per_second, energy, time);
	}

	@Override
	public @Nullable BasicChemicalRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		
		ItemStack output = buffer.readItem();
		FluidStack fluidOut = FluidStack.readFromPacket(buffer);
		IngredientWithSize itemInput = IngredientWithSize.read(buffer);
		HashSet<FluidTagInput> fluidSet = new HashSet<>();
		int fluid_input_size = buffer.readInt();
		for(int i = 0; i < fluid_input_size; i++) {
			FluidTagInput fluid = FluidTagInput.read(buffer);
			fluidSet.add(fluid);
		}

		int energy = buffer.readInt();
		int time = buffer.readInt();
		int damage_per_second = buffer.readInt();
		return new BasicChemicalRecipe(resourceLocation, itemInput, fluidSet, output, fluidOut, damage_per_second, energy, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, BasicChemicalRecipe recipe)
	{
		buffer.writeItemStack(recipe.itemOutput, false);
		recipe.fluidOutput.writeToPacket(buffer);
		recipe.itemInput.write(buffer);
		buffer.writeInt(recipe.fluidIn.size());
		recipe.fluidIn.forEach(f -> f.write(buffer));
		buffer.writeInt(recipe.getTotalProcessEnergy());
		int time = recipe.getTotalProcessTime();
		buffer.writeInt(time);
		int damage_per_second = recipe.getDamagePerTick();
		buffer.writeInt(damage_per_second);
	}
}
