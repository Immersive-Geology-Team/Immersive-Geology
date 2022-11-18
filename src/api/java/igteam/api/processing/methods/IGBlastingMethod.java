package igteam.api.processing.methods;

import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class IGBlastingMethod extends IGProcessingMethod {
    public IGBlastingMethod(IGProcessingStage stage) {
        super(RecipeMethod.Blasting, stage);
    }
    private ItemStack output, slag;
    private TagKey<Item> input;

    private String name;

    public void create(String method_name, TagKey<Item> input, ItemStack output){
        this.input = input;
        this.output = output;
        this.name = method_name;
        // slag needs better method of grabbin
        //this.slag = new ItemStack(IEItems.Ingredients.slag);
    }

    public void create(String method_name, TagKey<Item> input, ItemStack output, ItemStack slag){
        this.input = input;
        this.output = output;
        this.name = method_name;
        this.slag = slag;
    }

    public ItemStack getOutput() {
        return output;
    }

    public TagKey<Item> getInput() {
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
    public TagKey<?> getGenericInput(){
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
