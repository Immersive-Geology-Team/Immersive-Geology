package com.igteam.immersive_geology.api.materials.material_data.gases;

import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialGasSulfuric extends MaterialFluidBase {

    @Override
    public String getName() {
        return "sulfuric_dioxide";
    }

    @Override
    public boolean hasSubtype(MaterialUseType useType)
    {
        switch(useType)
        {
            case FLASK:
                return hasFlask();
            case FLUIDS:
                return hasFluidBlock();
        }
        return false;
    }



    @Override
    public EnumFluidType getFluidType() {
        return EnumFluidType.GAS;
    }

    @Override
    public Set<PeriodicTableElement.ElementProportion> getSoluteElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR)
        ));
    }

    @Override
    public float getConcentration() {
        return 0.5f;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return null;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public boolean hasBucket() {
        return false;
    }

    @Override
    public boolean hasFlask() {
        return true;
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
        return 0;
    }
}
