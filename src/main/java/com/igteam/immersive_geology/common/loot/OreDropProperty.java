package com.igteam.immersive_geology.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.common.block.blocks.IGOreBlock;
import com.igteam.immersive_geology.common.item.IGGenericItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class OreDropProperty extends LootFunction {
    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "variable_ore_drops");

    public OreDropProperty(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack doApply(ItemStack itemStack, LootContext lootContext) {
        ItemStack toolStack = lootContext.get(LootParameters.TOOL);
        Set<ToolType> tool = toolStack.getToolTypes();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(Objects.requireNonNull(lootContext.get(LootParameters.TOOL)));

        if(tool.contains(ToolType.PICKAXE)){
            int fortune = enchantments.getOrDefault(Enchantments.FORTUNE, 0);
            int harvestLevel = toolStack.getHarvestLevel(ToolType.PICKAXE, null, lootContext.get(LootParameters.BLOCK_STATE));
            if(enchantments.get(Enchantments.SILK_TOUCH) != null){
                //Tool has silk touch
                if(itemStack.getItem() instanceof IGGenericItem){
                    IGGenericItem igItem = (IGGenericItem) itemStack.getItem();
                    Block igOreBlock = igItem.getMaterial(MaterialTexture.base).getBlock(BlockPattern.ore, igItem.getMaterial(MaterialTexture.overlay));
                    return new ItemStack(igOreBlock);
                }
                return itemStack; //catch return if for some reason the item isn't IGGenericItem
            } else {
                //Does not have silk touch
                itemStack.setCount(Math.min(5, Math.min(4, harvestLevel) + fortune));
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
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
