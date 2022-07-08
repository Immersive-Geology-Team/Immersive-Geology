package igteam.immersive_geology.common.block.blocks.multiblocks;

import igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import igteam.immersive_geology.common.block.tileentity.CrystallizerTileEntity;
import igteam.immersive_geology.core.registration.IGTileTypes;

public class CrystallizerBlock extends IGMetalMultiblock<CrystallizerTileEntity> {
    public CrystallizerBlock(){
        super("crystallizer", () -> IGTileTypes.CRYSTALLIZER.get());
    }

}
