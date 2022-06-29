package igteam.immersive_geology.processing.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Objects;

public class IGHydrojetMethod extends IGProcessingMethod {

    private String methodName;

    public IGHydrojetMethod(IGProcessingStage stage) {
        super(RecipeMethod.Cutting, stage);
    }

    private ItemStack result;
    private ITag<Item> input;
    private FluidTagInput fluidInput;

    public void create(ITag<Item> input, FluidTagInput fluidInput, ItemStack output){
        String sanitizedTagName = input.toString().substring(input.toString().indexOf("/")+1, input.toString().indexOf("]"));
        methodName = sanitizedTagName;
        this.result = output;
        this.input = input;
        this.fluidInput = fluidInput;
    }

    public ItemStack getResult(){
        return result;
    }

    public ITag<Item> getInput(){
        return input;
    }

    public String getName() {
        return methodName;
    }

    public FluidTagInput getFluidInput() {
        return fluidInput;
    }

    @Override
    public ResourceLocation getLocation() {
        return toRL("cutting/cut_" + Objects.requireNonNull(getName()));
    }
}
