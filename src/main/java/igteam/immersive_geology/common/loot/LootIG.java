package igteam.immersive_geology.common.loot;

import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class LootIG {

    public static void initialize(){
        Functions.ORE_DROP_PROPERTIES = registerFunction(OreDropProperty.ID, new OreDropProperty.Serializer());
        Functions.SECONDARY_ORE_DROP_PROPERTIES = registerFunction(SecondaryOreDropProperty.ID, new SecondaryOreDropProperty.Serializer());
        Functions.SILKTOUCH_DUMMY_ORE_DROP_PROPERTIES = registerFunction(DummyOreDropProperty.ID, new DummyOreDropProperty.Serializer());
    }

    private static LootFunctionType registerFunction(ResourceLocation id, ILootSerializer<? extends ILootFunction> serializer)
    {
        return Registry.register(
                Registry.LOOT_FUNCTION_TYPE, id, new LootFunctionType(serializer)
        );
    }

    public static class Functions {
        public static LootFunctionType ORE_DROP_PROPERTIES;
        public static LootFunctionType SECONDARY_ORE_DROP_PROPERTIES;
        public static LootFunctionType SILKTOUCH_DUMMY_ORE_DROP_PROPERTIES;
    }
}
