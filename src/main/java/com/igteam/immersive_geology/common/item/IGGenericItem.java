package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.client.menu.IGItemGroup;
import com.igteam.immersive_geology.client.menu.ItemSubGroup;
import com.igteam.immersive_geology.common.item.helper.IGFlagItem;
import com.igteam.immersive_geology.common.item.helper.IGItemType;
import com.igteam.immersive_geology.core.material.helper.ItemCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import com.igteam.immersive_geology.core.material.helper.MaterialTexture;
import net.minecraft.world.item.Item;

import java.util.*;

public class IGGenericItem extends Item implements IGItemType, IGFlagItem {

    private final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
    private final ItemCategoryFlags category;

    public IGGenericItem(ItemCategoryFlags flag, MaterialInterface<?> material) {
        super(new Properties().tab(IGItemGroup.IGGroup));
        this.materialMap.put(MaterialTexture.base, material);
        this.category = flag;
    }

    public int getColor(int index) {
        if (index >= materialMap.values().size()) index = materialMap.values().size() - 1;
        //let's use last available colour. map could not be empty
        return materialMap.get(MaterialTexture.values()[index]).getColor(category);
    }

    @Override
    public ItemCategoryFlags getFlag() {
        return category;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return category.getSubGroup();
    }

    @Override
    public Collection<MaterialInterface<?>> getMaterials() {
        return materialMap.values();
    }

    @Override
    public MaterialInterface<?> getMaterial(MaterialTexture t) {
        return materialMap.get(t);
    }
}
