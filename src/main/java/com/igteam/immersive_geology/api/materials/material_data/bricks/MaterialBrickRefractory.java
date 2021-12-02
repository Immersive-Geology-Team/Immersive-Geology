package com.igteam.immersive_geology.api.materials.material_data.bricks;

import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialBrickBase;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialBrickRefractory extends MaterialBrickBase
{
    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM, 6),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 13)
        ));
    }

    @Nonnull
    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public int getBoilingPoint() {
        return 3250;
    }

    @Override
    public int getMeltingPoint() {
        return 2327;
    }

    @Override
    public int getColor(int temperature) {
        return 0;
    }

    @Override
    public float getHardness() {
        return 1.0f;
    }

    @Override
    public float getMiningResistance() {
        return 1.0f;
    }

    @Override
    public float getBlastResistance() {
        return 2.5f;
    }

    @Override
    public float getDensity() {
        return 1.3f;
    }
}
