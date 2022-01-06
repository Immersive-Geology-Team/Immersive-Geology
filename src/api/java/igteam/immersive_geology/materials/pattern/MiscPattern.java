package igteam.immersive_geology.materials.pattern;

import igteam.immersive_geology.menu.ItemSubGroup;

public enum MiscPattern implements MaterialPattern {
    slurry,
    fluid,
    machine;

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
}
