package com.igteam.immersivegeology.core.material.helper.flags;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

public enum ModFlags implements IFlagType<ModFlags> {
    BEYOND_EARTH;

    @Override
    public ModFlags getValue() {
        return this;
    }

    public static boolean isLoaded(IFlagType<?> flag){
        return DatagenModLoader.isRunningDataGen() || (flag instanceof ModFlags modFlags && ModList.get().isLoaded(modFlags.getName().toLowerCase()));
    }
}
