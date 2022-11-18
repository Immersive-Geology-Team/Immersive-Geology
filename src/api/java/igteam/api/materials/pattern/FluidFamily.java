package igteam.api.materials.pattern;

import igteam.api.menu.ItemSubGroup;

public enum FluidFamily implements MaterialPattern {
    slurry,
    fluid,
    gas; //Not the best way to implement gas, but it works well enough.

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
