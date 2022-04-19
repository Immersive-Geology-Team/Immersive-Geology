package igteam.immersive_geology.processing.recipe;

import blusunrize.immersiveengineering.api.crafting.*;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.processing.Serializers;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BloomeryRecipe extends IESerializableRecipe
{
    public static IRecipeType<BloomeryRecipe> TYPE = IRecipeType.register(IGApi.MODID + ":bloomery");
    public static Map<ResourceLocation, BloomeryRecipe> recipes = new HashMap<>();
    public final IngredientWithSize input;
    public final ItemStack output;
    public final int time;

    public BloomeryRecipe(ResourceLocation id, ItemStack output, IngredientWithSize input, int time) {
        super(output, TYPE, id);
        this.output = output;
        this.input = input;
        this.time = time;
    }

    public static BloomeryRecipe findRecipe(ItemStack input){
        return findRecipe(input, null);
    }

    public static BloomeryRecipe findRecipe(ItemStack input, @Nullable BloomeryRecipe hint){
        if(input.isEmpty()) return null;
        if(hint != null && hint.matches(input)) return hint;
        for(BloomeryRecipe recipe : recipes.values()){
            if(recipe.matches(input)){
                return recipe;
            }
        }
        return null;
    }

    public boolean matches(ItemStack input){
        return this.input.test(input);
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return Serializers.BLOOMERY_SERIALIZER.get();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output.copy();
    }

    public IngredientWithSize getRecipeInput() {
        return input;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(IInventory inv) {
        return super.getRemainingItems(inv);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return super.getIngredients();
    }

    @Override
    public String getGroup() {
        return super.getGroup();
    }

    public int getTime() {
        return time;
    }
}
