package com.igteam.immersive_geology.common.block.blocks.multiblocks;

import com.igteam.immersive_geology.common.block.helpers.IGMetalMultiblock;
import com.igteam.immersive_geology.common.block.tileentity.GravitySeparatorTileEntity;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import igteam.immersive_geology.materials.MetalEnum;
import net.minecraft.util.ResourceLocation;

public class GravitySeparatorBlock extends IGMetalMultiblock<GravitySeparatorTileEntity> {
    public GravitySeparatorBlock(){
        super("gravityseparator", () -> IGTileTypes.GRAVITY.get());
    }
}