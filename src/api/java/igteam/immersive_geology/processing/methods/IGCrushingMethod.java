package igteam.immersive_geology.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.common.items.IEItems;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.Objects;

public class IGCrushingMethod extends IGProcessingMethod {
    public IGCrushingMethod(IGProcessingStage stage) {
        super(RecipeMethod.Crushing, stage);
    }
    private ItemStack output;
    private ITag<Item> input, secondary;
    private float chance = 0;
    private int energy, time;
    private String name;

    public void create(String method_name, ITag<Item> input,ItemStack output, int energy, int time){
        this.input = input;
        this.output = output;
        this.name = method_name;
        this.energy = energy;
        this.time = time;
    }

    public void create(String method_name, ITag<Item> input, ItemStack output, ITag<Item> secondary, int energy, int time, float chance){
        this.input = input;
        this.output = output;
        this.name = method_name;
        this.energy = energy;
        this.time = time;
        this.secondary = secondary;
        this.chance = chance;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ITag<Item> getInput() {
        return input;
    }
    
    public String getMethodName() {
        return name;
    }

    public int getEnergy() {
        return energy;
    }

    public int getTime() {
        return time;
    }

    public ITag<Item> getSecondary() {
        return secondary;
    }

    public float getSecondaryChange() {
        return chance;
    }

    public boolean hasSecondary() {
        return chance != 0;
    }
}
