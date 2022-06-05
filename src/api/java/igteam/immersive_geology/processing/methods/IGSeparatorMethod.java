package igteam.immersive_geology.processing.methods;

import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.annotation.Nonnull;

public class IGSeparatorMethod extends IGProcessingMethod {

    private String methodName;

    public IGSeparatorMethod(IGProcessingStage stage) {
        super(RecipeMethod.Separator, stage);
    }

    private ItemStack result;
    private ITag<Item> input;
    private ItemStack waste;

    public void create(ITag<Item> input, ItemStack output, @Nonnull ItemStack waste){
        String sanitizedTagName = input.toString().substring(input.toString().indexOf("/")+1, input.toString().indexOf("]"));
        methodName = sanitizedTagName;
        this.result = output;
        this.input = input;
        this.waste = waste;
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

    public ItemStack getWaste() {
        return waste;
    }
}
