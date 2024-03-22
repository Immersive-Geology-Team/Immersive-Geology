package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersivegeology.common.block.multiblock.crystallizer.CrystallizerBlock;
import com.igteam.immersivegeology.common.block.multiblock.crystallizer.CrystallizerMultiblock;
import net.minecraftforge.registries.RegistryObject;

public class IGMultiblockHolder {
    public static final RegistryObject<CrystallizerBlock> CRYSTALLIZER = IGRegistrationHolder.registerMultiblockBlock("crystallizer", CrystallizerBlock::new);

    public static void forceLoad(){};

    public static void initialize(){
        MultiblockHandler.registerMultiblock(CrystallizerMultiblock.INSTANCE);
    }
}
