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

    public boolean isLoaded(){
        return DatagenModLoader.isRunningDataGen() || ModList.get().isLoaded(getName().toLowerCase());
    }
}
