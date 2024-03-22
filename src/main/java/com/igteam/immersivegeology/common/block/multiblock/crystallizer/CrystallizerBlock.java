package com.igteam.immersivegeology.common.block.multiblock.crystallizer;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import com.igteam.immersivegeology.common.block.multiblock.IGMetalMultiblock;
import com.igteam.immersivegeology.core.registration.IGTileTypes;

public class CrystallizerBlock extends IGMetalMultiblock<CrystallizerTileEntity> {
    public CrystallizerBlock() {
        super(IGTileTypes.CRYSTALLIZER);
    }
}
