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
