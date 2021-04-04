package com.igteam.immersive_geology.api.materials;

public enum MaterialUseType {
    //Blocks
    DUST_BLOCK,     //Returns Self
    SHEETMETAL,     //Returns Self
    ORE_STONE,      //Gives Ore Chunks
    COBBLESTONE,    //Returns Self
    STONE,          //Gives Chunks
    GEODE,          //Gives Crystals

    //Items
    CHUNK,
    BIT,
    ORE_CHUNK,
    ORE_BIT,
    CRYSTAL,
    CUT_CRYSTAL,
    DUST,
    BUCKET,
    FLUIDS,
    ROD,
    INGOT,
    PLATE,
    NUGGET,
    GEAR,
    WIRE;

    public String getName() {
        return this.name();
    }
}
