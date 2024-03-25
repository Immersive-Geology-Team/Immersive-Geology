package com.igteam.immersivegeology.common.block.multiblock.gravityseparator;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.api.crafting.CrystallizerRecipe;
import com.igteam.immersivegeology.api.crafting.GravitySeparatorRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class GravitySeparatorRecipeSerializer extends IERecipeSerializer<GravitySeparatorRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockHolder.GRAVITY_SEPARATOR.get());
    }

    @Override
    public GravitySeparatorRecipe readFromJson(ResourceLocation resourceLocation, JsonObject jsonObject, ICondition.IContext iContext) {
        return null;
    }

    @Nullable
    @Override
    public GravitySeparatorRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        return null;
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, GravitySeparatorRecipe gravitySeparatorRecipe) {

    }
}
