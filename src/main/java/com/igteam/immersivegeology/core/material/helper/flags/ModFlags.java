package com.igteam.immersivegeology.core.material.helper.flags;


import com.igteam.immersivegeology.api.IGApi;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import static com.igteam.immersivegeology.ImmersiveGeology.checkDataGeneration;

public enum ModFlags implements IFlagType<ModFlags> {
    BEYOND_EARTH;

    @Override
    public ModFlags getValue() {
        return this;
    }

    public static boolean isLoaded(IFlagType<?> flag){
        return checkDataGeneration() || (flag instanceof ModFlags modFlags && ModList.get().isLoaded(modFlags.getName().toLowerCase()));
    }
}
