package com.igteam.immersivegeology.core.material.helper.flags;

import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.fml.ModList;

public enum ModFlags implements IFlagType<ModFlags> {
    BEYOND_EARTH,
    AD_ASTRA,
    TFC;

    @Override
    public ModFlags getValue() {
        return this;
    }

    @Override
    public String getTagPrefix()
    {
        return "";
    }

    public static boolean isLoaded(IFlagType<?> flag){
        return DatagenModLoader.isRunningDataGen() || (flag instanceof ModFlags modFlags && ModList.get().isLoaded(modFlags.getName().toLowerCase()));
    }
}
