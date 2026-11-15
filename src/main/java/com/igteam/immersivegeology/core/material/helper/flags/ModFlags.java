package com.igteam.immersivegeology.core.material.helper.flags;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

import java.util.Locale;

public enum ModFlags implements IFlagType<ModFlags> {
    MINECRAFT,
    BEYOND_EARTH,
    AD_ASTRA,
    IMMERSIVEENGINEERING,
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

    public boolean isLoaded()
    {
        return this.equals(MINECRAFT) || DatagenModLoader.isRunningDataGen() || ModList.get().isLoaded(getName().toLowerCase(Locale.ROOT));
    }

    public boolean isStrictlyLoaded(){
        return this.equals(MINECRAFT) || ModList.get().isLoaded(getName().toLowerCase(Locale.ROOT));
    }
}
