package com.igteam.immersive_geology.common.block.blocks.multiblocks;

import com.igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import com.igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import net.minecraft.util.ResourceLocation;

public class ReverberationFurnaceBlock extends IGMetalMultiblock<ReverberationFurnaceTileEntity> {
    public ReverberationFurnaceBlock(){
        super("reverberation_furnace", () -> IGTileTypes.REV_FURNACE.get());
    }

    @Override
    public MaterialInterface<?> getMachineMaterial() {
        return StoneEnum.Stone;
    }
}
