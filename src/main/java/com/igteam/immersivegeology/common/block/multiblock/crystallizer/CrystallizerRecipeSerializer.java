package com.igteam.immersivegeology.common.block.multiblock.crystallizer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.core.registration.IGMultiblockHolder;
import com.igteam.immersivegeology.api.crafting.CrystallizerRecipe;
import com.igteam.immersivegeology.api.crafting.IGMultiblockRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class CrystallizerRecipeSerializer extends IERecipeSerializer<CrystallizerRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockHolder.CRYSTALLIZER.get());
    }

    @Override
    public CrystallizerRecipe readFromJson(ResourceLocation resourceLocation, JsonObject jsonObject, ICondition.IContext iContext) {
        return null;
    }

    @Nullable
    @Override
    public CrystallizerRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        return null;
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, CrystallizerRecipe crystallizerRecipe) {

    }
}
