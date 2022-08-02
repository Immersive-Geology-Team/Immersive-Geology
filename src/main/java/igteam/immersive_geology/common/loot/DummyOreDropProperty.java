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

public class DummyOreDropProperty extends LootFunction {
    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "variable_dummy_ore_drops");

    public DummyOreDropProperty(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack doApply(ItemStack itemStack, LootContext lootContext) {
        ItemStack toolStack = lootContext.get(LootParameters.TOOL);
        Set<ToolType> tool = toolStack.getToolTypes();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(Objects.requireNonNull(lootContext.get(LootParameters.TOOL)));
        BlockState blockStateIn = Objects.requireNonNull(lootContext.get(LootParameters.BLOCK_STATE));
        if (toolStack.canHarvestBlock(blockStateIn)){
            int fortune = enchantments.getOrDefault(Enchantments.FORTUNE, 0);
            int harvestLevel = toolStack.getHarvestLevel(ToolType.PICKAXE, null, lootContext.get(LootParameters.BLOCK_STATE));
            if(enchantments.get(Enchantments.SILK_TOUCH) != null){
                //Tool has silk touch
                return itemStack; //To drop the ore
            } else {
                //Does not have silk touch
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return LootIG.Functions.SILKTOUCH_DUMMY_ORE_DROP_PROPERTIES;
    }

    public static Builder<?> builder() {
        return builder(DummyOreDropProperty::new);
    }

    public static class Serializer extends LootFunction.Serializer<DummyOreDropProperty> {

        @Override
        public DummyOreDropProperty deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] iLootConditions) {
            return new DummyOreDropProperty(iLootConditions);
        }
    }
}
