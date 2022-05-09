package com.igteam.immersive_geology.common.item.multiblock;

import blusunrize.immersiveengineering.api.IEProperties;
import com.igteam.immersive_geology.common.block.blocks.multiblocks.BloomeryBlock;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import igteam.immersive_geology.block.IGBlockType;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class IGBloomeryBlockItem extends IGGenericBlockItem {

    public IGBloomeryBlockItem(IGBlockType b, MaterialInterface<?> m, ItemPattern p) {
        super(b, m, p);
    }

    @Override
    protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
        Block b = state.getBlock();
        if(b instanceof BloomeryBlock){
            BloomeryBlock bloomeryBlock = (BloomeryBlock) b;
            if(!bloomeryBlock.canIEBlockBePlaced(state, context))
                return false;
            boolean ret = super.placeBlock(context, state);
            if(ret) bloomeryBlock.onIEBlockPlacedBy(context, state);
            return ret;
        }
        return super.placeBlock(context, state);
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        if(state.hasProperty(IEProperties.MULTIBLOCKSLAVE))
            return false;
        return super.onBlockPlaced(pos, worldIn, player, stack, state);
    }
}
