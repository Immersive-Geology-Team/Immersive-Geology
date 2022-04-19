package igteam.immersive_geology.processing.methods;

import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import slimeknights.tconstruct.library.utils.TagUtil;

public class IGSeparatorMethod extends IGProcessingMethod {

    private String methodName;

    public IGSeparatorMethod(IGProcessingStage stage) {
        super(RecipeMethod.Separator, stage);
    }

    private ItemStack result;
    private int rCount;
    private ITag<Item> input;

    public void create(ItemStack output, int count, ITag<Item> input){
        String sanitizedTagName = input.toString().substring(input.toString().indexOf("/")+1, input.toString().indexOf("]"));
        methodName = sanitizedTagName;
        this.result = output;
        this.rCount = count;
        this.input = input;
    }

    public ItemStack getResult(){
        return result;
    }

    public int getResultCount(){
        return rCount;
    }

    public ITag<Item> getInput(){
        return input;
    }

    public String getName() {
        return methodName;
    }
}
