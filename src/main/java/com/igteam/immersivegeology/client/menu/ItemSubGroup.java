/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum ItemSubGroup {
    natural(ItemCategoryFlags.NORMAL_ORE, MineralEnum.Vanadinite),
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
