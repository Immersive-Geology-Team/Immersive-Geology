package com.igteam.immersivegeology.core.material.helper.flags;

public enum MaterialFlags implements IFlagType<MaterialFlags> {
    GENERATE_IN_WORLD,
    EXISTING_IMPLEMENTATION,
    IS_ORE_BEARING,
    IS_MACHINE,
    HAS_SLURRY,
    IS_SALT,
    IS_GAS,
    IS_CHEMICAL,
    IS_MOLTEN_METAL,
    IS_METAL_ALLOY;

    @Override
    public MaterialFlags getValue() {
        return this;
    }

    @Override
    public String getTagPrefix()
    {
        return "";
    }
}
