package com.igteam.immersive_geology.core.material.helper;

import com.igteam.immersive_geology.client.menu.ItemSubGroup;

public enum BlockCategoryFlags implements IFlagType<BlockCategoryFlags> {
    STORAGE_BLOCK,
    STAIRS,
    ORE_BLOCK,
    RAW_ORE,
    GEODE_BLOCK,
    DEFAULT_BLOCK,
    SLAB,
    DUST_BLOCK,
    SHEETMETAL_BLOCK;

    @Override
    public BlockCategoryFlags getValue() {
        return this;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return ItemSubGroup.decoration;
    }
}
