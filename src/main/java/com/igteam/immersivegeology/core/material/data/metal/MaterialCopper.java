package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.*;

import java.util.function.Function;

public class MaterialCopper extends MaterialNativeMetal {

    public MaterialCopper() {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);

        addExistingFlag(ModFlags.MINECRAFT, ItemCategoryFlags.INGOT);
        addExistingFlag(ModFlags.MINECRAFT, BlockCategoryFlags.STORAGE_BLOCK);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, BlockCategoryFlags.SHEETMETAL_BLOCK, BlockCategoryFlags.STAIRS, BlockCategoryFlags.SLAB);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.WIRE, ItemCategoryFlags.DUST, ItemCategoryFlags.PLATE, ItemCategoryFlags.NUGGET);
        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
        addExistingFlag(ModFlags.TFC, ItemCategoryFlags.POOR_ORE, ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.RICH_ORE,  ItemCategoryFlags.ROD);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xe39919));
    }
}
