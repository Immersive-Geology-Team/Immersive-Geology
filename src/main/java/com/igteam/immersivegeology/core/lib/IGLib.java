package com.igteam.immersivegeology.core.lib;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
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

        return list;
    }
}
