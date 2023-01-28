package com.igteam.immersive_geology.core.material.helper;

import com.igteam.immersive_geology.client.menu.ItemSubGroup;

public enum ItemCategoryFlags implements IFlagType<ItemCategoryFlags> {
    INGOT(ItemSubGroup.processed),
    WIRE(ItemSubGroup.processed),
    GEAR(ItemSubGroup.processed),
    ROD(ItemSubGroup.processed),
    CLAY(ItemSubGroup.natural),
    DUST(ItemSubGroup.processed),
    FUEL(ItemSubGroup.processed),
    SLAG(ItemSubGroup.processed),
    PLATE(ItemSubGroup.processed),
    RAW_ORE(ItemSubGroup.natural),
    NUGGET(ItemSubGroup.processed),
    CRYSTAL(ItemSubGroup.processed),
    COMPOUND_DUST(ItemSubGroup.processed),
    CRUSHED_ORE(ItemSubGroup.processed),
    METAL_OXIDE(ItemSubGroup.processed),
    DIRTY_CRUSHED_ORE(ItemSubGroup.processed),
    BUCKET(ItemSubGroup.misc);

    private final ItemSubGroup subgroup;

    ItemCategoryFlags(ItemSubGroup group){
        subgroup = group;
    }

    @Override
    public ItemCategoryFlags getValue() {
        return this;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return subgroup;
    }
}
