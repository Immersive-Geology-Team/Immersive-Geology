package com.igteam.immersivegeology.core.material.helper.flags;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.blocks.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;

import java.util.ArrayList;
import java.util.List;

public interface IFlagType<T extends Enum<T>> {
    T getValue();

    static List<IFlagType<?>> getAllRegistryFlags() {
        List<IFlagType<?>> list = new ArrayList<>();
        list.addAll(List.of(BlockCategoryFlags.values()));
        list.addAll(List.of(ItemCategoryFlags.values()));
        return list;
    }

    default String getRegistryKey(MaterialHelper material) {
        return getValue().name().toLowerCase() + "_" + material.getName().toLowerCase();
    }

    default String getRegistryKey(MaterialHelper ore, MaterialHelper stone) {
        return getValue().name().toLowerCase() + "_" + ore.getName().toLowerCase() + "_" + stone.getName().toLowerCase();
    }

    default String getRegistryKey(MaterialInterface<?> material) {
        return getRegistryKey(material.instance());
    }

    default String getRegistryKey(MaterialInterface<?> ore, MaterialInterface<?> stone) {
        return getRegistryKey(ore.instance(), stone.instance());
    }

    default String getRegistryKey(MaterialInterface<?> ore, MaterialInterface<?> stone, OreRichness richness) {
        return (richness.name().toLowerCase() + "_" + getRegistryKey(ore.instance(), stone.instance()));
    }

    default String getRegistryKey(MaterialHelper ore, MaterialInterface<?> stone, OreRichness richness) {
        return (richness.name().toLowerCase() + "_" + getRegistryKey(ore, stone.instance()));
    }

    default ItemSubGroup getSubGroup() {
        return ItemSubGroup.values()[0];
    };

    default String getName() {
        return getValue().name().toLowerCase();
    }
}
