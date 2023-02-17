package com.igteam.immersive_geology.common.block.helper;

import com.igteam.immersive_geology.client.menu.ItemSubGroup;
import com.igteam.immersive_geology.core.material.helper.IFlagType;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import com.igteam.immersive_geology.core.material.helper.MaterialTexture;
import net.minecraft.world.level.block.Block;

import java.util.Collection;

public interface IGBlockType {
    Block getBlock();

    Collection<MaterialInterface<?>> getMaterials();
    MaterialInterface<?> getMaterial(MaterialTexture t);
    IFlagType<?> getFlag();

    ItemSubGroup getGroup();

    int getColor(int index);
}
