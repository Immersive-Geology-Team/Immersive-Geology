package com.igteam.immersivegeology.common.materials.crystals;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialCrystalBase;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MaterialCrystalQuartz extends MaterialCrystalBase
{
    @Override
    public String getName()
    {
        return "quartz";
    }

    @Nonnull
    @Override
    public String getModID()
    {
        return "minecraft";
    }

    @Override
    public Set<PeriodicTableElement.ElementProportion> getElements() {
        return new HashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
        ));
    }

    @Nonnull
    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public int getBoilingPoint() {
        return 2503;
    }

    @Override
    public int getMeltingPoint() {
        return 1923;
    }

    @Override
    public int getColor(int temperature) {
        return 0xe9dfe0;
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getMiningResistance() {
        return 2;
    }

    @Override
    public float getBlastResistance() {
        return 2;
    }

    @Override
    public float getDensity() {
        return 0.45f;
    }

    @Override
    public int getBlockHarvestLevel() {
        return 1;
    }

    @Override
    public boolean hasRawCrystal() {
        return true;
    }
}
