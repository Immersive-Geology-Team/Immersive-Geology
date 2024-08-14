package com.igteam.immersivegeology.core.material.configuration;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface ConfigurationHelper {
    Function<GeologyMaterial, List<ItemCategoryFlags>> defaultItemFlags = (material) -> {
        List<ItemCategoryFlags> list = new ArrayList<>();

        if(material instanceof MaterialMetal metal) {
            // This sets things for the default items
            list.add(ItemCategoryFlags.INGOT);
            list.add(ItemCategoryFlags.GEAR);
            list.add(ItemCategoryFlags.PLATE);
            list.add(ItemCategoryFlags.NUGGET);
            list.add(ItemCategoryFlags.CRYSTAL);

            if(metal instanceof MaterialNativeMetal) {
                list.add(ItemCategoryFlags.RAW_ORE);
                list.add(ItemCategoryFlags.DIRTY_CRUSHED_ORE);
                list.add(ItemCategoryFlags.CRUSHED_ORE);
            }
        }

        if(material instanceof MaterialMineral) {
            list.add(ItemCategoryFlags.RAW_ORE);
            list.add(ItemCategoryFlags.DIRTY_CRUSHED_ORE);
            list.add(ItemCategoryFlags.CRUSHED_ORE);
        }

        return list;
    };

    Function<GeologyMaterial, List<BlockCategoryFlags>> defaultBlockFlags = (material) -> {
        List<BlockCategoryFlags> list = new ArrayList<>();
        if(material instanceof MaterialMetal metal) {
            // This sets things for the default items
            list.add(BlockCategoryFlags.STORAGE_BLOCK);
            list.add(BlockCategoryFlags.SHEETMETAL_BLOCK);
            list.add(BlockCategoryFlags.STAIRS);
            list.add(BlockCategoryFlags.FLUID);
            if(metal instanceof MaterialNativeMetal) {
                list.add(BlockCategoryFlags.RAW_ORE_BLOCK);
                list.add(BlockCategoryFlags.ORE_BLOCK);
            }
        }

        if(material instanceof MaterialMineral) {
            list.add(BlockCategoryFlags.RAW_ORE_BLOCK);
            list.add(BlockCategoryFlags.ORE_BLOCK);
        }

        return list;
    };
}
