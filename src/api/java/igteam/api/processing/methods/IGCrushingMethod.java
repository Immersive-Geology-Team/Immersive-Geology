package igteam.api.processing.methods;

import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public class IGCrushingMethod extends IGProcessingMethod {
    public IGCrushingMethod(IGProcessingStage stage) {
        super(RecipeMethod.Crushing, stage);
    }
    private ItemStack output;
    private TagKey<Item> input, secondary;
    private TagKey<Block> blockInput;
    private boolean usingBlockTag = false;
    private float chance = 0;
    private int energy, time;
    private String name;

    public void create(String method_name, TagKey<Item> input, ItemStack output, int energy, int time){
        this.input = input;
        this.output = output;
        this.name = method_name;
        this.energy = energy;
        this.time = time;
    }

    public void create(String method_name, TagKey<Item> input, ItemStack output, TagKey<Item> secondary, int energy, int time, float chance){
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

    public TagKey<Item> getInput() {
        return input;
    }

    public int getEnergy() {
        return energy;
    }

    public int getTime() {
        return time;
    }

    public TagKey<Item> getSecondary() {
        return secondary;
    }

    public float getSecondaryChange() {
        return chance;
    }

    public boolean hasSecondary() {
        return chance != 0;
    }

    public TagKey<Block> getBlockInput() {
        return blockInput;
    }
    public boolean usingBlockTag(){
        return usingBlockTag;
    }

    public void create(String method_name, ItemStack stack, TagKey<Block> blockTag, int energy, int time) {
        usingBlockTag = true;
        this.blockInput = blockTag;
        this.name = method_name;
        this.output = stack;
        this.energy = energy;
        this.time = time;
    }

    public void create(String method_name, ItemStack stack, TagKey<Block> blockTag, TagKey<Item> secondary, int energy, int time, float chance) {
        create(method_name, stack, blockTag, energy, time);
        this.secondary = secondary;
        this.chance = chance;
    }

    @Override
    public ResourceLocation getLocation() {
        return toRL("crushing/crush_" + Objects.requireNonNull(getName()));
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
