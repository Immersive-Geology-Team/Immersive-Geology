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
