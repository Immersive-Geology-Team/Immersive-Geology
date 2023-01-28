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

    public static HashMap<String,ForgeConfigSpec.ConfigValue<List<ItemCategoryFlags>>> ITEM_FLAGS = new HashMap<>();
    public static HashMap<String,ForgeConfigSpec.ConfigValue<List<BlockCategoryFlags>>> BLOCK_FLAGS = new HashMap<>();

    public static void initialize(){
        BUILDER.push("Immersive Geology Common Configuration");

        for (MaterialInterface<?> material : ImmersiveGeology.getGeologyMaterials()) {
            BUILDER.push(material.getName());
                ITEM_FLAGS.put(material.getName(), BUILDER.comment("Material: " + material.getName()).define("Item Flags", ConfigurationHelper.defaultItemFlags.apply(material)));
                BLOCK_FLAGS.put(material.getName(), BUILDER.comment("Material: " + material.getName()).define("Block Flags", ConfigurationHelper.defaultBlockFlags.apply(material)));
            BUILDER.pop();
        }

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
