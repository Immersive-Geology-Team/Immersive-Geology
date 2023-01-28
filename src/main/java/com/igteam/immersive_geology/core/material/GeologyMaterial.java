package com.igteam.immersive_geology.core.material;

import com.google.common.collect.Sets;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.configuration.CommonConfiguration;
import com.igteam.immersive_geology.core.material.helper.IFlagType;
import com.igteam.immersive_geology.core.material.helper.ItemCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialHelper;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class GeologyMaterial implements MaterialHelper {
    protected String name;
    protected Logger logger = ImmersiveGeology.getNewLogger();
    protected Function<IFlagType<?>, Integer> colorFunction; // in goes a category, returns the color white as a default
    protected Predicate<IFlagType<?>> applyColorTint = (flag) -> true;
    private final Set<Enum<?>> materialFlags = Sets.newHashSet();

    public GeologyMaterial(String name) {
        this.name = name;
        this.colorFunction = materialColorFunction();
    }

    public void initializeFlags(){
        List<ItemCategoryFlags> flagList = CommonConfiguration.ITEM_FLAGS.get(name).get();
        
        for (ItemCategoryFlags flag : flagList) {
            addFlags(flag);
        }
    }

    // Used to check properties of the material
    protected void addFlags(IFlagType<?>... flags) {
        for (IFlagType<?> flag : flags) {
            materialFlags.add(flag.getValue());
        }
    }

    protected void removeMaterialFlags(IFlagType<?>... flags){
        for (IFlagType<?> flag : flags) {
            materialFlags.remove(flag.getValue());
        }
    }

    protected boolean hasFlag(IFlagType<?> flag) {
        return materialFlags.contains(flag.getValue());
    }
    @Override
    public String getName() {
        return name;
    }

    public int getColor(IFlagType<?> p) {
        return applyColorTint.test(p) ? colorFunction.apply(p) : 0xFFFFFF;
    }

    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return (flag) -> (0xffffff);
    }

    public Set<Enum<?>> getFlags() {
        return materialFlags;
    }
}
