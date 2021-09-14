package com.igteam.immersive_geology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersive_geology.common.block.blocks.ChemicalVatBlock;
import com.igteam.immersive_geology.common.block.blocks.GravitySeparatorBlock;
import com.igteam.immersive_geology.common.multiblocks.ChemicalVatMultiblock;
import com.igteam.immersive_geology.common.multiblocks.GravitySeparatorMultiblock;
import net.minecraft.block.Block;

public class IGMultiblockRegistrationHolder {
    public static class Multiblock {
        //Ore Processing
        public static Block chemicalvat;
        public static Block gravityseparator;
        public static Block industrialseparator;
        public static Block industrialcalcinator;
        public static Block industrialroaster;
        public static Block electrolizer;
    }

    public static void populate() {
        Multiblock.chemicalvat = new ChemicalVatBlock();
        Multiblock.gravityseparator = new GravitySeparatorBlock();
    }

    public static void initialize(){
        MultiblockHandler.registerMultiblock(ChemicalVatMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(GravitySeparatorMultiblock.INSTANCE);
    }
}
