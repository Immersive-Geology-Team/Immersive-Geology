package com.igteam.immersive_geology.common.item;

import igteam.immersive_geology.item.IGItemType;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.menu.ItemSubGroup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class IGGenericBlockItem extends BlockItem implements IGItemType {
    public IGGenericBlockItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }

    @Override
    public int getColourForIGItem(ItemStack stack, int pass) {
        return 0;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return null;
    }

    @Override
    public Set<MaterialInterface> getMaterials() {
        return null;
    }

    @Override
    public MaterialPattern getPattern() {
        return null;
    }
}
