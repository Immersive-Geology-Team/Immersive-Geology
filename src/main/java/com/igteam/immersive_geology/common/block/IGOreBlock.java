package com.igteam.immersive_geology.common.block;

import com.igteam.immersive_geology.client.IGClientRenderHandler;
import com.igteam.immersive_geology.core.material.helper.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class IGOreBlock extends IGGenericBlock {
    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?>... materials) {
        super(flag, materials);
        IGClientRenderHandler.setRenderType(this, IGClientRenderHandler.RenderTypeSkeleton.TRANSLUCENT);
    }
}
