/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item.helper;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;

import java.util.Collection;

public interface IGFlagItem {
    IFlagType<?> getFlag();
    int getColor(int index);
    ItemSubGroup getSubGroup();
    Collection<MaterialInterface<?>> getMaterials();

    MaterialInterface<?> getMaterial(MaterialTexture base);
}
