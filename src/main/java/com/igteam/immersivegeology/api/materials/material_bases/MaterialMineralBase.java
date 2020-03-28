package com.igteam.immersivegeology.api.materials.material_bases;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.MaterialUseType;

public abstract class MaterialMineralBase extends Material
{
    public abstract EnumMineralType getMineralType();

    @Override
    public boolean hasSubtype(MaterialUseType useType)
    {
        switch(useType)
        {
            case CHUNK:
            case DUST:
            case TINY_DUST:
                return true;
        }
        return false;
    }

    @Override
    public MaterialTypes getMaterialType()
    {
        return MaterialTypes.MINERAL;
    }

    @Override
    public net.minecraft.block.material.Material getBlockMaterial()
    {
        return net.minecraft.block.material.Material.ROCK;
    }

    public enum EnumMineralType
    {
        MINERAL
    }
}
