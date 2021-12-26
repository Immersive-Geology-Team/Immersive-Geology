package com.igteam.immersive_geology.api.materials.material_data.fluids.chemical;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGVatProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialFluidNitricAcid extends MaterialFluidBase {

    public MaterialFluidNitricAcid() {
        useDefaultFluidTextures = true;
    }

    @Override
    public String getName() {
        return "nitric_acid";
    }

    @Override
    public EnumFluidType getFluidType()
    {
        return EnumFluidType.SOLUTION;
    }

    @Override
    public Set<PeriodicTableElement.ElementProportion> getSoluteElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.HYDROGEN),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.NITROGEN),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN,3)
        ));
    }

    @Override
    public float getConcentration() {
        return 0.95f;
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


    @Override
    public IGMaterialProcess getProcessingMethod()
    {
        IGVatProcessingMethod sulfuric_saltpeter_method = new IGVatProcessingMethod(1000, 120);
        sulfuric_saltpeter_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Sodium.getMaterial(), MaterialUseType.COMPOUND_DUST),1));
        sulfuric_saltpeter_method.addFluidOutput(FluidEnum.NitricAcid, 125);
        sulfuric_saltpeter_method.addPrimaryFluidInput(FluidEnum.SulfuricAcid, 125);
        sulfuric_saltpeter_method.addSecondaryFluidInput(Fluids.WATER, 125);
        sulfuric_saltpeter_method.addItemInput(
                new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.SaltPeter.getMaterial(), MaterialUseType.DUST), 1)
        ); //FIXME -- wiggle it to have just forge:dusts/saltpeter as intput. Make vat recipes accept tags.

        inheritedProcessingMethods.add(sulfuric_saltpeter_method);
        
        return super.getProcessingMethod();

    }
}
