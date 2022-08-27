package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.SlurryEnum;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class IGCrystallizationMethod extends IGProcessingMethod {

    private String methodName;
    private ITag<Fluid> fluidTag;

    public IGCrystallizationMethod(IGProcessingStage stage) {
        super(RecipeMethod.Crystalization, stage);
    }
    private ItemStack itemResult;
    private FluidTagInput fluidInput;
    private int time;
    private int energy;

    public void create(String name, ItemStack output, ITag<Fluid> fluidInput, int fluidAmount, int time, int energy){
        methodName = name;

        this.itemResult = output;
        this.fluidTag = fluidInput;
        this.fluidInput = new FluidTagInput(fluidInput, fluidAmount);

        this.time = time;
        this.energy = energy;
    }

    public int getEnergy() {return energy;};

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
        return toRL("crystallization/crystallize_" + Objects.requireNonNull(getName()));
    }

    @Override
    public ITag<?> getGenericInput(){
        return fluidTag;
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
