package com.igteam.immersive_geology.api.crafting;

import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class VatRecipe extends IGMultiblockRecipe
{
    public static final IRecipeType<VatRecipe> TYPE = IRecipeType.register(IGLib.MODID + ":vat");

    public static Map<ResourceLocation, VatRecipe> recipes = new HashMap<>();

    public static VatRecipe findRecipe(ItemStack stack, FluidStack fluid1, FluidStack fluid2)
    {
        for(VatRecipe recipe:recipes.values())
        {

        }
    }
}
