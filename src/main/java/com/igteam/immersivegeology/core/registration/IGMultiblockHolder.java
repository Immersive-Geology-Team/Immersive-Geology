package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.igteam.immersivegeology.common.block.multiblock.coredrill.CoreDrillBlock;
import com.igteam.immersivegeology.common.block.multiblock.coredrill.CoreDrillMultiblock;
import com.igteam.immersivegeology.common.block.multiblock.crystallizer.CrystallizerBlock;
import com.igteam.immersivegeology.common.block.multiblock.crystallizer.CrystallizerMultiblock;
import com.igteam.immersivegeology.common.block.multiblock.gravityseparator.GravitySeparatorBlock;
import com.igteam.immersivegeology.common.block.multiblock.gravityseparator.GravitySeparatorMultiblock;
import net.minecraftforge.registries.RegistryObject;

public class IGMultiblockHolder {
    public static final RegistryObject<CrystallizerBlock> CRYSTALLIZER = IGRegistrationHolder.registerMultiblockBlock("crystallizer", CrystallizerBlock::new);
    public static final RegistryObject<GravitySeparatorBlock> GRAVITY_SEPARATOR = IGRegistrationHolder.registerMultiblockBlock("gravity_separator", GravitySeparatorBlock::new);
    public static final RegistryObject<CoreDrillBlock> COREDRILL = IGRegistrationHolder.registerMultiblockBlock("coredrill", CoreDrillBlock::new);

    public static void forceLoad(){};

    public static void initialize(){
        MultiblockHandler.registerMultiblock(CrystallizerMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(GravitySeparatorMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(CoreDrillMultiblock.INSTANCE);
    }
}
