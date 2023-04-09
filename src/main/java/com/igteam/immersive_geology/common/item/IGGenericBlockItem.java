package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.client.menu.IGItemGroup;
import com.igteam.immersive_geology.client.menu.ItemSubGroup;
import com.igteam.immersive_geology.common.block.helper.IGBlockType;
import com.igteam.immersive_geology.common.item.helper.IGFlagItem;
import com.igteam.immersive_geology.common.item.helper.IGItemType;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.material.MaterialInterface;
import com.igteam.immersive_geology.core.material.helper.material.MaterialTexture;
import net.minecraft.world.item.BlockItem;

import java.util.*;

public class IGGenericBlockItem extends BlockItem implements IGItemType, IGFlagItem {

    private final IGBlockType block;

    public IGGenericBlockItem(IGBlockType block) {
        super(block.getBlock(), new Properties().tab(IGItemGroup.IGGroup));
        this.block = block;
    }

    @Override
    public IFlagType<?> getFlag() {
        return block.getFlag();
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return block.getGroup();
    }

    @Override
    public Collection<MaterialInterface<?>> getMaterials() {
        return block.getMaterials();
    }

    @Override
    public MaterialInterface<?> getMaterial(MaterialTexture t) {
        return block.getMaterial(t);
    }

    @Override
    public int getColor(int index) {
        return this.block.getColor(index);
    }

}
