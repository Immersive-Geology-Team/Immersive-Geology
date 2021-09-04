package com.igteam.immersive_geology.core.data_helper;

import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class LootIG {

    public static void initialize(){
        Functions.ORE_DROP_PROPERTIES = registerFunction(OreDropProperty.ID, new OreDropProperty.Serializer());
    }

    private static LootFunctionType registerFunction(ResourceLocation id, ILootSerializer<? extends ILootFunction> serializer)
    {
        return Registry.register(
                Registry.LOOT_FUNCTION_TYPE, id, new LootFunctionType(serializer)
        );
    }

    public static class Functions {
        public static LootFunctionType ORE_DROP_PROPERTIES;
    }
}
