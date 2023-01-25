package com.igteam.immersive_geology.material;

import com.igteam.immersive_geology.material.helper.CategoryFlags;
import com.igteam.immersive_geology.material.helper.MaterialFlags;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class GeologyMaterial {

    // Functions
    protected Function<CategoryFlags, Integer> colorFunction = (flag) -> (0xffffff); // in goes a category, returns the color white as a default
    protected Predicate<CategoryFlags> applyColorTint = (flag) -> true;
    // Flags
    private final EnumSet<CategoryFlags> categoryFlags = EnumSet.noneOf(CategoryFlags.class);
    private final EnumSet<MaterialFlags> materialFlags = EnumSet.noneOf(MaterialFlags.class);


    // Used to check what types of items and blocks generate
    protected void addCategories(CategoryFlags... flags) {
        categoryFlags.addAll(List.of(flags));
    }

    protected void removeCategories(CategoryFlags... flags){
        List.of(flags).forEach(categoryFlags::remove);
    }

    protected boolean hasCategory(CategoryFlags flag) {
        return categoryFlags.contains(flag);
    }

    // Used to check properties of the material
    protected void addMaterialFlags(MaterialFlags... flags) {
        materialFlags.addAll(List.of(flags));
    }

    protected void removeMaterialFlags(MaterialFlags... flags){
        List.of(flags).forEach(materialFlags::remove);
    }

    protected boolean hasMaterialFlags(MaterialFlags flag) {
        return materialFlags.contains(flag);
    }
}
