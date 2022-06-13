package igteam.immersive_geology.processing.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.immersive_geology.materials.data.slurry.variants.MaterialSlurryWrapper;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    int time;
    int energy;

    public void create(IngredientWithSize itemInput, FluidTagInput primaryFluid, FluidTagInput secondaryFluid, ItemStack itemResult, FluidStack fluidResult, int time, int energy){
        methodName = itemInput.toString().substring(primaryFluid.toString().indexOf("/")+1, secondaryFluid.toString().indexOf("]"));

        this.fluidResult = fluidResult;
        this.itemResult = itemResult;
        this.fluidInput1 = primaryFluid;
        this.fluidInput2 = secondaryFluid;
        this.itemInput = itemInput;

        this.time = time;
        this.energy = energy;
    }

    public void create(String name, ItemStack itemInput, FluidTagInput primaryFluid, FluidTagInput secondaryFluid, ItemStack itemResult, FluidStack fluidResult, int time, int energy){
        methodName = name;

        this.fluidResult = fluidResult;
        this.itemResult = itemResult;
        this.fluidInput1 = primaryFluid;
        this.fluidInput2 = secondaryFluid;
        this.itemInput = itemInput.isEmpty() ? null : IngredientWithSize.of(itemInput);

        this.time = time;
        this.energy = energy;
    }

    public void create(String name, ITag<Item> itemInput, int input_count, FluidTagInput primaryFluid, FluidTagInput secondaryFluid, ItemStack itemResult, FluidStack fluidResult, int time, int energy){
        methodName = name;

        this.fluidResult = fluidResult;
        this.itemResult = itemResult;
        this.fluidInput1 = primaryFluid;
        this.fluidInput2 = secondaryFluid;
        this.itemInput = new IngredientWithSize(Ingredient.fromTag(itemInput), input_count);

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
}
