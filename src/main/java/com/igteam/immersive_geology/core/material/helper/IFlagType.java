package com.igteam.immersive_geology.core.material.helper;

import com.igteam.immersive_geology.client.menu.ItemSubGroup;

import java.util.ArrayList;
import java.util.List;

public interface IFlagType<T extends Enum<T>> {
    T getValue();

    static List<IFlagType<?>> getAllRegistryFlags(){
        List<IFlagType<?>> list = new ArrayList<>();
        list.addAll(List.of(BlockCategoryFlags.values()));
        list.addAll(List.of(ItemCategoryFlags.values()));
        return list;
    }

    default String getRegistryKey(MaterialHelper material) {
        return getValue().name().toLowerCase() + "_" + material.getName().toLowerCase();
    }

    default String getRegistryKey(MaterialInterface<?> material) {
        return getRegistryKey(material.instance());
    }

    default ItemSubGroup getSubGroup() {
        return ItemSubGroup.values()[0];
    };

    default String getName(){
        return getValue().name().toLowerCase();
    }
}
