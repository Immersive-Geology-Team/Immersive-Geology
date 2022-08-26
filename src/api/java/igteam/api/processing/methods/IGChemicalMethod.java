package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class IGChemicalMethod extends IGProcessingMethod {

    private String methodName;

    public IGChemicalMethod(IGProcessingStage stage) {
        super(RecipeMethod.Chemical, stage);
    }

    private FluidStack fluidResult;
    private ItemStack itemResult;

    private FluidTagInput fluidInput1;
    private FluidTagInput fluidInput2;
    private IngredientWithSize itemInput;
    private ITag<Item> inputTag;
    int time;
    int energy;

    public void create(ITag<Item> inputTag, int inputAmount, FluidTagInput primaryFluid, FluidTagInput secondaryFluid, ItemStack itemResult, FluidStack fluidResult, int time, int energy){
        methodName = itemInput.toString().substring(primaryFluid.toString().indexOf("/")+1, secondaryFluid.toString().indexOf("]"));

        this.fluidResult = fluidResult;
        this.itemResult = itemResult;
        this.fluidInput1 = primaryFluid;
        this.fluidInput2 = secondaryFluid;
        this.itemInput = new IngredientWithSize(inputTag, inputAmount);

        this.time = time;
        this.energy = energy;
    }
    public void create(String name, ITag<Item> itemInput, int input_count, FluidTagInput primaryFluid, FluidTagInput secondaryFluid, ItemStack itemResult, FluidStack fluidResult, int time, int energy){
        methodName = name;

        this.inputTag = itemInput;
        this.fluidResult = fluidResult;
        this.itemResult = itemResult;
        this.fluidInput1 = primaryFluid;
        this.fluidInput2 = secondaryFluid;
        this.itemInput = itemInput == null ? null : new IngredientWithSize(Ingredient.fromTag(itemInput), input_count);

        this.time = time;
        this.energy = energy;
    }

    public String getMethodName() {
        return methodName;
    }

    public FluidStack getFluidResult() {
        return fluidResult;
    }

    public ItemStack getItemResult() {
        return itemResult;
    }

    public FluidTagInput getFluidInput1() {
        return fluidInput1;
    }

    public FluidTagInput getFluidInput2() {
        return fluidInput2;
    }

    @Nullable
    public IngredientWithSize getItemInput() {
        return itemInput;
    }

    public int getTime() {
        return time;
    }

    public int getEnergy() {
        return energy;
    }

    @Override
    public ResourceLocation getLocation() {
        return toRL("vat/leach_" + Objects.requireNonNull(getMethodName()));
    }

    @Override
    public ITag<?> getGenericInput(){
        return inputTag;
    }

    @Override
    public String getName() {
        return methodName;
    }

    public void create(String s, ItemStack itemStack, FluidTagInput primaryFluid, FluidTagInput secondaryFluid, ItemStack stackResult, FluidStack fluidResult, int time, int energy) {
        methodName = s;

        this.fluidResult = fluidResult;
        this.itemResult = stackResult;
        this.fluidInput1 = primaryFluid;
        this.fluidInput2 = secondaryFluid;
        this.itemInput = itemStack.isEmpty() ? null : IngredientWithSize.of(itemStack);

        this.time = time;
        this.energy = energy;
    }
}
