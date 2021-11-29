package com.igteam.immersive_geology.common.block.blocks;

import com.igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import com.igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import com.igteam.immersive_geology.core.registration.IGTileTypes;

public class ReverberationFurnaceBlock extends IGMetalMultiblock<ReverberationFurnaceTileEntity> {
    public ReverberationFurnaceBlock(){
        super("reverberation_furnace", () -> IGTileTypes.REV_FURNACE.get());
    }
}
