package com.igteam.immersive_geology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersive_geology.common.block.blocks.ChemicalVatBlock;
import com.igteam.immersive_geology.common.block.blocks.CrystalyzerBlock;
import com.igteam.immersive_geology.common.block.blocks.GravitySeparatorBlock;
import com.igteam.immersive_geology.common.block.blocks.ReverberationFurnaceBlock;
import com.igteam.immersive_geology.common.multiblocks.ChemicalVatMultiblock;
import com.igteam.immersive_geology.common.multiblocks.GravitySeparatorMultiblock;
import com.igteam.immersive_geology.common.multiblocks.ReverberationFurnaceMultiblock;
import net.minecraft.block.Block;

public class IGMultiblockRegistrationHolder {
    public static class Multiblock {
        //Ore Processing
        public static Block chemicalvat;
        public static Block gravityseparator;
        public static Block industrialseparator;
        public static Block industrialcalcinator;
        public static Block industrialroaster;
        public static Block crystalyzer;
        public static Block reverberation_furnace;
    }

    public static void populate() {
        //Steel
        Multiblock.chemicalvat = new ChemicalVatBlock();
        Multiblock.gravityseparator = new GravitySeparatorBlock();
        Multiblock.crystalyzer = new CrystalyzerBlock();
        //Stone
        Multiblock.reverberation_furnace = new ReverberationFurnaceBlock();
    }

    public static void initialize(){
        MultiblockHandler.registerMultiblock(ChemicalVatMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(GravitySeparatorMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(ReverberationFurnaceMultiblock.INSTANCE);
    }
}
