/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.helper;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.Map;

public interface IGBlockType {
    Block getBlock();

    Collection<MaterialInterface<?>> getMaterials();
    MaterialInterface<?> getMaterial(MaterialTexture t);
    IFlagType<?> getFlag();

    ItemSubGroup getGroup();

    Map<MaterialTexture, MaterialInterface<?>> getMaterialMap();
    int getColor(int index);
}
