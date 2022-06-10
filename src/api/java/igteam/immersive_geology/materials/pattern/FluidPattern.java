package igteam.immersive_geology.materials.pattern;

import igteam.immersive_geology.menu.ItemSubGroup;

public enum FluidPattern implements MaterialPattern {
    slurry,
    fluid;

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return ItemSubGroup.misc;
    }

    @Override
    public boolean isComplexPattern() {
        return false;
    }

    @Override
    public boolean hasSuffix() {
        return false;
    }

    @Override
    public String getSuffix() {
        return null;
    }
}