package com.igteam.immersivegeology.common.block;

import com.igteam.immersivegeology.client.IGClientRenderHandler;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

public class IGOreBlock extends IGGenericBlock {

    protected final OreRichness richness;

    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial) {
        this(flag, baseMaterial, oreMaterial, OreRichness.POOR);
    }

    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial, OreRichness richness) {
        super(flag, baseMaterial);
        this.materialMap.put(MaterialTexture.overlay, oreMaterial);
        IGClientRenderHandler.setRenderType(this, IGClientRenderHandler.RenderTypeSkeleton.CUTOUT_MIPPED);
        this.richness = richness;
    }

    public OreRichness getOreRichness()
    {
        return richness;
    }

    public StoneFormation getStoneFormation()
    {
        if(materialMap.get(MaterialTexture.base).instance() instanceof MaterialStone stone){
            return stone.getStoneFormation();
        }
        return null;
    }

    public enum OreRichness
    {
        POOR,
        NORMAL,
        RICH;
    }
}