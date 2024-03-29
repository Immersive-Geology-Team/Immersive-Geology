package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.common.items.IEItems;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class IGBlastingMethod extends IGProcessingMethod {
    public IGBlastingMethod(IGProcessingStage stage) {
        super(RecipeMethod.Blasting, stage);
    }
    private ItemStack output, slag;
    private ITag<Item> input;

    private String name;

    public void create(String method_name, ITag<Item> input, ItemStack output){
        this.input = input;
        this.output = output;
        this.name = method_name;
        this.slag = new ItemStack(IEItems.Ingredients.slag);
    }

    public void create(String method_name, ITag<Item> input, ItemStack output, ItemStack slag){
        this.input = input;
        this.output = output;
        this.name = method_name;
        this.slag = slag;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ITag<Item> getInput() {
        return input;
    }

    public ItemStack getSlag() {
        return slag;
    }

    @Override
    public ResourceLocation getLocation() {
        return toRL("blasting/blast_" + Objects.requireNonNull(getName()));
    }

    @Override
    public ITag<?> getGenericInput(){
        return input;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getGenericOutput() {
        return output;
    }
}
