package com.igteam.immersive_geology.api.materials;

import com.igteam.immersive_geology.client.menu.IGItemGroup;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;

public enum MaterialUseType {
    //Blocks
    DUST_BLOCK(true, ItemSubGroup.natrual),     //Returns Self
    SHEET_METAL(true, ItemSubGroup.processed),     //Returns Self
    ORE_STONE(true, ItemSubGroup.natrual),
    COBBLESTONE(true, ItemSubGroup.natrual),    //Returns Self
    STONE(true, ItemSubGroup.natrual),          //Gives Chunks
    GEODE(true, ItemSubGroup.natrual),          //Gives Crystals

    //Items
    CHUNK(false, ItemSubGroup.natrual),
    ROCK_BIT(false, ItemSubGroup.natrual),
    ORE_CHUNK(false, ItemSubGroup.natrual),
    ORE_BIT(false, ItemSubGroup.natrual),
    CRYSTAL(false, ItemSubGroup.natrual),
    CUT_CRYSTAL(false, ItemSubGroup.processed),
    DUST(false, ItemSubGroup.processed),
    BUCKET(false, ItemSubGroup.processed),
    FLUIDS(false, ItemSubGroup.misc),
    ROD(false, ItemSubGroup.processed),
    INGOT(false, ItemSubGroup.processed),
    PLATE(false, ItemSubGroup.processed),
    NUGGET(false, ItemSubGroup.processed),
    GEAR(false, ItemSubGroup.processed),
    WIRE(false, ItemSubGroup.processed);

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
