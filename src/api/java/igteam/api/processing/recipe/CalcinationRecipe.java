package igteam.api.processing.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.IGApi;
import igteam.api.processing.Serializers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class CalcinationRecipe extends IGMultiblockRecipe
{
    public static final IRecipeType<CalcinationRecipe> TYPE = IRecipeType.register(IGApi.MODID + ":rotary_kiln");
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
        return Arrays.asList(itemInput);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.itemOutput;
    }

    public IngredientWithSize getItemInput() {
        return this.itemInput;
    }
}
