package com.igteam.immersive_geology.client;

import igteam.immersive_geology.item.IGItemType;
import igteam.immersive_geology.materials.helper.IGRegistryProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class IGColorHandlers implements ItemColor, BlockColor {

    public static IGColorHandlers INSTANCE = new IGColorHandlers();

    public static void register(){
        for(Item i : IGRegistryProvider.IG_ITEM_REGISTRY.values()){
            if(i instanceof IGItemType){
                Minecraft.getInstance().getItemColors().register(INSTANCE, i);
            }
        }
    }

    @Override
    public int getColor(BlockState p_92567_, @Nullable BlockAndTintGetter p_92568_, @Nullable BlockPos p_92569_, int p_92570_) {
        return 0;
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if(stack.getItem() instanceof IGItemType)
            return ((IGItemType) stack.getItem()).getColourForIGItem(stack, tintIndex);
        return 0xffffff;
    }
}
