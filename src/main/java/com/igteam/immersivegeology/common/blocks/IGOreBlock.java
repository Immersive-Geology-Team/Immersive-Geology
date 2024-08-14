package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.client.IGClientRenderHandler;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;

public class IGOreBlock extends IGGenericBlock {
    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial) {
        super(flag, baseMaterial);
        this.materialMap.put(MaterialTexture.overlay, oreMaterial);
        IGClientRenderHandler.setRenderType(this, IGClientRenderHandler.RenderTypeSkeleton.CUTOUT_MIPPED);
    }
}