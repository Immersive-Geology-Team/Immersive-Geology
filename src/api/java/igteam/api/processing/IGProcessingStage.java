package igteam.api.processing;

import igteam.api.IGApi;
import igteam.api.materials.data.MaterialBase;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.IGStageDesignation;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.*;

public abstract class IGProcessingStage {
    private final String name;
    private String description;

    private Set<IGProcessingMethod> methods = new LinkedHashSet<>();
    private MaterialBase material;

    public IGProcessingStage(MaterialBase material, IGStageDesignation designation){
        this.name = designation.name();
        this.material = material;
        material.addStage(this);
        describe();
        this.description = material.getName() + " " + designation.name();
    }

    public IGProcessingStage(MaterialBase material, IGStageDesignation designation, String description){
        this(material, designation);
        this.description = description;
    }

    public MaterialBase getParentMaterial(){
        return material;
    }

    protected abstract void describe();

    public Set<IGProcessingMethod> getMethods(){
        return methods;
    }

    public HashSet<IGProcessingMethod> getMethodTree(){
        return new HashSet<>(buildProcessingTree());
    }

    private ArrayList<IGProcessingMethod> buildProcessingTree(){
        ArrayList<IGProcessingMethod> tree = new ArrayList<>();

        Set<IGProcessingMethod> methods = getMethods();
        for (IGProcessingMethod method : methods) {
            ITag<?> inputTag = method.getGenericInput();
            if(inputTag != null)
                IGApi.getNewLogger().info("Input: " + inputTag.toString());

            ItemStack output = method.getGenericOutput();
            if (output != null) {
                String testString = new TranslationTextComponent(output.getItem().getRegistryName().toString()).getString();
                IGApi.getNewLogger().info("Output: " + testString);
            }
        }

        return tree;
    }

    public void addMethod(IGProcessingMethod m) {
        methods.add(m);
    }

    public String getStageName(){
        return name;
    }
}
