package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class IGBloomeryMethod extends IGProcessingMethod {

    private String methodName;

    public IGBloomeryMethod(IGProcessingStage stage) {
        super(RecipeMethod.Bloomery, stage);
    }
    private ItemStack itemResult;
    private IngredientWithSize itemInput;

    private TagKey<Item> inputTag;
    int time;

    public void create(String name, TagKey<Item> itemInput, int inputAmount, ItemStack itemResult, int time){
        methodName = name;
        this.inputTag = itemInput;
        this.itemResult = itemResult;
        this.itemInput = new IngredientWithSize(itemInput, inputAmount);

        this.time = time;
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
        return toRL("bloomery/refine_" + Objects.requireNonNull(getName()));
    }

    @Override
    public TagKey<?> getGenericInput(){
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
