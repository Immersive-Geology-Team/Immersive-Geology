package igteam.immersive_geology.processing;

import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class IGProcessingStage {
    private final String name;

    private Set<IGProcessingMethod> methods = new LinkedHashSet<>();

    public IGProcessingStage(String name){
        this.name = name;
        describe();
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

    public void build(MaterialBase material) {
        material.addStage(this);
    }
}
