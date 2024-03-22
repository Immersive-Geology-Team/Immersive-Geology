package com.igteam.immersivegeology.common.block.helper;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface IGClientTickableTile {
    void tickClient();

    static <T extends BlockEntity & IGClientTickableTile> BlockEntityTicker<T> makeTicker(){
        return (level, pos, state, te) -> te.tickClient();
    }
}
