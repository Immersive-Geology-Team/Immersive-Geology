package com.igteam.immersive_geology.common.configuration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.configuration.helper.ConfigurationHelper;
import com.igteam.immersive_geology.core.material.helper.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.ItemCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;

public class CommonConfiguration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static void initialize(){
        BUILDER.push("Immersive Geology Common Configuration");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
