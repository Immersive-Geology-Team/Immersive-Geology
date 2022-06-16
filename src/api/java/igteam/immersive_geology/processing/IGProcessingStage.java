package igteam.immersive_geology.processing;

import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.IGStageDesignation;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class IGProcessingStage {
    private final String name;

    private Set<IGProcessingMethod> methods = new LinkedHashSet<>();
    private MaterialBase material;

    public IGProcessingStage(MaterialBase material, String name){
        this.name = name;
        this.material = material;
        material.addStage(this);
        describe();
    }

    public IGProcessingStage(MaterialBase material, IGStageDesignation designation){
        this.name = designation.name();
        this.material = material;
        material.addStage(this);
        describe();
    }

    public MaterialBase getParentMaterial(){
        return material;
    }

    protected abstract void describe();

    public Set<IGProcessingMethod> getMethods(){
        return methods;
    }

    public void addMethod(IGProcessingMethod m) {
        methods.add(m);
    }

    public String getStageName(){
        return name;
    }
}
