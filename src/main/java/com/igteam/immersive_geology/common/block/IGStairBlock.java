package com.igteam.immersive_geology.common.block;

import com.igteam.immersive_geology.client.IGClientRenderHandler;
import com.igteam.immersive_geology.core.material.helper.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;

public class IGStairBlock extends IGGenericBlock {
    public IGStairBlock(BlockCategoryFlags flag, MaterialInterface<?>... materials) {
        super(flag, materials);
    }

}
