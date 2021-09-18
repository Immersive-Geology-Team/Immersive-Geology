package com.igteam.immersive_geology.api.crafting.recipes.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.igteam.immersive_geology.common.crafting.Serializers;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeparatorRecipe extends MultiblockRecipe
{
    public static IRecipeType<SeparatorRecipe> TYPE = IRecipeType.register(IGLib.MODID + ":gravityseparator");
    public static Map<ResourceLocation, SeparatorRecipe> recipes = new HashMap<>();
    public final Ingredient input;
    public final ItemStack output;
    public final Ingredient waste;

    public final List<StackWithChance> secondaryOutputs = new ArrayList<>();

    public SeparatorRecipe(ResourceLocation id, ItemStack output, Ingredient waste, Ingredient input) {
        super(output, TYPE, id);
        this.output = output;
        this.waste = waste;
        this.input = input;

        setInputList(Lists.newArrayList(this.input));
        this.outputList = NonNullList.from(ItemStack.EMPTY, this.output);
    }

    public static SeparatorRecipe findRecipe(ItemStack input){
        for(SeparatorRecipe recipe : recipes.values()){
            if(recipe.input.test(input)){
                return recipe;
            }
        }
        return null;
    }

    public SeparatorRecipe addToSecondaryOutput(StackWithChance output)
    {
        Preconditions.checkNotNull(output);
        secondaryOutputs.add(output);
        return this;
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return Serializers.GRAVITY_SEPARATOR_SERIALIZER.get();
    }

    @Override
    public int getMultipleProcessTicks() {
        return 4;
    }

    @Override
    public int getTotalProcessTime() {
        return 20 * 4; //20 ticks times 4, four seconds approx
    }

    @Override
    public int getTotalProcessEnergy() {
        return 0;
    }
}
