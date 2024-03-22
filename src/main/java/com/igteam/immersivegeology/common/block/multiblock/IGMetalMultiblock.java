package com.igteam.immersivegeology.common.block.multiblock;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;
import com.igteam.immersivegeology.common.block.helper.IGCommonTickableTile;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nonnull;

public class IGMetalMultiblock <T extends MultiblockPartBlockEntity<T> & IGCommonTickableTile> extends MetalMultiblockBlock<T> {
    private final MultiblockBEType<T> multiblockBEType;

    public IGMetalMultiblock(MultiblockBEType<T> te){
        super(te, Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(3, 15)
                .requiresCorrectToolForDrops()
                .isViewBlocking((state, blockReader, pos) -> false)
                .noOcclusion()
        );
        this.multiblockBEType = te;
    }

    @Override
    public <E extends BlockEntity> BlockEntityTicker<E> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<E> type){
        return IGRegistrationHolder.createCommonTicker(world.isClientSide, type, multiblockBEType.master());
    }
}
