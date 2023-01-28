package com.igteam.immersive_geology.client.menu;

import com.igteam.immersive_geology.core.material.MetalEnum;
import com.igteam.immersive_geology.core.material.helper.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.IFlagType;
import com.igteam.immersive_geology.core.material.helper.ItemCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum ItemSubGroup {
    natural(ItemCategoryFlags.RAW_ORE, MetalEnum.Tin),
    processed(ItemCategoryFlags.INGOT, MetalEnum.Platinum),
    decoration(ItemCategoryFlags.PLATE, MetalEnum.Chromium),
    misc(ItemCategoryFlags.GEAR, MetalEnum.Tin);

    private final ItemCategoryFlags flag;
    private final MaterialInterface<?> material;

    ItemSubGroup(ItemCategoryFlags flag, MaterialInterface<?> material) {
        this.flag = flag;
        this.material = material;
    }

    @NonNull
    ItemCategoryFlags getFlag() {
        return flag;
    }

    MaterialInterface<?> getMaterial() {
        return material;
    }
}
