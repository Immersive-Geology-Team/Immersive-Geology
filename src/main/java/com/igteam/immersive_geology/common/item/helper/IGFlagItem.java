package com.igteam.immersive_geology.common.item.helper;

import com.igteam.immersive_geology.client.menu.ItemSubGroup;
import com.igteam.immersive_geology.core.material.helper.IFlagType;
import com.igteam.immersive_geology.core.material.helper.ItemCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;

import java.util.List;

public interface IGFlagItem {
    ItemCategoryFlags getFlag();

    ItemSubGroup getSubGroup();
    List<MaterialInterface<?>> getMaterials();
}
