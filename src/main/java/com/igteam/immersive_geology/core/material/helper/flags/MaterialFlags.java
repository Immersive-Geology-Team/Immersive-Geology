package com.igteam.immersive_geology.core.material.helper.flags;

public enum MaterialFlags implements IFlagType<MaterialFlags> {
    GENERATE_IN_WORLD,
    EXISTING_IMPLEMENTATION,
    IS_MACHINE,
    IS_SLURRY,
    IS_SALT,
    IS_FLUID,
    IS_GAS,
    IS_METAL_ALLOY;

    @Override
    public MaterialFlags getValue() {
        return this;
    }
}
