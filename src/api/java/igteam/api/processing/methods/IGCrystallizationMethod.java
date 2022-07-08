package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class IGCrystallizationMethod extends IGProcessingMethod {

    private String methodName;

    public IGCrystallizationMethod(IGProcessingStage stage) {
        super(RecipeMethod.Crystalization, stage);
    }
    private ItemStack itemResult;
    private FluidTagInput fluidInput;
    private int time;
    private int energy;

    public void create(String name, ItemStack output, FluidTagInput fluidInput, int time, int energy){
        methodName = name;

        this.itemResult = output;
        this.fluidInput = fluidInput;

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

    public FluidTagInput getFluidInput() {
        return fluidInput;
    }

    public int getTime() {
        return time;
    }

    @Override
    public ResourceLocation getLocation() {
        return toRL("crystallization/crystallize_" + Objects.requireNonNull(getMethodName()));
    }
}
