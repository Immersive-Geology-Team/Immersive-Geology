package com.igteam.immersive_geology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersive_geology.common.block.blocks.multiblocks.*;
import com.igteam.immersive_geology.common.multiblocks.*;
import igteam.immersive_geology.main.IGMultiblockProvider;

public class IGMultiblockRegistrationHolder {
    public static void populate() {
        //Steel
        IGMultiblockProvider.chemicalvat = new ChemicalVatBlock();
        IGMultiblockProvider.gravityseparator = new GravitySeparatorBlock();
        IGMultiblockProvider.crystallizer = new CrystallizerBlock();
        IGMultiblockProvider.rotarykiln = new RotaryKilnBlock();

        //First location a Multiblock is set in. While static reference is a Block type, it needs to be a IGMetalMultiblock which extends IE's MetalMultiblockBlock
        //Used in IGTileTypes - this is where the Block is linked with it's TileEntity
        IGMultiblockProvider.hydrojet_cutter = new HydroJetCutterBlock();

        //Stone
        IGMultiblockProvider.reverberation_furnace = new ReverberationFurnaceBlock();
        IGMultiblockProvider.bloomery = new BloomeryBlock();
    }

    //Not the cause of network issue, try elsewhere
    public static void initialize() {
        MultiblockHandler.registerMultiblock(ChemicalVatMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(GravitySeparatorMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(ReverberationFurnaceMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(CrystallizerMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(RotaryKilnMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(HydroJetCutterMultiblock.INSTANCE);
    }
}
