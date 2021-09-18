package com.igteam.immersive_geology.common.block.blocks;

import com.igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import com.igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import com.igteam.immersive_geology.core.registration.IGTileTypes;

public class ChemicalVatBlock extends IGMetalMultiblock<ChemicalVatTileEntity> {
    public ChemicalVatBlock(){
        super("chemicalvat", () -> IGTileTypes.VAT.get());
    }

    
}
