package com.igteam.immersive_geology.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.api.crafting.recipes.VatRecipe;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class VatRecipeSerializer extends IERecipeSerializer<VatRecipe> {

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockRegistrationHolder.Multiblock.chemicalvat);
    }

    @Override
    public VatRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        return null;
    }

    @Override
    public VatRecipe read(ResourceLocation resourceLocation, PacketBuffer packetBuffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer packetBuffer, VatRecipe vatRecipe) {

    }
}
