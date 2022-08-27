package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class IGCalcinationMethod extends IGProcessingMethod {

    private String methodName;

    public IGCalcinationMethod(IGProcessingStage stage) {
        super(RecipeMethod.Calcination, stage);
    }
    private ItemStack itemResult;
    private IngredientWithSize itemInput;
    private ITag<Item> inputTag;
    private int time;
    private int energy;

    public void create(String name, ItemStack output, ITag<Item> inputTag, int itemAmount, int time, int energy){
        methodName = name;

        this.inputTag = inputTag;
        this.itemResult = output;
        this.itemInput = new IngredientWithSize(inputTag, itemAmount);

        this.time = time;
        this.energy = energy;
    }

    public int getEnergy() {return energy;};

    public String getMethodName() {
        return methodName;
    }

    public ItemStack getItemResult() {
        return itemResult;
    }

    public IngredientWithSize getItemInput() {
        return itemInput;
    }

    public int getTime() {
        return time;
    }

    @Override
    public ResourceLocation getLocation() {
        return toRL("calcination/decompose_" + Objects.requireNonNull(getMethodName()));
    }
    @Override
    public ITag<?> getGenericInput(){
        return inputTag;
    }

    @Override
    public String getName() {
        return methodName;
    }

    @Override
    public ItemStack getGenericOutput() {
        return itemResult;
    }
}
