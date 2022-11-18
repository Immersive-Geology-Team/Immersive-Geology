package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class IGRoastingMethod extends IGProcessingMethod {

    private String methodName;

    public IGRoastingMethod(IGProcessingStage stage) {
        super(RecipeMethod.Roasting, stage);
    }
    private ItemStack itemResult;
    private IngredientWithSize itemInput;
    private TagKey<Item> itemTag;
    private int time;
    private float wasteMult;

    public void create(String name, TagKey<Item> itemInput, int inputAmount, ItemStack output, int time, float wasteMult){
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
    public TagKey<?> getGenericInput(){
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
