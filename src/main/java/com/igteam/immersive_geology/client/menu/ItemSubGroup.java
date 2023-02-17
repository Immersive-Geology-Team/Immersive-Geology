package com.igteam.immersive_geology.client.menu;

import com.igteam.immersive_geology.core.material.MetalEnum;
import com.igteam.immersive_geology.core.material.helper.*;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum ItemSubGroup {
    natural(BlockCategoryFlags.RAW_ORE_BLOCK, MetalEnum.Platinum),
    processed(ItemCategoryFlags.INGOT, MetalEnum.Chromium),
    decoration(BlockCategoryFlags.SHEETMETAL_BLOCK, MetalEnum.Tin),
    misc(ItemCategoryFlags.CRYSTAL, MetalEnum.Tin);

    private final IFlagType<?> flag;
    private final MaterialInterface<?> material;

    ItemSubGroup(IFlagType<?> flag, MaterialInterface<?> material) {
        this.flag = flag;
        this.material = material;
    }

    @NonNull
    IFlagType<?> getFlag() {
        return flag;
    }

    MaterialInterface<?> getMaterial() {
        return material;
    }
}
