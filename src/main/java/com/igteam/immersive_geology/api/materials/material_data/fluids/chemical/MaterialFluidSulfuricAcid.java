package com.igteam.immersive_geology.api.materials.material_data.fluids.chemical;

import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGVatProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Rarity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialFluidSulfuricAcid extends MaterialFluidBase {

    @Override
    public String getName() {
        return "sulfuric_acid";
    }

    @Override
    public EnumFluidType getFluidType()
    {
        return EnumFluidType.SOLUTION;
    }

    @Override
    public Set<PeriodicTableElement.ElementProportion> getSoluteElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.HYDROGEN, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
        ));
    }

    @Override
    public float getConcentration() {
        return 0.983f;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.HYDROGEN, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
        ));
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
        return 0xFFFFFF;
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

    @Override
    public IGMaterialProcess getProcessingMethod() {
        IGVatProcessingMethod acid = new IGVatProcessingMethod(1000, 120);
        acid.addFluidOutput(FluidEnum.SulfuricAcid, 125);
        acid.addPrimaryFluidInput(FluidEnum.SulfurDioxide, 125);
        acid.addSecondaryFluidInput(Fluids.WATER, 125);

        inheritedProcessingMethods.add(acid);

        return super.getProcessingMethod();
    }
}
