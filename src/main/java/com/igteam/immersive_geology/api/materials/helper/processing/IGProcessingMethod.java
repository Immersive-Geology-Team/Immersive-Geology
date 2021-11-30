package com.igteam.immersive_geology.api.materials.helper.processing;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract class IGProcessingMethod {
    public abstract ProcessingMethod getKey();

    public abstract Pair<MaterialUseType, Material> outputFluidData();

    public abstract int getEnergyCost();

    public abstract int getProcessingTime();

    public abstract FluidStack getSecondaryFluid();

    public abstract FluidStack getPrimaryFluid();

    public abstract ItemStack getItemInput();

    public abstract ItemStack getItemOutput();

    public abstract FluidStack getFluidResult();
}
