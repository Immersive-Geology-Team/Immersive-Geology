/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu;

import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum ItemSubGroup {
    geologic(ItemCategoryFlags.NORMAL_ORE, MineralEnum.Chalcopyrite),
    components(ItemCategoryFlags.GEAR, MetalEnum.Manganese),
    pyrometallurgy(ItemCategoryFlags.PELLET, MineralEnum.Cuprite),
    hydrometallurgy(ItemCategoryFlags.BUCKET, ChemicalEnum.HydrochloricAcid, MetalEnum.Cobalt),
    structural(ItemCategoryFlags.HAMMER, MetalEnum.StainlessSteel);

    private final IFlagType<?> flag;
    private final MaterialInterface<?> material;
    private final MaterialInterface<?> secondary;

    ItemSubGroup(IFlagType<?> flag, MaterialInterface<?> material) {
        this.flag = flag;
        this.material = material;
        this.secondary = material;
    }

    ItemSubGroup(IFlagType<?> flag, MaterialInterface<?> material, MaterialInterface<?> secondary) {
        this.flag = flag;
        this.material = material;
        this.secondary = secondary;
    }

    @NonNull
    IFlagType<?> getFlag() {
        return flag;
    }

    MaterialInterface<?> getMaterial() {
        return material;
    }
    MaterialInterface<?> getSecondary() {
        return secondary;
    }
}
