package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import com.igteam.immersivegeology.common.block.multiblock.chemicalvat.ChemicalVatTileEntity;
import com.igteam.immersivegeology.common.block.multiblock.coredrill.CoreDrillTileEntity;
import com.igteam.immersivegeology.common.block.multiblock.crystallizer.CrystallizerTileEntity;
import com.igteam.immersivegeology.common.block.multiblock.gravityseparator.GravitySeparatorTileEntity;

public class IGTileTypes {
    public static final MultiblockBEType<CrystallizerTileEntity> CRYSTALLIZER = IGRegistrationHolder.registerMultiblockTE("crystallizer", CrystallizerTileEntity::new, IGMultiblockHolder.CRYSTALLIZER);
    public static final MultiblockBEType<GravitySeparatorTileEntity> GRAVITY_SEPARATOR = IGRegistrationHolder.registerMultiblockTE("gravity_separator", GravitySeparatorTileEntity::new, IGMultiblockHolder.GRAVITY_SEPARATOR);
    public static final MultiblockBEType<CoreDrillTileEntity> CORE_DRILL = IGRegistrationHolder.registerMultiblockTE("coredrill", CoreDrillTileEntity::new, IGMultiblockHolder.COREDRILL);
    public static final MultiblockBEType<ChemicalVatTileEntity> CHEMICALVAT = IGRegistrationHolder.registerMultiblockTE("chemicalvat", ChemicalVatTileEntity::new, IGMultiblockHolder.CHEMICALVAT);
}
