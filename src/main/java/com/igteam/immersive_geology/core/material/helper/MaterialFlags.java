package com.igteam.immersive_geology.core.material.helper;

public enum MaterialFlags implements IFlagType<MaterialFlags> {
    GENERATE_IN_WORLD,
    EXISTING_IMPLEMENTATION,
    IS_MACHINE,
    IS_SLURRY,
    IS_SALT,
    IS_FLUID,
    IS_GAS;

    @Override
    public MaterialFlags getValue() {
        return this;
    }
}
