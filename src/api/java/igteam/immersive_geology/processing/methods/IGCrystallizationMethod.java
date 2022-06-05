package igteam.immersive_geology.processing.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.ItemStack;

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
}
