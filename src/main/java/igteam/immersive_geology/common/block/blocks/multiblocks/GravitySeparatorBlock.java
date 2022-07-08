package igteam.immersive_geology.common.block.blocks.multiblocks;

import igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import igteam.immersive_geology.common.block.tileentity.GravitySeparatorTileEntity;
import igteam.immersive_geology.core.registration.IGTileTypes;

public class GravitySeparatorBlock extends IGMetalMultiblock<GravitySeparatorTileEntity> {
    public GravitySeparatorBlock(){
        super("gravityseparator", () -> IGTileTypes.GRAVITY.get());
    }
}