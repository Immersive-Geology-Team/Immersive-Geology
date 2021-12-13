package com.igteam.immersive_geology.api.materials.material_data.fluids.slurry;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.api.tags.IGTags;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Rarity;
import net.minecraft.potion.Effect;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialSlurryWrapper extends MaterialFluidBase {

    @Override
    public String getName()
    {
        return soluteMaterial.getName() + "_" + baseFluid.getName();
    }

    @Override
    public String getDisplayName() {
        return new TranslationTextComponent(I18n.format("fluid." + IGLib.MODID + ".slurry_" + soluteMaterial.getName() + "_" + baseFluid.getName())).getString();
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

    public MaterialSlurryWrapper(MaterialEnum solute_material, FluidEnum base, float strength){
        this(solute_material.getMaterial(), base.getMaterial(), strength);
    }

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
        return (baseFluid.getColor(0) * 128 + soluteMaterial.getColor(0) * (255 - 128)) / 255;
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

    public MaterialFluidBase getBaseFluidMaterial() {
        return baseFluid;
    }

    @Override
    public boolean hasSubtype(MaterialUseType useType)
    {
        switch(useType)
        {
            case BUCKET:
                return hasBucket();
            case FLASK:
                return hasFlask();
            case SLURRY:
                return true;
        }
        return false;
    }
}
