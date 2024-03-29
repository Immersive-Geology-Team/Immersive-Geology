package igteam.immersive_geology.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ChunkDropProperty extends LootFunction {
    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "variable_chunk_drops");

    public ChunkDropProperty(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack doApply(ItemStack itemStack, LootContext lootContext) {
        ItemStack toolStack = lootContext.get(LootParameters.TOOL);
        Set<ToolType> tool = toolStack.getToolTypes();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(Objects.requireNonNull(lootContext.get(LootParameters.TOOL)));
        BlockState blockStateIn = Objects.requireNonNull(lootContext.get(LootParameters.BLOCK_STATE));
        if (toolStack.canHarvestBlock(blockStateIn))
        {
            int harvestLevel = toolStack.getHarvestLevel(ToolType.PICKAXE, null, lootContext.get(LootParameters.BLOCK_STATE));
            if(enchantments.get(Enchantments.SILK_TOUCH) != null){
                //Tool has silk touch
                return ItemStack.EMPTY; //catch return if for some reason the item isn't IGGenericItem
            } else {
                //Does not have silk touch
                int fortune = Math.min(5,enchantments.getOrDefault(Enchantments.FORTUNE, 0));
                itemStack.setCount(Math.max(0, 8 - harvestLevel - fortune));
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return LootIG.Functions.CHUNK_DROP_PROPERTIES;
    }

    public static Builder<?> builder() {
        return builder(ChunkDropProperty::new);
    }

    public static class Serializer extends LootFunction.Serializer<ChunkDropProperty> {

        @Override
        public ChunkDropProperty deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] iLootConditions) {
            return new ChunkDropProperty(iLootConditions);
        }
    }
}
