package com.igteam.immersivegeology.common.block.multiblock.coredrill;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.api.crafting.CoreDrillRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class CoreDrillRecipeSerializer extends IERecipeSerializer<CoreDrillRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockHolder.COREDRILL.get());
    }

    @Override
    public CoreDrillRecipe readFromJson(ResourceLocation resourceLocation, JsonObject jsonObject, ICondition.IContext iContext) {
        return null;
    }

    @Nullable
    @Override
    public CoreDrillRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        return null;
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, CoreDrillRecipe coreDrillRecipe) {

    }
}
