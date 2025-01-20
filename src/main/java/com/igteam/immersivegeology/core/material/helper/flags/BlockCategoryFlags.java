package com.igteam.immersivegeology.core.material.helper.flags;

import com.igteam.immersivegeology.client.IGClientRenderHandler.RenderTypeSkeleton;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;

public enum BlockCategoryFlags implements IFlagType<BlockCategoryFlags> {
    STORAGE_BLOCK(4),
    ORE_BLOCK(0),
    GEODE_BLOCK(0),
    DEFAULT_BLOCK(4),
    SLAB(4),
    DUST_BLOCK(1),
    SHEETMETAL_BLOCK(4),
    STAIRS(4),
    FLUID(3),
    SLURRY(3),
    MISC(4);

    private final int groupOrdinal;

    BlockCategoryFlags(int ordinal) {
        groupOrdinal = ordinal;
    }
    @Override
    public BlockCategoryFlags getValue() {
        return this;
    }

    @Override
    public String getTagPrefix()
    {
        return "";
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return ItemSubGroup.values()[groupOrdinal];
    }

    @Override
    public RenderTypeSkeleton getRenderType()
    {
        if(this == ORE_BLOCK) return RenderTypeSkeleton.CUTOUT_MIPPED;
        return IFlagType.super.getRenderType();
    }
}
