package com.igteam.immersivegeology.common.materials.minerals;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MaterialMineralUraninite extends MaterialMineralBase
{
    @Override
    public String getName()
    {
        return "uraninite";
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
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.URANIUM, 3),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 8)
        ));
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.UNCOMMON;
    }

    @Override
    public int getBoilingPoint()
    {
        return 4404;
    }

    @Override
    public int getMeltingPoint() {
        return 3138;
    }

    @Override
    public EnumMineralType getMineralType() {
        return EnumMineralType.MINERAL;
    }

    @Override
    public int getColor(int temperature) {
        return 0xB2BEB5;
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
