package com.igteam.immersive_geology.api.materials.helper.processing;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.mojang.datafixers.util.Pair;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract class IGProcessingMethod {
    public abstract ProcessingMethod getKey();
    public abstract int getEnergyCost();
    public abstract int getProcessingTime();

}
