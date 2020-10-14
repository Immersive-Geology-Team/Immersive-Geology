package com.igteam.immersivegeology.common.world.help;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class IGBlockHelpers {
    public static boolean isRawStone(BlockState blockState) {
        Block providedBlock = blockState.getBlock();
        boolean isRock = false;
        if(providedBlock instanceof IGMaterialBlock){
            IGMaterialBlock igBlock = (IGMaterialBlock) providedBlock;
            isRock = (igBlock.subtype == MaterialUseType.ROCK || igBlock.subtype == MaterialUseType.MOSS_ROCK);
        }
        return isRock;
    }
}
