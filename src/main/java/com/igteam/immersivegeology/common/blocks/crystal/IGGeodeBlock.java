package com.igteam.immersivegeology.common.blocks.crystal;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialCrystalStructure;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialCrystalBase;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public class IGGeodeBlock extends IGMaterialBlock {
    public IGGeodeBlock(MaterialUseType use_type, Material material)
    {
        super(use_type, material);
        this.setDefaultState((BlockState)this.getDefaultState().with(NATURAL, false));
    }
}
