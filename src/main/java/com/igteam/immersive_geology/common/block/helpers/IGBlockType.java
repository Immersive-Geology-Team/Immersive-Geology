package com.igteam.immersive_geology.common.block.helpers;

import com.igteam.immersive_geology.legacy_api.materials.Material;
import com.igteam.immersive_geology.legacy_api.materials.MaterialUseType;

public interface IGBlockType {
    String getHolderName();
    MaterialUseType getBlockUseType();
    Material getMaterial(BlockMaterialType type);

    MaterialUseType getDropUseType();

    float maxDrops();
    float minDrops();
}
