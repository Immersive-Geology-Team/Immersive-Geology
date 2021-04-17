package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralPyrite  extends MaterialMineralBase
{
    @Override
    public String getName()
    {
        return "pyrite";
    }

    @Override
    public String getModID()
    {
        return IGLib.MODID;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR, 2)
        ));
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.COMMON;
    }

    @Override
    public int getBoilingPoint()
    {
        return 2896;
    }

    @Override
    public int getMeltingPoint()
    {
        return 1870;
    }

    @Override
    public EnumMineralType getMineralType()
    {
        return EnumMineralType.CRYSTAL;
    }

    public static int baseColor = 0xD6C380;

    @Override
    public int getColor(int temperature)
    {
        return baseColor;
    }

    @Override
    public float getHardness()
    {
        return 0;
    }

    @Override
    public float getMiningResistance()
    {
        return 0;
    }

    @Override
    public float getBlastResistance()
    {
        return 0;
    }

    @Override
    public float getDensity()
    {
        return 0;
    }

    @Override
    public int getBlockHarvestLevel()
    {
        return 0;
    }
}
