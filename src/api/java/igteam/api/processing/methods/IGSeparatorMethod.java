package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;

public class IGSeparatorMethod extends IGProcessingMethod {

    private String methodName;

    public IGSeparatorMethod(IGProcessingStage stage) {
        super(RecipeMethod.Separator, stage);
    }

    private ItemStack result;
    private TagKey<Item> input;
    private ItemStack waste;

    public void create(TagKey<Item> input, ItemStack output, @Nonnull ItemStack waste){
        String sanitizedTagName = input.toString().substring(input.toString().indexOf("/")+1, input.toString().indexOf("]"));
        methodName = sanitizedTagName;
        this.result = output;
        this.input = input;
        this.waste = waste;
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

    public ItemStack getWaste() {
        return waste;
    }

    @Override
    public ResourceLocation getLocation() {
        return toRL("wash/wash_" + Objects.requireNonNull(getName()));
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
