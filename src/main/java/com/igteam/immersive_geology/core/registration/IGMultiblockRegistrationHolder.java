package com.igteam.immersive_geology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersive_geology.common.block.blocks.*;
import com.igteam.immersive_geology.common.multiblocks.*;
import igteam.immersive_geology.main.IGMultiblockProvider;
import net.minecraft.block.Block;

public class IGMultiblockRegistrationHolder {
    public static void populate() {
        //Steel
        IGMultiblockProvider.chemicalvat = new ChemicalVatBlock();
        IGMultiblockProvider.gravityseparator = new GravitySeparatorBlock();
        IGMultiblockProvider.crystallizer = new CrystallizerBlock();
        IGMultiblockProvider.rotarykiln = new RotaryKilnBlock();

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
    }
}
