package com.igteam.immersivegeology.core.material.helper.flags;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;

public enum ItemCategoryFlags implements IFlagType<ItemCategoryFlags> {
    INGOT(1),
    WIRE(1),
    GEAR(1),
    ROD(1),
    CLAY(0),
    DUST(1),
    FUEL(1),
    SLAG(1),
    PLATE(1),
    RAW_ORE(0),
    NUGGET(1),
    CRYSTAL(1),
    COMPOUND_DUST(1),
    CRUSHED_ORE(1),
    METAL_OXIDE(1),
    DIRTY_CRUSHED_ORE(1),
    BUCKET(3);

    private final int groupOrdinal;

    ItemCategoryFlags(int group){
        groupOrdinal = group;
    }

    @Override
    public ItemCategoryFlags getValue() {
        return this;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return ItemSubGroup.values()[groupOrdinal];
    }
}
