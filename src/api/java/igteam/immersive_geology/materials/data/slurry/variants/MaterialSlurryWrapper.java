package igteam.immersive_geology.materials.data.slurry.variants;

import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.data.slurry.MaterialBaseSlurry;
import igteam.immersive_geology.materials.helper.MaterialInterface;

public class MaterialSlurryWrapper extends MaterialBaseSlurry {
    private final MaterialInterface soluteMaterial;
    private final FluidEnum baseFluid;
    public MaterialSlurryWrapper(MaterialInterface soluteMaterial, FluidEnum base) {
        super(soluteMaterial.getName() + base.getName());
        this.soluteMaterial = soluteMaterial;
        this.baseFluid = base;
    }

    public MaterialInterface getSoluteMaterial(){
        return soluteMaterial;
    }

    public FluidEnum getFluidBase(){
        return baseFluid;
    }


}