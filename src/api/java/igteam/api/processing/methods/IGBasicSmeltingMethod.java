package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class IGBasicSmeltingMethod extends IGProcessingMethod {

    public IGBasicSmeltingMethod(IGProcessingStage stage) {
        super(RecipeMethod.basicSmelting, stage);
    }
    private IItemProvider input, output;
    private ITag<Item> inputTag;

    public void create(ITag<Item> inputTag, IItemProvider inputProvider, IItemProvider output){
        this.inputTag = inputTag;
        this.input = inputProvider;
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
    @Override
    public ITag<?> getGenericInput(){
        return inputTag;
    }

    @Override
    public String getName() {
        return toPath(output) + "_from_blasting";
    }
}
