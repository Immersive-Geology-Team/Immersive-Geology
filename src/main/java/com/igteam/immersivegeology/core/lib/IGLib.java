/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.lib;

import com.igteam.immersivegeology.core.material.data.enums.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.logging.LogUtils;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class IGLib {
    public static final String MODID = "immersivegeology";
    public static final String VERSION = "1.0.0";

    public static final Logger IG_LOGGER = LogUtils.getLogger();

    // These should probably stay the same
    public static final int SLURRY_TO_CRYSTAL_MB = 100;
    public static final int ACID_TO_SLURRY_AMOUNT = 100;
    public static final int SLURRY_FROM_ACID_AMOUNT = 100;

    public static final float TWO_ACID_USED_MULTIPLIER = 0.5f;
    public static final float THREE_ACID_USED_MULTIPLIER = 0.5f;

    // Should we change these baselines?
    public static final int DUST_TO_SLURRY_AMOUNT = 1;
    public static final int COMPOUND_FROM_ACID_AMOUNT = 1;
    public static final int ACID_TO_COMPOUND_AMOUNT = 125;

    // Cryolite process uses these mostly.
    public static final int COMPOUND_ACID_TO_DUST_AMOUNT = 1;
    public static final int ACID_TO_DUST_AMOUNT = 125;
    public static final int DUST_FROM_COMPOUND_ACID_AMOUNT = 1;


    public static Logger getNewLogger()
    {
        return  LogUtils.getLogger();
    }

    public static List<MaterialInterface<?>> getGeologyMaterials(){
        ArrayList<MaterialInterface<?>> list = new ArrayList<>();
        list.addAll(List.of(StoneEnum.values()));
        list.addAll(List.of(MetalEnum.values()));
        list.addAll(List.of(MineralEnum.values()));
        list.addAll(List.of(MiscEnum.values()));
        list.addAll(List.of(ChemicalEnum.values()));

        return list;
    }
}
