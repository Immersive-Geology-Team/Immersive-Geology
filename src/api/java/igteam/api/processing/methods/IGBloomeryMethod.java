package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class IGBloomeryMethod extends IGProcessingMethod {

    private String methodName;

    public IGBloomeryMethod(IGProcessingStage stage) {
        super(RecipeMethod.Bloomery, stage);
    }
    private ItemStack itemResult;
    private IngredientWithSize itemInput;
    int time;

    public void create(String name, ItemStack itemInput, ItemStack itemResult, int time){
        methodName = name;

        this.itemResult = itemResult;
        this.itemInput = IngredientWithSize.of(itemInput);

        this.time = time;
    }

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
        return toRL("bloomery/refine_" + Objects.requireNonNull(getMethodName()));
    }
}