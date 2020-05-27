package com.igteam.immersivegeology.api.materials.material_bases;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.EnumMaterials;

public abstract class MaterialRockBase extends Material
{
    public abstract EnumRockType getRockType();

    @Override
    public boolean hasSubtype(MaterialUseType useType)
    {
    	switch(useType) {
    		case ROCK:
    		return true;
    	}
        return false;
    }

    @Override
    public MaterialTypes getMaterialType()
    {
        return MaterialTypes.STONE;
    }
    
    public EnumMaterials getDropChunk() {
		return EnumMaterials.Limestone;
    }
    
    @Override
    public net.minecraft.block.material.Material getBlockMaterial()
    {
        return net.minecraft.block.material.Material.ROCK;
    }

    public enum EnumRockType
    {
    	IGNEOUS,
    	SEDIMENTARY,
    	METAMORPHIC;

		public String getName() {
			// TODO Auto-generated method stub
			return toString().toLowerCase();
		}
    }
}
