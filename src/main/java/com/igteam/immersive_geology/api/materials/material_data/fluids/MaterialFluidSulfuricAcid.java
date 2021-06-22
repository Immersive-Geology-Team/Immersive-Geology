package com.igteam.immersive_geology.api.materials.material_data.fluids;

import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import net.minecraft.item.Rarity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;

import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialFluidSulfuricAcid extends MaterialFluidBase {

    @Override
    public String getName() {
        return "sulfuric_acid";
    }

    @Override
    public EnumFluidType getFluidType() {
        return EnumFluidType.FLUID;
    }

    @Override
    public Set<PeriodicTableElement.ElementProportion> getSoluteElements() {
        return null;
    }

    @Override
    public float getConcentration() {
        return 0;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return null;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public int getBoilingPoint() {
        return 0;
    }

    @Override
    public int getMeltingPoint() {
        return 0;
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
        return 1830;
    }

    @Override
    public int getViscosity() { return 2100; }

    @Override
    public Effect getContactEffect(){
        return Effects.WITHER;
    }

    @Override
    public int getContactEffectDuration(){
        return 40;
    }

    @Override
    public int getContactEffectLevel(){
        return 4;
    }

    @Override
    public boolean hasFlask() {
        return true;
    }

    @Override
    public boolean hasBucket() {
        return false;
    }
}
