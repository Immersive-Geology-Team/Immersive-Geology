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
import blusunrize.immersiveengineering.common.crafting.serializers.MetalPressRecipeSerializer;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class FoundryRecipeSerializer extends IERecipeSerializer<FoundryRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.FOUNDRY.iconStack();
	}

	@Override
	public FoundryRecipe readFromJson(ResourceLocation resourceLocation, JsonObject json, IContext iContext)
	{
		Lazy<ItemStack> output = readOutput(json.get("result"));
		FluidTagInput input = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "input"));
		int energy = GsonHelper.getAsInt(json, "energy");
		int time = GsonHelper.getAsInt(json, "time");
		Item mold = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(json, "mold")));
		return new FoundryRecipe(resourceLocation, input, output, mold, energy, time);
	}

	@Override
	public @Nullable FoundryRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		Lazy<ItemStack> output = readLazyStack(buffer);
		FluidTagInput input = FluidTagInput.read(buffer);
		int energy = buffer.readInt();
		int time = buffer.readInt();
		Item mold = (Item)buffer.readRegistryIdSafe(Item.class);
		return new FoundryRecipe(resourceLocation, input, output, mold, energy, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FoundryRecipe recipe)
	{
		writeLazyStack(buffer, recipe.itemOutput);
		recipe.fluidIn.write(buffer);
		buffer.writeInt(recipe.getTotalProcessEnergy());
		buffer.writeInt(recipe.getTotalProcessTime());
		buffer.writeRegistryId(ForgeRegistries.ITEMS, recipe.mold);
	}
}
