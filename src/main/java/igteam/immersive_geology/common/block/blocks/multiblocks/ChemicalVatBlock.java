package igteam.immersive_geology.common.block.blocks.multiblocks;

import igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import igteam.immersive_geology.core.registration.IGTileTypes;

public class ChemicalVatBlock extends IGMetalMultiblock<ChemicalVatTileEntity> {

    public ChemicalVatBlock(){
        super("chemicalvat", () -> IGTileTypes.VAT.get());
    }
}
