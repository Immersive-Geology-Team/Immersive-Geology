/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.common.item.IGGenericDrillHead.DrillHeadProps;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetalAlloy;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;

import java.util.function.BiFunction;

public class MaterialTungstenCarbide extends MaterialMetalAlloy
{

    public MaterialTungstenCarbide() {
        super();
        removeMaterialFlags(ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.COMPOUND_DUST, BlockCategoryFlags.SLURRY);
        addFlags(ItemCategoryFlags.POWDER);
        addFlags(ItemCategoryFlags.DRILL_HEAD);
    }

    @Override
    public DrillHeadProps drillHeadInstance()
    {
        return new DrillHeadProps(getName(), getItemTag(ItemCategoryFlags.INGOT), 3, 1, Tiers.NETHERITE, 9.0f, 11, 96000, () -> new ResourceLocation(IGLib.MODID, "item/colored/" + getName() + "/drill"));
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff264e4c));
    }
}
