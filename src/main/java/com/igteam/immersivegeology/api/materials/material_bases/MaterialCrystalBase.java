package com.igteam.immersivegeology.api.materials.material_bases;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialCrystalStructure;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.MaterialUseType;

public abstract class MaterialCrystalBase extends Material {
    public abstract MaterialCrystalStructure getCrystalStructure();

	@Override
	public boolean hasUsetype(MaterialUseType useType)
	{
		switch(useType)
		{
			case GEODE:
				return true;
			case CRYSTAL:
				return hasCrystal();
			case RAW_CRYSTAL:
				return hasRawCrystal();
			case DUST:
				return hasDust();
			case TINY_DUST:
				return hasTinyDust();
			case STORAGE_BLOCK:
				return hasStorageBlock();
			case DUST_BLOCK:
				return hasDustBlock();
		}
		return false;
	}
    public abstract MaterialCrystalStructure.LatticeStructure getLatticeStructure();


    protected static int baseColor = 0xffffff;

    @Override
    public MaterialTypes getMaterialType() {
        return MaterialTypes.CRYSTAL;
    }

    public static int getStaticColor() {
        return baseColor;
    }

    @Override
    public net.minecraft.block.material.Material getBlockMaterial() {
        return net.minecraft.block.material.Material.ROCK;
    }

    public boolean hasRawCrystal() {
        return false;
    }

    public boolean hasCrystal() {
        return true;
    }

    public boolean hasDust() {
        return true;
    }

    public boolean hasTinyDust() {
        return true;
    }

    public boolean hasStorageBlock() {
        return true;
    }

    public boolean hasDustBlock() {
        return true;
    }

    @Override
    public String getSpecialSubtypeModelName(MaterialUseType useType) {
        if (useType == MaterialUseType.CRYSTAL || useType == MaterialUseType.RAW_CRYSTAL) {
            String s = getLatticeStructure().name().toLowerCase();
            return s.substring(0, s.length() - 1);
        } else
            return super.getSpecialSubtypeModelName(useType);
    }
}
