package com.igteam.immersive_geology.api.materials;

import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;

public enum MaterialUseType {
    //Remember Naming convention is to add a type in the Singular, so Crystal not Crystals, this is due to how we generate tags! (we add an s to the end)
    //Blocks
    DUST_BLOCK(true, ItemSubGroup.natural),     //Returns Self
    SHEETMETAL(true, ItemSubGroup.processed),     //Returns Self
    STAIRS(true, ItemSubGroup.processed), //Review ME!!
    ORE_STONE(true, ItemSubGroup.natural),
    COBBLESTONE(true, ItemSubGroup.natural),    //Returns Self
    STONE(true, ItemSubGroup.natural),          //Gives Chunks
    GEODE(true, ItemSubGroup.natural),          //Gives Crystals

    //Items
    CHUNK(false, ItemSubGroup.natural),
    ROCK_BIT(false, ItemSubGroup.natural),
    ORE_CHUNK(false, ItemSubGroup.natural),
    ORE_BIT(false, ItemSubGroup.natural),
    CRYSTAL(false, ItemSubGroup.natural),
    CUT_CRYSTAL(false, ItemSubGroup.processed),
    DUST(false, ItemSubGroup.processed),
    BUCKET(false, ItemSubGroup.processed),
    FLUIDS(false, ItemSubGroup.misc),
    ROD(false, ItemSubGroup.processed),
    INGOT(false, ItemSubGroup.processed),
    PLATE(false, ItemSubGroup.processed),
    NUGGET(false, ItemSubGroup.processed),
    GEAR(false, ItemSubGroup.processed),
    WIRE(false, ItemSubGroup.processed),
    TINY_DUST(false, ItemSubGroup.processed);

    public String getName() {
        return this.name().toLowerCase();
    }

    private final boolean isBlock;
    private final ItemSubGroup subGroup;

    MaterialUseType(boolean isBlock, ItemSubGroup subGroup){
        this.isBlock = isBlock;
        this.subGroup = subGroup;
    }

    public ItemSubGroup getSubgroup(){
        return subGroup;
    }

    public boolean isBlock(){
        return isBlock;
    }
}
