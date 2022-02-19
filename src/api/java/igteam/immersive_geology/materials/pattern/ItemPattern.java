package igteam.immersive_geology.materials.pattern;

import igteam.immersive_geology.menu.ItemSubGroup;

public enum ItemPattern implements MaterialPattern {
    ingot(ItemSubGroup.processed),
    stone_chunk,
    stone_bit,
    ore_chunk(true),
    ore_bit(true),
    crushed_ore,
    dirty_crushed_ore(true),
    dust(ItemSubGroup.processed),
    compound_dust(ItemSubGroup.processed),
    metal_oxide(ItemSubGroup.processed),
    wire(ItemSubGroup.processed),
    gear(ItemSubGroup.processed),
    rod(ItemSubGroup.processed),
    plate(ItemSubGroup.processed),
    nugget(ItemSubGroup.processed),
    crystal,
    clay(ItemSubGroup.misc, ""),
    fuel,
    slag(ItemSubGroup.misc),
    flask(ItemSubGroup.misc),
    bucket(ItemSubGroup.misc),
    block_item;

    private final ItemSubGroup subgroup;
    private final boolean isComplex;
    private final String suffix;
    private final boolean hasSuffix;


    ItemPattern(ItemSubGroup group, boolean isComplex){
        this.subgroup = group;
        this.isComplex = isComplex;
        this.suffix = "";
        this.hasSuffix = false;
    }

    ItemPattern(ItemSubGroup group, String suffix){
        this.subgroup = group;
        this.isComplex = false;
        this.suffix = suffix;
        this.hasSuffix = true;
    }

    ItemPattern(ItemSubGroup group, boolean isComplex, String suffix){
        this.subgroup = group;
        this.isComplex = isComplex;
        this.suffix = suffix;
        this.hasSuffix = true;
    }

    ItemPattern(ItemSubGroup group){
        this(group, false);
    }

    ItemPattern(boolean isComplex){
        this.subgroup = ItemSubGroup.natural;
        this.isComplex = isComplex;
        this.suffix = "";
        this.hasSuffix = false;
    }

    ItemPattern(){
        this(ItemSubGroup.natural, false);
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
        return isComplex;
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
