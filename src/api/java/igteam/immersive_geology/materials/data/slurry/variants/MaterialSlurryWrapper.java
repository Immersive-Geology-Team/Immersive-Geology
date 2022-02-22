package igteam.immersive_geology.materials.data.slurry.variants;

import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.data.slurry.MaterialBaseSlurry;
import igteam.immersive_geology.materials.helper.MaterialInterface;

public class MaterialSlurryWrapper extends MaterialBaseSlurry {
    private final MaterialInterface<? extends MaterialBase> soluteMaterial;
    private final FluidEnum baseFluid;
    public MaterialSlurryWrapper(MaterialInterface<? extends MaterialBase> soluteMaterial, FluidEnum base) {
        super(soluteMaterial.getName() + base.getName());
        this.soluteMaterial = soluteMaterial;
        this.baseFluid = base;
    }

    public MaterialInterface<? extends MaterialBase> getSoluteMaterial(){
        return soluteMaterial;
    }

    public FluidEnum getFluidBase(){
        return baseFluid;
    }


}