package igteam.immersive_geology.materials.pattern;

import igteam.immersive_geology.menu.ItemSubGroup;

public enum BlockPattern implements MaterialPattern {
    block, //Generic Type
    storage(ItemSubGroup.processed), // used for storage, eg Block of Material (Iron, Gold, Copper, ...)
    ore, // used for igteam.immersive_geology.materials that generate like minerals
    geode, // used for igteam.immersive_geology.materials that generate like geodes
    stairs(ItemSubGroup.processed),
    slab(ItemSubGroup.processed);

    private ItemSubGroup subgroup;

    BlockPattern(){
        this(ItemSubGroup.natural);
    }

    BlockPattern(ItemSubGroup group){
        this.subgroup = group;
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return subgroup;
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