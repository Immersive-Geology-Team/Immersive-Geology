package com.igteam.immersive_geology.common.configuration.helper;

import com.igteam.immersive_geology.core.material.MetalEnum;
import com.igteam.immersive_geology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersive_geology.core.material.helper.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.ItemCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface ConfigurationHelper {
    Function<MaterialInterface<?>, List<ItemCategoryFlags>> defaultItemFlags = (material) -> {
        List<ItemCategoryFlags> list = new ArrayList<>();
        if(material instanceof MetalEnum metal) {
            // This sets things for the default items
            list.add(ItemCategoryFlags.INGOT);
            list.add(ItemCategoryFlags.GEAR);
            list.add(ItemCategoryFlags.PLATE);
            list.add(ItemCategoryFlags.NUGGET);

            if(metal.instance() instanceof MaterialNativeMetal) {
                list.add(ItemCategoryFlags.RAW_ORE);
                list.add(ItemCategoryFlags.DIRTY_CRUSHED_ORE);
                list.add(ItemCategoryFlags.CRUSHED_ORE);
            }
        }

        return list;
    };

    Function<MaterialInterface<?>, List<BlockCategoryFlags>> defaultBlockFlags = (material) -> {
        List<BlockCategoryFlags> list = new ArrayList<>();
        if(material instanceof MetalEnum metal) {
            // This sets things for the default items
            list.add(BlockCategoryFlags.STORAGE_BLOCK);
            list.add(BlockCategoryFlags.SHEETMETAL_BLOCK);
            if(metal.instance() instanceof MaterialNativeMetal) {
                list.add(BlockCategoryFlags.RAW_ORE_BLOCK);
                list.add(BlockCategoryFlags.ORE_BLOCK);
            }
        }

        return list;
    };
}
