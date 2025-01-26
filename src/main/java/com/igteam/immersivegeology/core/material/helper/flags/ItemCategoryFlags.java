package com.igteam.immersivegeology.core.material.helper.flags;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;

public enum ItemCategoryFlags implements IFlagType<ItemCategoryFlags> {
    INGOT(1),
    WIRE(1),
    GEAR(1),
    ROD(1),
    CLAY(0),
    POWDER(2),
    GRIT (2),
    FUEL(2),
    SLAG(2),
    PELLET(2),
    OXIDE_PELLET(2),
    POWDERED_SLAG(2),
    PLATE(1),
    POOR_ORE(0),
    NORMAL_ORE(0),
    RICH_ORE(0),
    NUGGET(1),
    CRYSTAL(3),
    COMPOUND_DUST(3),
    CRUSHED_ORE(2),
    METAL_OXIDE(3),
    DIRTY_CRUSHED_ORE(2),
    BUCKET(3),
    CLEAN_FLASK(3),
    CLOUDY_FLASK(3),
    MECHANICAL_COMPONENT(1),
    HAMMER(4),
    MISC(4),
    BLUEPRINT(4);

    private final int groupOrdinal;

    ItemCategoryFlags(int group){
        groupOrdinal = group;
    }

    @Override
    public ItemCategoryFlags getValue() {
        return this;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return ItemSubGroup.values()[groupOrdinal];
    }

    public String getTagPrefix()
    {
        switch(this){
            case INGOT, POWDER, GRIT, GEAR,NUGGET,PLATE,ROD,WIRE,PELLET:
                return "s";
            default:
                return "";
        }
    }
}
