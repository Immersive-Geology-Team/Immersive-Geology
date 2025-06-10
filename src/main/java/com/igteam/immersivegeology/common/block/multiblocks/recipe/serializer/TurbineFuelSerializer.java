/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.common.network.PacketUtils;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.TurbineFuel;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import static com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.TurbineFuelBuilder.*;

public class TurbineFuelSerializer extends IERecipeSerializer<TurbineFuel>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.STEAM_TURBINE.iconStack();
	}

	@Override
	public TurbineFuel readFromJson(ResourceLocation recipeId, JsonObject json, IContext context)
	{
		ResourceLocation tagName = new ResourceLocation(json.get(FLUID_TAG_KEY).getAsString());
		TagKey<Fluid> tag = TagKey.create(Registries.FLUID, tagName);
		int amount = json.get(BURN_TIME_KEY).getAsInt();
		float outputRatio = json.get(OUTPUT_RATIO).getAsFloat();
		return new TurbineFuel(recipeId, tag, outputRatio, amount);
	}

	@Nullable
	@Override
	public TurbineFuel fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer)
	{
		List<Fluid> fluids = PacketUtils.readList(buffer, buf -> buf.readRegistryIdUnsafe(ForgeRegistries.FLUIDS));
		int burnTime = buffer.readInt();
		float outputRatio = buffer.readFloat();
		return new TurbineFuel(recipeId, fluids, outputRatio, burnTime);
	}

	@Override
	public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull TurbineFuel recipe)
	{
		PacketUtils.writeList(
				buffer, recipe.getFluids(), (f, buf) -> buf.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, f)
		);
		buffer.writeInt(recipe.getBurnTime());
		buffer.writeFloat(recipe.getOutputRatio());
	}
}