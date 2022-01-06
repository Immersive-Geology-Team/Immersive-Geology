package igteam.immersive_geology.materials.data.slurry.variants;

import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.data.slurry.MaterialBaseSlurry;
import igteam.immersive_geology.materials.helper.MaterialInterface;

public class MaterialSlurryWrapper extends MaterialBaseSlurry {
    public MaterialSlurryWrapper(MaterialInterface soluteMaterial, FluidEnum base) {
        super(soluteMaterial.getName() + base.getName());
    }
}