package com.igteam.immersive_geology.api.materials.material_data.fluids.chemical;

import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import net.minecraft.item.Rarity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialFluidSodiumHydroxide extends MaterialFluidBase {

    public MaterialFluidSodiumHydroxide() {
        useDefaultFluidTextures = true;
    }

    @Override
    public String getName() {
        return "sodium_hydroxide";
    }

    @Override
    public EnumFluidType getFluidType()
    {
        return EnumFluidType.SOLUTION;
    }

    @Override
    public Set<PeriodicTableElement.ElementProportion> getSoluteElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SODIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.HYDROGEN)
        ));
    }

    @Override
    public float getConcentration() {
        return 0.5f;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.HYDROGEN, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN)
        ));
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public int getBoilingPoint() {
        return 323;
    }

    @Override
    public int getMeltingPoint() {
        return 158;
    }

    @Override
    public int getColor(int temperature) {
        return 0xe3ce77;
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
        return 1639;
    }

    @Override
    public int getViscosity() { return 405; }

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
        return 2;
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
