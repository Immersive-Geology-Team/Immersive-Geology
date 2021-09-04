package com.igteam.immersive_geology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersive_geology.common.block.blocks.ChemicalVatBlock;
import com.igteam.immersive_geology.common.block.blocks.GravitySeperatorBlock;
import com.igteam.immersive_geology.common.multiblocks.ChemicalVatMultiblock;
import com.igteam.immersive_geology.common.multiblocks.GravitySeperatorMultiblock;
import net.minecraft.block.Block;

public class IGMultiblockRegistrationHolder {
    public static class Multiblock {
        //Ore Processing
        public static Block chemicalvat;
        public static Block gravityseperator;
        public static Block industrialseperator;
        public static Block industrialcalcinator;
        public static Block industrialroaster;
        public static Block electrolizer;
    }

    public static void populate() {
        Multiblock.chemicalvat = new ChemicalVatBlock();
        Multiblock.gravityseperator = new GravitySeperatorBlock();
    }

    public static void initialize(){
        MultiblockHandler.registerMultiblock(ChemicalVatMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(GravitySeperatorMultiblock.INSTANCE);
    }
}
