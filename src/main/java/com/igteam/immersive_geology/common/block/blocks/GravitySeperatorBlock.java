package com.igteam.immersive_geology.common.block.blocks;

import com.igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import com.igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import com.igteam.immersive_geology.common.block.tileentity.GravitySeperatorTileEntity;
import com.igteam.immersive_geology.core.registration.IGTileTypes;

public class GravitySeperatorBlock extends IGMetalMultiblock<GravitySeperatorTileEntity> {
    public GravitySeperatorBlock(){
        super("gravityseperator", () -> IGTileTypes.GRAVITY.get());
    }
}
