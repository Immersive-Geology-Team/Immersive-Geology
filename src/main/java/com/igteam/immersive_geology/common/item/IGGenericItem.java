package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.client.menu.IGItemGroup;
import com.igteam.immersive_geology.client.menu.ItemSubGroup;
import com.igteam.immersive_geology.common.item.helper.IGFlagItem;
import com.igteam.immersive_geology.common.item.helper.IGItemType;
import com.igteam.immersive_geology.core.material.helper.ItemCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.*;

public class IGGenericItem extends Item implements IGItemType, IGFlagItem {

    private final List<MaterialInterface<?>> materials;
    private final ItemCategoryFlags category;

    public IGGenericItem(ItemCategoryFlags flag, MaterialInterface<?>... materials) {
        super(new Properties().tab(IGItemGroup.IGGroup));
        this.materials = List.of(materials);
        this.category = flag;
    }

    public int getColor(int index) {
        return materials.get(index % materials.size()).getColor(this.category);
    }

    @Override
    public ItemCategoryFlags getFlag() {
        return category;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return category.getSubGroup();
    }

    @Override
    public List<MaterialInterface<?>> getMaterials() {
        return materials;
    }


}
