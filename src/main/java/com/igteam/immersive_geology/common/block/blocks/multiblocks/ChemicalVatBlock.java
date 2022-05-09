package com.igteam.immersive_geology.common.block.blocks.multiblocks;

import com.igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import com.igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import igteam.immersive_geology.materials.MetalEnum;
import net.minecraft.util.ResourceLocation;

public class ChemicalVatBlock extends IGMetalMultiblock<ChemicalVatTileEntity> {

    public ChemicalVatBlock(){
        super("chemicalvat", () -> IGTileTypes.VAT.get());
    }
}
