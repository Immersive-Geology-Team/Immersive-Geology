package igteam.immersive_geology.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class SecondaryOreDropProperty extends LootFunction {
    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "variable_secondary_ore_drops");

    public SecondaryOreDropProperty(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack doApply(ItemStack itemStack, LootContext lootContext) {
        ItemStack toolStack = lootContext.get(LootParameters.TOOL);
        Set<ToolType> tool = toolStack.getToolTypes();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(Objects.requireNonNull(lootContext.get(LootParameters.TOOL)));
        BlockState blockStateIn = Objects.requireNonNull(lootContext.get(LootParameters.BLOCK_STATE));
        if (toolStack.canHarvestBlock(blockStateIn)){
            int fortune = Math.min(5,enchantments.getOrDefault(Enchantments.FORTUNE, 0));
            int harvestLevel = toolStack.getHarvestLevel(ToolType.PICKAXE, null, lootContext.get(LootParameters.BLOCK_STATE));
            Random rand = new Random();
            if(enchantments.get(Enchantments.SILK_TOUCH) != null){
                //Tool has silk touch
                return ItemStack.EMPTY; //To prevent multiple ore blocks when something has a secondary drop.
            } else {
                //Does not have silk touch
                itemStack.setCount(Math.min(8, harvestLevel + rand.nextInt(harvestLevel+1)) + rand.nextInt((rand.nextInt(fortune+1)+1)*fortune+1));
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return LootIG.Functions.SECONDARY_ORE_DROP_PROPERTIES;
    }

    public static Builder<?> builder() {
        return builder(SecondaryOreDropProperty::new);
    }

    public static class Serializer extends LootFunction.Serializer<SecondaryOreDropProperty> {

        @Override
        public SecondaryOreDropProperty deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] iLootConditions) {
            return new SecondaryOreDropProperty(iLootConditions);
        }
    }
}
