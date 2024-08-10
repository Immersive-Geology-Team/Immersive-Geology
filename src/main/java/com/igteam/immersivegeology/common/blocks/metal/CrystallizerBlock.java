package com.igteam.immersivegeology.common.blocks.metal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CrystallizerBlock extends Block {
    public CrystallizerBlock() {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK));
    }
}
