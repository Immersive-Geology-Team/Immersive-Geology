package com.igteam.immersivegeology.common.materials.minerals;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralHematite extends MaterialMineralBase
{
    @Override
    public String getName()
    {
        return "hematite";
    }

    @Override
    public String getModID()
    {
        return ImmersiveGeology.MODID;
    }

    @Override
	public LinkedHashSet<ElementProportion> getElements()
    {
		return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
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
        return 1838;
    }

    @Override
    public EnumMineralType getMineralType() {
        return EnumMineralType.MINERAL;
    }

    @Override
    public int getColor(int temperature) {
        return 0x887c7d;
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
