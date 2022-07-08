package igteam.api.processing.methods;

import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Objects;

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

    @Override
    public ResourceLocation getLocation() {
        return toRL("wash/wash_" + Objects.requireNonNull(getName()));
    }
}
