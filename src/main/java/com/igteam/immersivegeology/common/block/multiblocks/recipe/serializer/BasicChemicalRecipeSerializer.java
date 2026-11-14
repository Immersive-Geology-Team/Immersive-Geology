/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BasicChemicalRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import malte0811.dualcodecs.DualCodec;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.*;
import java.util.stream.Stream;

public class BasicChemicalRecipeSerializer extends IERecipeSerializer<BasicChemicalRecipe>
{
	private static final DualCodec<RegistryFriendlyByteBuf, TagOutput> OUTPUT_CODECS = new DualCodec<>(
			TagOutput.CODECS.codec().xmap(BasicChemicalRecipeSerializer::resolveOutput, output -> output),
			TagOutput.CODECS.streamCodec()
	);

	private static final MapCodec<Set<SizedFluidIngredient>> FLUID_INPUT_CODEC = RecordCodecBuilder.<Set<SizedFluidIngredient>>mapCodec(inst -> inst.group(
			SizedFluidIngredient.FLAT_CODEC.optionalFieldOf("fluidInputA").forGetter(inputs -> inputs.stream().skip(0).findFirst()),
			SizedFluidIngredient.FLAT_CODEC.optionalFieldOf("fluidInputB").forGetter(inputs -> inputs.stream().skip(1).findFirst())
	).apply(inst, (inputA, inputB) -> toFluidSet(Stream.of(inputA, inputB).flatMap(Optional::stream).toList())));

	private static final DualMapCodec<RegistryFriendlyByteBuf, Set<SizedFluidIngredient>> FLUID_INPUT_CODECS = new DualMapCodec<>(
			FLUID_INPUT_CODEC,
			SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).map(BasicChemicalRecipeSerializer::toFluidSet, ArrayList::new)
	);

	public static final DualMapCodec<RegistryFriendlyByteBuf, BasicChemicalRecipe> CODECS = DualCompositeMapCodecs.composite(
			IngredientWithSize.CODECS.fieldOf("itemInput"), r -> r.itemInput,
			FLUID_INPUT_CODECS, r -> r.fluidIn,
			OUTPUT_CODECS.fieldOf("result"), r -> r.itemOutput,
			IEDualCodecs.FLUID_STACK.fieldOf("fluidResult"), r -> r.fluidOutput,
			DualCodecs.INT.fieldOf("damage_per_second"), BasicChemicalRecipe::getDamagePerTick,
			DualCodecs.INT.fieldOf("energy"), BasicChemicalRecipe::getTotalProcessEnergy,
			DualCodecs.INT.fieldOf("time"), BasicChemicalRecipe::getTotalProcessTime,
			BasicChemicalRecipe::new
	);

	private static TagOutput resolveOutput(TagOutput output)
	{
		try
		{
			return new TagOutput(output.get());
		}
		catch(Exception e)
		{
			return TagOutput.EMPTY;
		}
	}

	private static Set<SizedFluidIngredient> toFluidSet(List<SizedFluidIngredient> inputs)
	{
		// FluidTagInput used identity equality; keep distinct input entries distinct.
		Set<SizedFluidIngredient> fluidSet = Collections.newSetFromMap(new IdentityHashMap<>());
		fluidSet.addAll(inputs);
		return fluidSet;
	}

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, BasicChemicalRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.SMALL_CHEMICAL_REACTOR.iconStack();
	}
}
