package igteam.api.processing.methods;

import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public class IGBasicSmeltingMethod extends IGProcessingMethod {

    public IGBasicSmeltingMethod(IGProcessingStage stage) {
        super(RecipeMethod.basicSmelting, stage);
    }
    private ItemLike input, output;
    private TagKey<Item> inputTag;

    public void create(TagKey<Item> inputTag, ItemLike inputProvider, ItemLike output){
        this.inputTag = inputTag;
        this.input = inputProvider;
        this.output = output;
    }

    public ItemLike getInput() {
        return input;
    }

    public ItemLike getOutput() {
        return output;
    }

    @Override
    public ResourceLocation getLocation() {
        return toRL(toPath(output) + "_from_blasting");
    }

    private String toPath(ItemLike src) {
        return Objects.requireNonNull(src.asItem().getRegistryName()).getPath();
    }
    @Override
    public TagKey<?> getGenericInput(){
        return inputTag;
    }

    @Override
    public String getName() {
        return toPath(output) + "_from_blasting";
    }

    @Override
    public ItemStack getGenericOutput() {
        return new ItemStack(output.asItem());
    }
}
