package com.igteam.immersivegeology.common.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfiguration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("Immersive Geology Client Configuration");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
