package com.igteam.immersivegeology.common.blocks.machines;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGTileBlock;
import com.igteam.immersivegeology.common.materials.EnumMaterials;

public class IGCrudeForgeCore extends IGTileBlock {

    public IGCrudeForgeCore() {
        super("crude_forge", MaterialUseType.ROCK, EnumMaterials.Marble.material);
    }

}
