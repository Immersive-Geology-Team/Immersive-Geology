package igteam.immersive_geology.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class IGCalcinationMethod extends IGProcessingMethod {

    private String methodName;

    public IGCalcinationMethod(IGProcessingStage stage) {
        super(RecipeMethod.Calcination, stage);
    }
    private ItemStack itemResult;
    private IngredientWithSize itemInput;
    private int time;
    private int energy;

    public void create(String name, ItemStack output, ItemStack itemInput, int time, int energy){
        methodName = name;

        this.itemResult = output;
        this.itemInput = IngredientWithSize.of(itemInput);

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
}
