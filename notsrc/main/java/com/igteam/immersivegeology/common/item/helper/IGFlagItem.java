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
