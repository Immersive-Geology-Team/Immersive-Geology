package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

public class IGArcSmeltingMethod extends IGProcessingMethod {

    public IGArcSmeltingMethod(IGProcessingStage stage) {
        super(RecipeMethod.arcSmelting, stage);
    }
    private IngredientWithSize input;
    private ITag<?> inputTag;

    private ItemStack slag, output;
    private List<IngredientWithSize> additives;
    int energy, time;
    private String name;

    public IGArcSmeltingMethod create(String method_name, ITag<Item> input, int inputAmount, ItemStack output, @Nullable ItemStack iSlag, IngredientWithSize... additives){
        this.inputTag = input;
        this.input = new IngredientWithSize(input, inputAmount);
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

    @Override
    public ResourceLocation getLocation() {
        return toRL("arc_smelting/arc_" + Objects.requireNonNull(getName()));
    }

    @Override
    public ITag<?> getGenericInput(){
        return inputTag;
    }

    @Override
    public ItemStack getGenericOutput() {
        return output;
    }
}


