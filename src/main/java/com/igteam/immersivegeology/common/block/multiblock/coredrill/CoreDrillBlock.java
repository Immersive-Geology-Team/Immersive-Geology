package com.igteam.immersivegeology.common.block.multiblock.coredrill;

import com.igteam.immersivegeology.common.block.multiblock.IGMetalMultiblock;
import com.igteam.immersivegeology.core.registration.IGTileTypes;

public class CoreDrillBlock extends IGMetalMultiblock<CoreDrillTileEntity> {
    public CoreDrillBlock() {
        super(IGTileTypes.CORE_DRILL);
    }
}
