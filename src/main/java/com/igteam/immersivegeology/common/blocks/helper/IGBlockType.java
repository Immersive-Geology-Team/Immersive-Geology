package com.igteam.immersivegeology.common.blocks.helper;

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
