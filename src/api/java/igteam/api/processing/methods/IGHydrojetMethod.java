package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class IGHydrojetMethod extends IGProcessingMethod {

    private String methodName;

    public IGHydrojetMethod(IGProcessingStage stage) {
        super(RecipeMethod.Cutting, stage);
    }

    private ItemStack result;
    private TagKey<Item> input;
    private FluidTagInput fluidInput;

    public void create(TagKey<Item> input, FluidTagInput fluidInput, ItemStack output){
        String sanitizedTagName = input.toString().substring(input.toString().indexOf("/")+1, input.toString().indexOf("]"));
        methodName = sanitizedTagName.contains(":") ? sanitizedTagName.substring(sanitizedTagName.indexOf(":") + 1) : sanitizedTagName;
        this.result = output;
        this.input = input;
        this.fluidInput = fluidInput;
    }

    public ItemStack getResult(){
        return result;
    }

    public TagKey<Item> getInput(){
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

    @Override
    public TagKey<?> getGenericInput(){
        return input;
    }

    @Override
    public ItemStack getGenericOutput() {
        return result;
    }
}
