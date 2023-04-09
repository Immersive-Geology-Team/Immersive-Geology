package com.igteam.immersive_geology.common.block;

import com.igteam.immersive_geology.client.IGClientRenderHandler;
import com.igteam.immersive_geology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.material.MaterialInterface;
import com.igteam.immersive_geology.core.material.helper.material.MaterialTexture;

public class IGOreBlock extends IGGenericBlock {
    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial) {
        super(flag, baseMaterial);
        this.materialMap.put(MaterialTexture.overlay, oreMaterial);
        IGClientRenderHandler.setRenderType(this, IGClientRenderHandler.RenderTypeSkeleton.CUTOUT_MIPPED);
    }
}
