package com.igteam.immersivegeology.common.materials.minerals;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MaterialMineralMagnetite extends MaterialMineralBase
{
    @Override
    public String getName()
    {
        return "magnetite";
    }

    @Override
    public String getModID()
    {
        return ImmersiveGeology.MODID;
    }

    @Override
    public Set<PeriodicTableElement.ElementProportion> getElements()
    {
        return new HashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON, 3),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
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
    public int getMeltingPoint() {
        return 1870;
    }

    @Override
    public EnumMineralType getMineralType() {
        return EnumMineralType.MINERAL;
    }

    @Override
    public int getColor(int temperature) {
        return 0xb4b7b0;
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
    public int getBlockHarvestLevel() {
        return 0;
    }
}
