package com.igteam.immersivegeology.common.materials.layer;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialLayerBase;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialLayerMoss extends MaterialLayerBase {
    //this get name is also the name of the texture it needs to use, it finds it via the path texture/block/greyscale/layer/"NAME"
    @Override
    public String getName()
    {
        return "moss";
    }

    @Nonnull
    @Override
    public String getModID()
    {
        return ImmersiveGeology.MODID;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CARBON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.HYDROGEN),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN)
        ));
    }

    @Nonnull
    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
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
        return 0x568756;
    }

    @Override
    public float getHardness() {
        return 1;
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
        return 1;
    }
}
