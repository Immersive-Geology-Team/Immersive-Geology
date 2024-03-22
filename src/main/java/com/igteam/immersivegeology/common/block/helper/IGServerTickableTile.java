package com.igteam.immersivegeology.common.block.helper;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface IGServerTickableTile {
    void tickServer();

    static <T extends BlockEntity & IGServerTickableTile> BlockEntityTicker<T> makeTicker(){
        return (level, pos, state, te) -> te.tickServer();
    }
}
