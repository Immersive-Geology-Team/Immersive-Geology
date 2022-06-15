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
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class IGArcSmeltingMethod extends IGProcessingMethod {

    public IGArcSmeltingMethod(IGProcessingStage stage) {
        super(RecipeMethod.arcSmelting, stage);
    }
    private IngredientWithSize input;
    private ItemStack slag, output;
    private List<IngredientWithSize> additives;
    int energy, time;

    private String name;

    public IGArcSmeltingMethod create(String method_name, IngredientWithSize input, ItemStack output, @Nullable ItemStack iSlag, IngredientWithSize... additives){
        this.input = input;
        this.output = output;
        this.slag = iSlag == null ? ItemStack.EMPTY : iSlag;
        this.additives = asList(additives);
        this.name = method_name;
        return this;
    }

    public IGArcSmeltingMethod create(String method_name, IngredientWithSize input, ItemStack output, @Nullable ItemStack slag){
        this.input = input;
        this.output = output;
        this.slag = slag == null ? ItemStack.EMPTY : slag;
        this.additives = new ArrayList<>();
        this.name = method_name;
        return this;
    }

    public IGArcSmeltingMethod addAdditive(ITag<Item> item, int count){
        this.additives.add(new IngredientWithSize(item, count));
        return this;
    }

    public void setEnergyTime(int energy, int time)
    {
        this.time = time;
        this.energy = energy;
    }
    public String getName(){
        return this.name;
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


    public IngredientWithSize[] getAdditives() { return this.additives.toArray(new IngredientWithSize[this.additives.size()]);}

}


