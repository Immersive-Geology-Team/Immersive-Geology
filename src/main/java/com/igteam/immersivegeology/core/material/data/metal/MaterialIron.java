/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Biomes;

import java.util.Optional;
import java.util.function.Function;

public class MaterialIron extends MaterialNativeMetal {

    public MaterialIron() {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);

        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.ROD, ItemCategoryFlags.PLATE, ItemCategoryFlags.DUST);
        addExistingFlag(ModFlags.AD_ASTRA, ItemCategoryFlags.ROD, ItemCategoryFlags.PLATE);
        addExistingFlag(ModFlags.MINECRAFT, ItemCategoryFlags.INGOT, ItemCategoryFlags.NUGGET);
        addExistingFlag(ModFlags.MINECRAFT, BlockCategoryFlags.STORAGE_BLOCK);


        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.CONFIG = new MaterialMineral.MineralConfig(3,10,1,0,60,1, Optional.of(Biomes.IS_COLD));
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xd8dada));
    }
}
