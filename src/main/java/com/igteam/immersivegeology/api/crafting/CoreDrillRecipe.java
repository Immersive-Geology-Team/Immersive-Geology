package com.igteam.immersivegeology.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class CoreDrillRecipe extends IGMultiblockRecipe{
    protected CoreDrillRecipe(ItemStack outputDummy, Supplier<? extends RecipeType<?>> type, ResourceLocation id) {
        super(outputDummy, type, id);
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return null;
    }

    @Override
    public int getMultipleProcessTicks() {
        return 0;
    }
}
