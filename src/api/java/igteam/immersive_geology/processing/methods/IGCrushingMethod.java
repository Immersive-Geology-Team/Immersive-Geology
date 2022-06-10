package igteam.immersive_geology.processing.methods;

import blusunrize.immersiveengineering.common.items.IEItems;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

public class IGCrushingMethod extends IGProcessingMethod {
    public IGCrushingMethod(IGProcessingStage stage) {
        super(RecipeMethod.Crushing, stage);
    }
    private ItemStack output;
    private ITag<Item> input, secondary;
    private float chance = 0;
    private int energy, time;
    private String name;

    public void create(String method_name, ItemStack output, ITag<Item> input, int energy, int time){
        this.input = input;
        this.output = output;
        this.name = method_name;
        this.energy = energy;
        this.time = time;
    }

    public void create(String method_name, ItemStack output, ITag<Item> input, ITag<Item> secondary, int energy, int time, float chance){
        create(method_name, output, input, energy, time);
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
