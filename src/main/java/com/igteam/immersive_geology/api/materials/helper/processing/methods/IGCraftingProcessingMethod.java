package com.igteam.immersive_geology.api.materials.helper.processing.methods;

import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.ProcessingMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.*;

public class IGCraftingProcessingMethod extends IGProcessingMethod {

    String criterionName;
    ITag<Item> tag;

    public IGCraftingProcessingMethod(String criterionName, ITag<Item> tag) {
        this.tag = tag;
        this.criterionName = criterionName;
    }

    @Override
    public ProcessingMethod getKey() {
        return ProcessingMethod.CRAFTING;
    }

    @Override
    public int getEnergyCost() {
        return 0;
    }

    @Override
    public int getProcessingTime() {
        return 0;
    }

    ArrayList<Item> shapelessInputs = new ArrayList<Item>();
    ItemStack output;

    public IGCraftingProcessingMethod setShapeless(Item... inputs){
        shapelessInputs.addAll(Arrays.asList(inputs));
        return this;
    }

    public IGCraftingProcessingMethod setOutput(ItemStack output){
        assert output != null && !output.isEmpty() : "Output is Null? for " + getKey().name();

        this.output = output;
        return this;
    }

    public boolean isShapeless(){
        return !shapelessInputs.isEmpty();
    }

    public ArrayList<Item> getShapelessInputs() {
        return shapelessInputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public String getCriterionName(){
        return "";
    }

    public ITag<Item> getCriterion(){
        return tag;
    }


    public IGCraftingProcessingMethod setPattern(String top, String middle, String bottom){
        this.topline = top;
        this.midline = middle;
        this.botline = bottom;

        return this;
    }
    private String topline, midline, botline;
    public IGCraftingProcessingMethod setItemToKey(char key, Item stack){
        keyMap.put(key, stack);
        return this;
    }

    public Collection<Item> getShapedInputs(){
        return keyMap.values();
    }

    protected HashMap<Character, Item> keyMap = new HashMap<>();

    public Set<Character> getPatternKeys(){
        return keyMap.keySet();
    }

    public Item getItemByKey(Character key){
        return keyMap.get(key);
    }

    public String topLine(){
        return topline;
    }

    public String midLine(){
        return midline;
    }

    public String botLine(){
        return botline;
    }

}
