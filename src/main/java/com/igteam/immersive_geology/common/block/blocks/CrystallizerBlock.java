package com.igteam.immersive_geology.common.block.blocks;

import com.igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import com.igteam.immersive_geology.common.block.tileentity.CrystallizerTileEntity;
import com.igteam.immersive_geology.core.registration.IGTileTypes;

public class CrystallizerBlock extends IGMetalMultiblock<CrystallizerTileEntity> {
    public CrystallizerBlock(){
        super("crystallizer", () -> IGTileTypes.CRYSTALLIZER.get());
    }
}
