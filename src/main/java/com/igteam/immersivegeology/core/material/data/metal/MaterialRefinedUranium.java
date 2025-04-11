/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.types.MaterialRadioactiveMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import net.minecraft.world.item.Rarity;

import java.util.function.BiFunction;

public class MaterialRefinedUranium extends MaterialRadioactiveMetal
{
    public MaterialRefinedUranium() {
        super();
        // Remove Everything
        removeMaterialFlags(ItemCategoryFlags.values());
        removeMaterialFlags(BlockCategoryFlags.values());
        addFlags(ItemCategoryFlags.INGOT, BlockCategoryFlags.STORAGE_BLOCK);
        this.materialRarity = Rarity.EPIC;
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff759068));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.ORTHORHOMBIC;
    }

    @Override
    public int heatValue()
    {
        return 2500;
    }
}
