package igteam.api.materials.pattern;

import igteam.api.menu.ItemSubGroup;
import igteam.api.tags.IGTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public enum ItemFamily implements MaterialPattern {
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
    crystal(ItemSubGroup.processed),
    clay(ItemSubGroup.natural, ""),
    fuel,
    slag(ItemSubGroup.processed),
    flask(ItemSubGroup.misc),
    bucket(ItemSubGroup.misc),
    block_item;

    private final ItemSubGroup subgroup;
    private final boolean isComplex;
    private final String suffix;
    private final boolean hasSuffix;


    ItemFamily(ItemSubGroup group, boolean isComplex){
        this.subgroup = group;
        this.isComplex = isComplex;
        this.suffix = "";
        this.hasSuffix = false;
    }

    ItemFamily(ItemSubGroup group, String suffix){
        this.subgroup = group;
        this.isComplex = false;
        this.suffix = suffix;
        this.hasSuffix = true;
    }

    ItemFamily(ItemSubGroup group, boolean isComplex, String suffix){
        this.subgroup = group;
        this.isComplex = isComplex;
        this.suffix = suffix;
        this.hasSuffix = true;
    }

    ItemFamily(ItemSubGroup group){
        this(group, false);
    }

    ItemFamily(boolean isComplex){
        this.subgroup = ItemSubGroup.natural;
        this.isComplex = isComplex;
        this.suffix = "";
        this.hasSuffix = false;
    }

    ItemFamily(){
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

    public TagKey<Item> getPatternGroup(){
        return IGTags.IG_PATTERN_GROUP_TAGS.get(this);
    }

}
