package igteam.immersive_geology.common.block.blocks.multiblocks;

import igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import igteam.immersive_geology.common.block.tileentity.RotaryKilnTileEntity;
import igteam.immersive_geology.core.registration.IGTileTypes;

public class RotaryKilnBlock extends IGMetalMultiblock<RotaryKilnTileEntity> {
    public RotaryKilnBlock(){
        super("rotarykiln", () -> IGTileTypes.ROTARYKILN.get());
    }
}
