package com.igteam.immersive_geology.api.materials.material_data.stones;

import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialStoneVanillaExtrusive extends MaterialStoneBase
{
    //Rhyolite is the default minecraft stone, renamed
    @Override
    public String getName()
    {
        return "extrusive";
    }

    @Nonnull
    @Override
    public String getModID()
    {
        return IGLib.MODID;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM)
        ));
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.COMMON;
    }

    //Based on https://www.tulane.edu/~sanelson/eens1110/igneous.htm
    //A bit based on https://en.wikipedia.org/wiki/Magma#Temperature

    //A totally arbitrary value
    @Override
    public int getBoilingPoint()
    {
        return 900+273+300;
    }

    //An average value
    @Override
    public int getMeltingPoint()
    {
        return 900+273;
    }

    @Override
    public int getColor(int temperature)
    {
        return 0x7a7974;
    }

    //Needs to be changed in code for subtypes, such as sheetmetal
    @Override
    public float getHardness()
    {
        return 1.5F;
    }

    @Override
    public float getMiningResistance()
    {
        return 6.0F;
    }

    @Override
    public float getBlastResistance()
    {
        return 6f;
    }

    //Copied from Immersive Intelligence (steel has i think 1.65, leaves 0.35)
    @Override
    public float getDensity()
    {
        return 1;
    }

    //Stone pickaxe level
    @Override
    public int getBlockHarvestLevel()
    {
        return 0;
    }

    @Override
    public EnumStoneType getStoneType()
    {
        return EnumStoneType.IGNEOUS_EXTRUSIVE;
    }
}
