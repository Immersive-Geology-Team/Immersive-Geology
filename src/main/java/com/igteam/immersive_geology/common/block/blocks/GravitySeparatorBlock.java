package com.igteam.immersive_geology.common.block.blocks;

import com.igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import com.igteam.immersive_geology.common.block.tileentity.GravitySeparatorTileEntity;
import com.igteam.immersive_geology.core.registration.IGTileTypes;

public class GravitySeparatorBlock extends IGMetalMultiblock<GravitySeparatorTileEntity> {
    public GravitySeparatorBlock(){
        super("gravityseparator", () -> IGTileTypes.GRAVITY.get());
    }
}
