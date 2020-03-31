package com.igteam.immersivegeology.api.materials.material_bases;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;

/**
 * Created by JStocke12 on 31-03-2020.
 */
public abstract class MaterialFluidBase extends Material
{
    @Override
    public MaterialTypes getMaterialType()
    {
        return MaterialTypes.FLUID;
    }

    @Override
    public net.minecraft.block.material.Material getBlockMaterial()
    {
        return net.minecraft.block.material.Material.WATER;
    }

    public enum EnumMetalType
    {
        FLUID,
        SOLUTION
    }
}
