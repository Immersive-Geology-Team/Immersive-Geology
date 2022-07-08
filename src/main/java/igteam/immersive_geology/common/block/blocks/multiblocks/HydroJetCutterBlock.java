package igteam.immersive_geology.common.block.blocks.multiblocks;

import igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import igteam.immersive_geology.common.block.tileentity.HydroJetCutterTileEntity;
import igteam.immersive_geology.core.registration.IGTileTypes;


public class HydroJetCutterBlock extends IGMetalMultiblock<HydroJetCutterTileEntity> {
    //Used in IGMultiblockRegistrationHolder - used to set the block reference, Dependent on a IGTileType to function. See IGTileTypes
    public HydroJetCutterBlock(){
        //USE THE SAME name as the TileType eg 'hydrojet_cutter' here is the same as what the TileType reference is registered under
        super("hydrojet_cutter", () -> IGTileTypes.HYDROJET.get());
    }
}
