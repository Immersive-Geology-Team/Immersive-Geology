package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.*;

import java.util.function.Function;

public class MaterialKaolinite extends MaterialMineral {

    public MaterialKaolinite() {
        super();
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);
        removeMaterialFlags(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.DIRTY_CRUSHED_ORE, BlockCategoryFlags.ORE_BLOCK);
        addFlags(ItemCategoryFlags.CLAY, BlockCategoryFlags.STORAGE_BLOCK);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xE5DFD1));
    }
}
