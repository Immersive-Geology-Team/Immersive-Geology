package com.igteam.immersive_geology.common.item.multiblock;

import blusunrize.immersiveengineering.api.IEProperties;
import com.igteam.immersive_geology.common.block.blocks.multiblocks.BloomeryBlock;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import igteam.immersive_geology.block.IGBlockType;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent("machine.immersive_geology.bloomery");
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        if(state.hasProperty(IEProperties.MULTIBLOCKSLAVE))
            return false;
        return super.onBlockPlaced(pos, worldIn, player, stack, state);
    }
}
