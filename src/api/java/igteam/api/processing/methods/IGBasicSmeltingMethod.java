package igteam.api.processing.methods;

import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class IGBasicSmeltingMethod extends IGProcessingMethod {

    public IGBasicSmeltingMethod(IGProcessingStage stage) {
        super(RecipeMethod.basicSmelting, stage);
    }
    private IItemProvider input, output;

    public void create(IItemProvider input, IItemProvider output){
        this.input = input;
        this.output = output;
    }

    public IItemProvider getInput() {
        return input;
    }

    public IItemProvider getOutput() {
        return output;
    }

    @Override
    public ResourceLocation getLocation() {
        return toRL(toPath(output) + "_from_blasting");
    }

    private String toPath(IItemProvider src) {
        return Objects.requireNonNull(src.asItem().getRegistryName()).getPath();
    }
}
