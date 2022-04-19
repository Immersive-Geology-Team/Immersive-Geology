package igteam.immersive_geology.processing.methods;

import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.builders.SeparatorRecipeBuilder;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

public class IGSeparatorMethod extends IGProcessingMethod {

    private String methodName;

    public IGSeparatorMethod(IGProcessingStage stage) {
        super(RecipeMethod.Separator, stage);
    }

    private SeparatorRecipeBuilder builder;

    public SeparatorRecipeBuilder create(ITag<Item> result, int count, ItemStack input){
        methodName = input.getDisplayName().getString();
        builder = SeparatorRecipeBuilder.builder(result, count).addInput(input);
        return builder;
    }

    public SeparatorRecipeBuilder getBuilder(){ return builder; }

    public String getName() {
        return methodName;
    }
}
