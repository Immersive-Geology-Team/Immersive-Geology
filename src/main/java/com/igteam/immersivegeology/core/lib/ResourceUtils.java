/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.lib;

import blusunrize.immersiveengineering.api.Lib;
import net.minecraft.resources.ResourceLocation;

public class ResourceUtils {
    public static ResourceLocation ig(String path){
        return new ResourceLocation(IGLib.MODID, path);
    }

    public static ResourceLocation ie(String path){
        return new ResourceLocation(Lib.MODID, path);
    }
}
