/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.recipe;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryRecipe;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class IGGeoSerializer extends IERecipeSerializer<IGGeoRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return new ItemStack(MineralEnum.Unobtania.getOreBlock(StoneEnum.MCStone, OreRichness.NORMAL).asIGBlock());
	}

	@Override
	public IGGeoRecipe readFromJson (ResourceLocation resourceLocation, JsonObject json, IContext iContext)
	{
		int material_index = GsonHelper.getAsInt(json, "material_index");
		int material_type = GsonHelper.getAsInt(json, "material_type");
		GeologyMaterial material = switch(material_type)
		{
			case 0 -> MineralEnum.values()[material_index].instance();
			case 1 -> MetalEnum.values()[material_index].instance();
			default -> null;
		};
		if(material == null) throw new IllegalArgumentException();
		return new IGGeoRecipe(resourceLocation, material);
	}

	@Override
	public @Nullable IGGeoRecipe fromNetwork (ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		int type = buffer.readInt();
		int index = buffer.readInt();
		GeologyMaterial material = type == 1 ? MetalEnum.values()[index].instance() : MineralEnum.values()[index].instance();
 		return new IGGeoRecipe(resourceLocation, material);
	}

	@Override
	public void toNetwork (FriendlyByteBuf buffer, IGGeoRecipe recipe)
	{
		GeologyMaterial material = recipe.material;
		int type = material instanceof MaterialMineral ? 0 : 1;

		String input = material.getName();
		input = input.substring(0,1).toUpperCase() + input.substring(1);

		int index = type == 1 ? MetalEnum.valueOf(input).ordinal() : MineralEnum.valueOf(input).ordinal();

		buffer.writeInt(type);
		buffer.writeInt(index);
	}
}
