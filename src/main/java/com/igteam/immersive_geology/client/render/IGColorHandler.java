package com.igteam.immersive_geology.client.render;

import igteam.immersive_geology.block.IGBlockType;
import igteam.immersive_geology.item.IGItemType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;

import javax.annotation.Nullable;

public class IGColorHandler implements IItemColor, IBlockColor {

    public static IGColorHandler INSTANCE = new IGColorHandler();

    @Override
    public int getColor(BlockState state, @Nullable IBlockDisplayReader getter, @Nullable BlockPos pos, int index) {
        if(state.getBlock() instanceof IGBlockType) {
            IGBlockType type = (IGBlockType) state.getBlock();
            return type.getColourForIGBlock(index);
        }
        return 0xffffff;
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if(stack.getItem() instanceof IGItemType) {
            IGItemType type = (IGItemType) stack.getItem();
            return type.getColourForIGItem(stack, tintIndex);
        }
        return 0xffffff;
    }
}
