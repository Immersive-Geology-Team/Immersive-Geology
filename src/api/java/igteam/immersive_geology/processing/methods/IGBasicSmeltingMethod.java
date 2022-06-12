package igteam.immersive_geology.processing.methods;

import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

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
}
