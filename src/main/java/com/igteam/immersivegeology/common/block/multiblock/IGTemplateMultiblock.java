package com.igteam.immersivegeology.common.block.multiblock;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class IGTemplateMultiblock extends IETemplateMultiblock {
    private final Supplier<? extends Block> baseState;

    public IGTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, Supplier<? extends Block> baseState){
        super(loc, masterFromOrigin, triggerFromOrigin, size, new IEBlocks.BlockEntry<>(baseState.get()));
        this.baseState = baseState;
    }


    public @NotNull Block getBaseBlock() {
        return baseState.get();
    }

    @Override
    public float getManualScale() {
        return 0;
    }
}
