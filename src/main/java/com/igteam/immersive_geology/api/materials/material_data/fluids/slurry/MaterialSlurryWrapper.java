package com.igteam.immersive_geology.api.materials.material_data.fluids.slurry;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;
import net.minecraft.potion.Effect;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialSlurryWrapper extends MaterialFluidBase {

    @Override
    public String getName()
    {
        return soluteMaterial.getName() + "_"+baseFluid.getName()+"_"+"slurry";
    }

    @Override
    public String getModID()
    {
        return IGLib.MODID;
    }


    protected final Material soluteMaterial;
    protected final MaterialFluidBase baseFluid;
    protected final float concentration;

    //We need to parse through everything a slurry type will use!
    public MaterialSlurryWrapper(Material solute_material, MaterialFluidBase base, float strength){
        this.soluteMaterial = solute_material;
        this.baseFluid = base;
        this.concentration = strength;
    }

    @Override
    public EnumFluidType getFluidType() {
        return EnumFluidType.SOLUTION;
    }

    @Override
    public Set<PeriodicTableElement.ElementProportion> getSoluteElements() {
        return soluteMaterial.getElements();
    }

    @Override
    public float getConcentration() {
        return this.concentration;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return baseFluid.getElements();
    }

    @Nonnull
    @Override
    public Rarity getRarity() {
        return soluteMaterial.getRarity();
    }

    @Override
    public int getBoilingPoint() {
        return baseFluid.getBoilingPoint();
    }

    @Override
    public int getMeltingPoint() {
        return soluteMaterial.getMeltingPoint();
    }

    @Override
    public int getColor(int temperature) {
        return 0;
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public float getMiningResistance() {
        return 0;
    }

    @Override
    public float getBlastResistance() {
        return 0;
    }

    @Override
    public float getDensity() {
        return 0;
    }

    @Override
    public int getViscosity() {
        return baseFluid.getViscosity();
    }

    @Override
    public Effect getContactEffect() {
        return baseFluid.getContactEffect();
    }

    @Override
    public int getContactEffectDuration() {
        return baseFluid.getContactEffectDuration();
    }

    @Override
    public int getContactEffectLevel() {
        return baseFluid.getContactEffectLevel();
    }

    @Override
    public boolean hasBucket() {
        return true;
    }

    @Override
    public boolean hasFlask() {
        return false;
    }

    public Material getSoluteMaterial() {
        return soluteMaterial;
    }
}
