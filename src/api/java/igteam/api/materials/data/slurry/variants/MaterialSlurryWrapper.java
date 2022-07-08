package igteam.api.materials.data.slurry.variants;

import igteam.api.IGApi;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.materials.data.slurry.MaterialBaseSlurry;

import java.awt.*;

public class MaterialSlurryWrapper extends MaterialBaseSlurry {
    private final MaterialInterface<? extends MaterialBase> soluteMaterial;
    private final FluidEnum baseFluid;
    public MaterialSlurryWrapper(MaterialInterface<? extends MaterialBase> soluteMaterial, FluidEnum base) {
        super(base.getName() + "_" + soluteMaterial.getName());
        this.soluteMaterial = soluteMaterial;
        this.baseFluid = base;
    }

    public MaterialInterface<? extends MaterialBase> getSoluteMaterial(){
        return soluteMaterial;
    }

    public FluidEnum getFluidBase(){
        return baseFluid;
    }

    @Override
    public int getColor(MaterialPattern p) {
        return IGApi.mixColors(new Color(getFluidBase().getColor(p)), new Color(getSoluteMaterial().getColor(p))).getRGB();
    }

    @Override
    public boolean hasFlask() {
        return true;
    }
}