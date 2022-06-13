package igteam.immersive_geology.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.immersive_geology.materials.SlurryEnum;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;

import javax.annotation.Nullable;
import java.util.List;

public class IGArcSmeltingMethod extends IGProcessingMethod {

    public IGArcSmeltingMethod(IGProcessingStage stage) {
        super(RecipeMethod.arcSmelting, stage);
    }
    private IngredientWithSize input;
    private ItemStack slag, output;
    private IngredientWithSize[] additives;
    int energy, time;

    public void create(IngredientWithSize input, ItemStack output, @Nullable ItemStack iSlag, IngredientWithSize... additives){
        this.input = input;
        this.output = output;
        this.slag = iSlag == null ? ItemStack.EMPTY : iSlag;
        this.additives = additives;
    }

    public void create(IngredientWithSize input, ItemStack output, @Nullable ItemStack Slag, List<ITag<Item>> tagAdditives, @Nullable List<Integer> tagCounts){
//        MergeFunction<IngredientWithSize[], List<ITag<Item>>, List<Integer>> function = (tags, counts) -> {
//            IngredientWithSize[] l = new IngredientWithSize[tags.length];
//            for (ITag<Item> tag : tags) {
//
//            }
//            return l;
//        };
    }

    public void create(ITag<Item> input, int inputCount, ItemStack output, @Nullable ItemStack Slag, IngredientWithSize... additives){

    }


    public void create(ITag<Item> input, int inputCount, ItemStack output, @Nullable ItemStack Slag, List<ITag<Item>> tagAdditives, @Nullable List<Integer> tagCounts){

    }

    public void create(ItemStack input, ItemStack output, @Nullable ItemStack Slag, List<ITag<Item>> tagAdditives, @Nullable List<Integer> tagCounts){

    }

    public IngredientWithSize getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getSlag() {
        return slag;
    }

    public int getEnergy() {
        return energy;
    }

    public int getTime(){
        return time;
    }

    @FunctionalInterface
    protected interface MergeFunction<R, T, I> {
        R merge(T[] type, I[] count);
    }
}


