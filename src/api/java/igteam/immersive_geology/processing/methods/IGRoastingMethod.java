package igteam.immersive_geology.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class IGRoastingMethod extends IGProcessingMethod {

    private String methodName;

    public IGRoastingMethod(IGProcessingStage stage) {
        super(RecipeMethod.Roasting, stage);
    }
    private ItemStack itemResult;
    private IngredientWithSize itemInput;
    private int time;
    private float wasteMult;

    public void create(String name, ItemStack itemInput, ItemStack output, int time, float wasteMult){
        methodName = name;

        this.itemResult = output;
        this.itemInput = IngredientWithSize.of(itemInput);

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
}
