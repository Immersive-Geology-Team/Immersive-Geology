package com.igteam.immersive_geology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersive_geology.common.block.blocks.*;
import com.igteam.immersive_geology.common.multiblocks.*;
import net.minecraft.block.Block;

public class IGMultiblockRegistrationHolder {
    public static class Multiblock {
        //Ore Processing
        public static Block chemicalvat;
        public static Block gravityseparator;
        public static Block rotarykiln;
        public static Block crystallizer;
        public static Block reverberation_furnace;
    }

    public static void populate() {
        //Steel
        Multiblock.chemicalvat = new ChemicalVatBlock();
        Multiblock.gravityseparator = new GravitySeparatorBlock();
        Multiblock.crystallizer = new CrystallizerBlock();
        Multiblock.rotarykiln = new RotaryKilnBlock();

        //Stone
        Multiblock.reverberation_furnace = new ReverberationFurnaceBlock();
    }

    //Not the cause of network issue, try elsewhere
    public static void initialize() {
        MultiblockHandler.registerMultiblock(ChemicalVatMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(GravitySeparatorMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(ReverberationFurnaceMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(CrystallizerMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(RotaryKilnMultiblock.INSTANCE);
    }
}
