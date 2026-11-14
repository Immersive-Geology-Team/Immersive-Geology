/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public class IGGeoSerializer extends IERecipeSerializer<IGGeoRecipe>
{
	public static final MapCodec<IGGeoRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			Codec.INT.fieldOf("material_index").forGetter(IGGeoSerializer::getMaterialIndex),
			Codec.INT.fieldOf("material_type").forGetter(IGGeoSerializer::getMaterialType)
	).apply(inst, (material_index, material_type) -> {
		GeologyMaterial material = switch(material_type)
		{
			case 0 -> MineralEnum.values()[material_index].instance();
			case 1 -> MetalEnum.values()[material_index].instance();
			default -> null;
		};
		if(material == null) throw new IllegalArgumentException();
		return new IGGeoRecipe(material);
	}));

	public static final StreamCodec<RegistryFriendlyByteBuf, IGGeoRecipe> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, IGGeoSerializer::getMaterialType,
			ByteBufCodecs.INT, IGGeoSerializer::getMaterialIndex,
			(type, index) -> {
				GeologyMaterial material = type == 1 ? MetalEnum.values()[index].instance() : MineralEnum.values()[index].instance();
				return new IGGeoRecipe(material);
			}
	);

	public static final DualMapCodec<RegistryFriendlyByteBuf, IGGeoRecipe> CODECS = new DualMapCodec<>(CODEC, STREAM_CODEC);

	private static int getMaterialType(IGGeoRecipe recipe)
	{
		return recipe.material instanceof MaterialMineral?0: 1;
	}

	private static int getMaterialIndex(IGGeoRecipe recipe)
	{
		String input = recipe.material.getName();
		input = input.substring(0,1).toUpperCase(Locale.ROOT) + input.substring(1);
		return getMaterialType(recipe)==1?MetalEnum.valueOf(input).ordinal(): MineralEnum.valueOf(input).ordinal();
	}

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, IGGeoRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return new ItemStack(MineralEnum.Unobtania.getOreBlock(StoneEnum.MCStone, OreRichness.NORMAL).asIGBlock());
	}
}
