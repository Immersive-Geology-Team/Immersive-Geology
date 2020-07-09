package com.igteam.immersivegeology.common.materials.minerals;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by JStocke12 on 31-03-2020
 */
public class MaterialMineralPyrolusite extends MaterialMineralBase
{
    @Override
    public String getName()
    {
        return "pyrolusite";
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
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.MANGANESE),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2)
        ));
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.RARE;
    }

    @Override
    public int getBoilingPoint()
    {
        return 4673;
    }

    @Override
    public int getMeltingPoint() {
        return 3663;
    }

    @Override
    public EnumMineralType getMineralType() {
        return EnumMineralType.MINERAL;
    }

    protected static int baseColor = 0xc68f39;
    
    @Override
    public int getColor(int temperature) {
        return baseColor;
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