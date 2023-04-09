package com.igteam.immersive_geology.core.material.helper;

import com.igteam.immersive_geology.client.menu.ItemSubGroup;

public enum BlockCategoryFlags implements IFlagType<BlockCategoryFlags> {
    STORAGE_BLOCK(2),
    ORE_BLOCK(0),
    RAW_ORE_BLOCK(0),
    GEODE_BLOCK(0),
    DEFAULT_BLOCK(2),
    SLAB(2),
    DUST_BLOCK(1),
    SHEETMETAL_BLOCK(2),
    STAIRS(2);

    private final int groupOrdinal;

    BlockCategoryFlags(int ordinal) {
        groupOrdinal = ordinal;
    }
    @Override
    public BlockCategoryFlags getValue() {
        return this;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return ItemSubGroup.values()[groupOrdinal];
    }
}
