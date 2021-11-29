package com.igteam.immersive_geology.common.block.blocks;

import com.igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import com.igteam.immersive_geology.common.block.tileentity.CrystallizerTileEntity;
import com.igteam.immersive_geology.common.block.tileentity.RotaryKilnTileEntity;
import com.igteam.immersive_geology.core.registration.IGTileTypes;

public class RotaryKilnBlock extends IGMetalMultiblock<RotaryKilnTileEntity> {
    public RotaryKilnBlock(){
        super("rotarykiln", () -> IGTileTypes.ROTARYKILN.get());
    }
}
