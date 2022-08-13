package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;

public class IGRefineryMethod  extends IGProcessingMethod {
    public IGRefineryMethod(IGProcessingStage stage) {
        super(RecipeMethod.Synthesis, stage);
    }

    private String methodName;

    private FluidTagInput fluidInput1;
    private FluidTagInput fluidInput2;
    private FluidStack fluidResult;

    public String getMethodName() {
        return methodName;
    }

    public void create(String name,  FluidTagInput primaryFluid, FluidTagInput secondaryFluid, FluidStack fluidResult) {
        methodName = name;

        this.fluidResult = fluidResult;
        this.fluidInput1 = primaryFluid;
        this.fluidInput2 = secondaryFluid;
    }



    public FluidStack getFluidResult() {
        return fluidResult;
    }


    public FluidTagInput getFluidInput1() {
        return fluidInput1;
    }

    public FluidTagInput getFluidInput2() {
        return fluidInput2;
    }

    @Override
    public ResourceLocation getLocation() {
        return toRL("refinery/synthesize_" + Objects.requireNonNull(getMethodName()));
    }

}
