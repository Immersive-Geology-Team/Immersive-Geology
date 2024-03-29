package igteam.immersive_geology.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import igteam.api.materials.helper.MaterialTexture;
import igteam.api.materials.pattern.BlockPattern;
import igteam.immersive_geology.common.item.IGGenericItem;
import igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.block.Block;
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
import java.util.Random;
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
        BlockState blockStateIn = Objects.requireNonNull(lootContext.get(LootParameters.BLOCK_STATE));
        if (toolStack.canHarvestBlock(blockStateIn))
        {
            int harvestLevel = toolStack.getHarvestLevel(ToolType.PICKAXE, null, lootContext.get(LootParameters.BLOCK_STATE));
            if(enchantments.get(Enchantments.SILK_TOUCH) != null){
                //Tool has silk touch
                if(itemStack.getItem() instanceof IGGenericItem){
                    IGGenericItem igItem = (IGGenericItem) itemStack.getItem();
                    Block igOreBlock = igItem.getMaterial(MaterialTexture.base).getBlock(BlockPattern.ore, igItem.getMaterial(MaterialTexture.overlay));
                    return new ItemStack(igOreBlock);
                }
            } else {
                //Does not have silk touch
                Random rand = new Random();

                itemStack.setCount(Math.min(6, harvestLevel+ rand.nextInt(harvestLevel + 1)));
            }
            return itemStack; //catch return if for some reason the item isn't IGGenericItem
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
