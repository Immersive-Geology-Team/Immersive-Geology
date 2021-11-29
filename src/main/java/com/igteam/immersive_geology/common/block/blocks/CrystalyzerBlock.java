package com.igteam.immersive_geology.common.block.blocks;

import com.igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import com.igteam.immersive_geology.common.block.tileentity.CrystalyzerTileEntity;
import com.igteam.immersive_geology.common.block.tileentity.GravitySeparatorTileEntity;
import com.igteam.immersive_geology.core.registration.IGTileTypes;

public class CrystalyzerBlock extends IGMetalMultiblock<CrystalyzerTileEntity> {
    public CrystalyzerBlock(){
        super("crystalyzer", () -> IGTileTypes.CRYSTALYZER.get());
    }
}
