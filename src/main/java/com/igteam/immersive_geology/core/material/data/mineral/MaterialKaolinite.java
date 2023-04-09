package com.igteam.immersive_geology.core.material.data.mineral;

import com.igteam.immersive_geology.core.material.data.types.MaterialMineral;
import com.igteam.immersive_geology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.flags.ItemCategoryFlags;

import java.util.function.Function;

public class MaterialKaolinite extends MaterialMineral {

    public MaterialKaolinite() {
        super();
        removeMaterialFlags(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.DIRTY_CRUSHED_ORE, BlockCategoryFlags.ORE_BLOCK, BlockCategoryFlags.RAW_ORE_BLOCK);
        addFlags(ItemCategoryFlags.CLAY, BlockCategoryFlags.STORAGE_BLOCK);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xE5DFD1));
    }
}
