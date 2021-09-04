package com.igteam.immersive_geology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersive_geology.common.block.blocks.ChemicalVatBlock;
import com.igteam.immersive_geology.common.multiblocks.ChemicalVatMultiblock;
import net.minecraft.block.Block;

public class IGMultiblockRegistrationHolder {
    public static class Multiblock {
        //Ore Processing
        public static Block chemicalvat;
        public static Block electrolizer;
        public static Block oreseparator;
        public static Block hiveoven;
        public static Block industrialcalcinator;
        public static Block industrialroaster;

    }

    public static void populate() {
        Multiblock.chemicalvat = new ChemicalVatBlock();
    }

    public static void initialize(){
        MultiblockHandler.registerMultiblock(ChemicalVatMultiblock.INSTANCE);
    }
}
