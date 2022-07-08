package igteam.immersive_geology.common.block.blocks.multiblocks;

import igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import igteam.immersive_geology.core.registration.IGTileTypes;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.helper.MaterialInterface;

public class ReverberationFurnaceBlock extends IGMetalMultiblock<ReverberationFurnaceTileEntity> {
    public ReverberationFurnaceBlock(){
        super("reverberation_furnace", () -> IGTileTypes.REV_FURNACE.get());
    }

    @Override
    public MaterialInterface<?> getMachineMaterial() {
        return StoneEnum.Stone;
    }
}
