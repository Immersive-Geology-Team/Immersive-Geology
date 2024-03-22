package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import com.igteam.immersivegeology.common.block.multiblock.crystallizer.CrystallizerTileEntity;

public class IGTileTypes {
    public static final MultiblockBEType<CrystallizerTileEntity> CRYSTALLIZER = IGRegistrationHolder.registerMultiblockTE("crystallizer", CrystallizerTileEntity::new, IGMultiblockHolder.CRYSTALLIZER);
}
