package com.igteam.immersive_geology.common.crafting.recipes.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersive_geology.common.crafting.IGMultiblockRecipe;
import com.igteam.immersive_geology.common.crafting.Serializers;
import com.igteam.immersive_geology.core.config.IGConfigurationHandler;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class CalcinationRecipe extends IGMultiblockRecipe
{
    public static final IRecipeType<CalcinationRecipe> TYPE = IRecipeType.register(IGLib.MODID + ":rotary_kiln");
    public static Map<ResourceLocation, CalcinationRecipe> recipes = new HashMap<>();


    /** May return null! */
    public static CalcinationRecipe findRecipe(ItemStack itemInput)
    {
        if(!recipes.isEmpty()){
            for(CalcinationRecipe r:recipes.values()){
                if(r.itemInput != null && r.itemInput.test(itemInput))
                    return r;
            }
        }
        return null;
    }

    public static CalcinationRecipe loadFromNBT(CompoundNBT nbt){
        ItemStack itemInput =  ItemStack.read(nbt.getCompound("item_input"));
        return findRecipe(itemInput);
    }

    protected final IngredientWithSize itemInput;
    protected final ItemStack itemOutput;

    public CalcinationRecipe(ResourceLocation id, ItemStack itemOutput, IngredientWithSize itemInput, int energy, int time) {
        super(ItemStack.EMPTY, TYPE, id);
        this.itemInput = itemInput;
        this.itemOutput = itemOutput;

        this.outputList = NonNullList.from(ItemStack.EMPTY, itemOutput);

        timeAndEnergy(time, energy);
        modifyTimeAndEnergy(IGConfigurationHandler.Server.MULTIBLOCK.electrolizer_energyModifier::get, IGConfigurationHandler.Server.MULTIBLOCK.electrolizer_timeModifier::get);
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return Serializers.ROTARY_KILN_SERIALIZER.get();
    }

    @Override
    public boolean shouldCheckItemAvailability() {return false;}

    @Override
    public int getMultipleProcessTicks() {
        return 0;
    }

    @Override
    public NonNullList<ItemStack> getItemOutputs() {
        return outputList;
    }

    @Override
    public List<IngredientWithSize> getItemInputs() {
        return NonNullList.from(itemInput);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.itemOutput;
    }

    public IngredientWithSize getItemInput() {
        return this.itemInput;
    }
}
