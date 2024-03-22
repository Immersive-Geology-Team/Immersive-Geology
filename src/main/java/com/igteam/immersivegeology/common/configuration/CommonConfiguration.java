package com.igteam.immersivegeology.common.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfiguration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static void initialize(){
        BUILDER.push("Immersive Geology Common Configuration");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
