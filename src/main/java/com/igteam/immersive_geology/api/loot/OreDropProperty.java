package com.igteam.immersive_geology.api.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.item.IGOreItem;
import com.igteam.immersive_geology.common.item.ItemBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

import java.util.Map;
import java.util.Objects;

public class OreDropProperty extends LootFunction {
    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "variable_ore_drops");

    public OreDropProperty(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack doApply(ItemStack itemStack, LootContext lootContext) {
        ItemStack pick = lootContext.get(LootParameters.TOOL);
        BlockState blockstate = lootContext.get(LootParameters.BLOCK_STATE);
        PlayerEntity player = lootContext.get(LootParameters.LAST_DAMAGE_PLAYER);

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(Objects.requireNonNull(lootContext.get(LootParameters.TOOL)));
        //used to use lootContext without wrapper, wrapper should prevent some issues, but if we have drop issues, check here
        assert pick != null;
        assert blockstate != null;
        if(pick.getToolTypes().contains(ToolType.PICKAXE)) {
            int harvestLevel = pick.getHarvestLevel(ToolType.PICKAXE, player, blockstate);
            int fortuneLevel = enchantments.getOrDefault(Enchantments.FORTUNE, 0);
            Item dropItem = itemStack.getItem();
            if (enchantments.get(Enchantments.SILK_TOUCH) != null) {
                if (dropItem instanceof IGOreItem) {
                    IGOreItem oreItem = (IGOreItem) dropItem;
                    return new ItemStack(IGRegistrationHolder.getBlockByMaterial(oreItem.getMaterial(BlockMaterialType.BASE_MATERIAL), oreItem.getMaterial(BlockMaterialType.ORE_MATERIAL), MaterialUseType.ORE_STONE));
                } else {
                    return itemStack;
                }
            } else {
                int calculation = Math.min(8, Math.min(4, harvestLevel + 1) + fortuneLevel);
                if (dropItem.asItem() == Items.COAL.getItem())
                    return new ItemStack(itemStack.getItem(), calculation);
                if (dropItem instanceof IGOreItem)
                    return new ItemStack(itemStack.getItem(), Math.min(8, Math.min(4, harvestLevel) + fortuneLevel));
                if (dropItem instanceof ItemBase && ((ItemBase) dropItem).getMaterial(BlockMaterialType.BASE_MATERIAL) instanceof MaterialMineralBase) {
                    MaterialMineralBase mineral = ((MaterialMineralBase) ((ItemBase) dropItem).getMaterial(BlockMaterialType.BASE_MATERIAL));
                    if (mineral.getMineralType() == MaterialMineralBase.EnumMineralType.CLAY)
                        return new ItemStack(itemStack.getItem(), 4 + fortuneLevel);
                    if (mineral.getMineralType() == MaterialMineralBase.EnumMineralType.FUEL)
                        return new ItemStack(itemStack.getItem(), calculation);
                }
                return new ItemStack(itemStack.getItem(), Math.max(0, 4 - Math.min(4, harvestLevel)));
            }
        } else {
            return itemStack;
        }

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
