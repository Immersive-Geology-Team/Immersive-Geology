package com.igteam.immersive_geology.core.data.generators.helpers;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;

public class OreDropProperty extends LootFunction {
    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "variable_ore_drops");

    public OreDropProperty(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack doApply(ItemStack itemStack, LootContext lootContext) {
        return itemStack;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return LootIG.Functions.ORE_DROP_PROPERTIES;
    }

    public static LootFunction.Builder<?> builder() {
        return builder(OreDropProperty::new);
    }

    public static class Serializer extends LootFunction.Serializer<OreDropProperty> {

        @Override
        public OreDropProperty deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] iLootConditions) {
            return new OreDropProperty(iLootConditions);
        }
    }
}
