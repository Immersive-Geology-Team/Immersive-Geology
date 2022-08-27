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

public class IGRoastingMethod extends IGProcessingMethod {

    private String methodName;

    public IGRoastingMethod(IGProcessingStage stage) {
        super(RecipeMethod.Roasting, stage);
    }
    private ItemStack itemResult;
    private IngredientWithSize itemInput;
    private ITag<Item> itemTag;
    private int time;
    private float wasteMult;

    public void create(String name, ITag<Item> itemInput, int inputAmount, ItemStack output, int time, float wasteMult){
        methodName = name;

        this.itemTag = itemInput;
        this.itemResult = output;
        this.itemInput = new IngredientWithSize(itemInput, inputAmount);

        this.time = time;
        this.wasteMult = wasteMult;
    }

    public float getWasteMult() {return wasteMult;};

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
        return toRL("roasting/roast_" + Objects.requireNonNull(getMethodName()));
    }

    @Override
    public ITag<?> getGenericInput(){
        return itemTag;
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
