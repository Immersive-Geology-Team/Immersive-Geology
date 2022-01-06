package com.igteam.immersive_geology.api.crafting.recipes.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersive_geology.api.crafting.IGMultiblockRecipe;
import com.igteam.immersive_geology.common.crafting.Serializers;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReverberationRecipe extends IGMultiblockRecipe
{
    public static IRecipeType<ReverberationRecipe> TYPE = IRecipeType.register(IGLib.MODID + ":reverberation_furnace");
    public static Map<ResourceLocation, ReverberationRecipe> recipes = new HashMap<>();
    public final IngredientWithSize input;
    public final ItemStack output;
    public final int time;
    private final float wasteMult;

    private int slotOffset;

    public ReverberationRecipe(ResourceLocation id, ItemStack output, IngredientWithSize input, int time, float wasteMult) {
        super(output, TYPE, id);
        this.output = output;
        this.input = input;
        this.time = time;
        this.wasteMult = wasteMult;
        this.outputList = NonNullList.from(ItemStack.EMPTY, output);
        timeAndEnergy(time, 0);
    }

    public static ReverberationRecipe findRecipe(ItemStack input){
        return findRecipe(input, null);
    }

    public static ReverberationRecipe findRecipe(ItemStack input, @Nullable ReverberationRecipe hint){
        if(input.isEmpty()) return null;
        if(hint != null && hint.matches(input)) return hint;
        for(ReverberationRecipe recipe : recipes.values()){
            if(recipe.matches(input)){
                return recipe;
            }
        }
        return null;
    }

    public void setSlotOffset(int offset){
        this.slotOffset = offset;
    }

    public float getWasteMultipler() {
        return wasteMult;
    }

    public boolean matches(ItemStack input){
        return this.input.test(input);
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return Serializers.REVERBERATION_SERIALIZER.get();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output.copy();
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

    @Override
    public int getMultipleProcessTicks() {
        return 2;
    }

    public int getSlotOffset(){
        return slotOffset;
    }

    @Override
    public NonNullList<ItemStack> getItemOutputs() {
        return outputList;
    }

    public IngredientWithSize getInput() {
        return input;
    }
}
