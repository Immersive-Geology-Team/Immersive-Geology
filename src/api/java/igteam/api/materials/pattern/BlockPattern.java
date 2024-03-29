package igteam.api.materials.pattern;

import igteam.api.menu.ItemSubGroup;

public enum BlockPattern implements MaterialPattern {
    block, //Generic Type
    storage(ItemSubGroup.decoration), // used for storage, eg Block of Material (Iron, Gold, Copper, ...)
    sheetmetal(ItemSubGroup.decoration),
    ore, // used for igteam.immersive_geology.materials that generate like minerals
    clay(ItemSubGroup.natural),
    geode, // used for igteam.immersive_geology.materials that generate like geodes
    stairs(ItemSubGroup.decoration),
    slab(ItemSubGroup.decoration),
    dust_block(ItemSubGroup.processed),
    machine(ItemSubGroup.misc);

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