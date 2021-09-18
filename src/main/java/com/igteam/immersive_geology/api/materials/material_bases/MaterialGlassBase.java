package com.igteam.immersive_geology.api.materials.material_bases;

import com.igteam.immersive_geology.api.materials.*;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Note, this class is a only here to create the empty flask... Due to how IG is setup for mass registration for any material, this material is a tad bit of an outlier.
 * For future implementations, find a way for single item registration that doesn't require a new overarching type registration.
 */
public class MaterialGlassBase extends Material {

    @Override
    public String getName() {
        return "glass";
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2)
        ));
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public MaterialTypes getMaterialType() {
        return MaterialTypes.MINERAL;
    }

    @Override
    public MaterialTypes getMaterialSubType() {
        return MaterialTypes.CRYSTAL;
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
        return 0xffffff;
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
    public MaterialEnum getProcessedType() {
        return null;
    }

    @Override
    public MaterialEnum getSecondaryType() {
        return null;
    }

    @Override
    public boolean preExists() {
        return false;
    }

    @Override
    public boolean hasSubtype(MaterialUseType useType) {
        switch (useType) {
            case FLASK:
                return true;
            default:
                return false;
        }
    }
}
