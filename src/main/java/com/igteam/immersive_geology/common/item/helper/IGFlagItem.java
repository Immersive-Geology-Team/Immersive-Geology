package com.igteam.immersive_geology.common.item.helper;

import com.igteam.immersive_geology.client.menu.ItemSubGroup;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.material.MaterialInterface;
import com.igteam.immersive_geology.core.material.helper.material.MaterialTexture;

import java.util.Collection;

public interface IGFlagItem {
    IFlagType<?> getFlag();

    ItemSubGroup getSubGroup();
    Collection<MaterialInterface<?>> getMaterials();

    MaterialInterface<?> getMaterial(MaterialTexture base);
}
