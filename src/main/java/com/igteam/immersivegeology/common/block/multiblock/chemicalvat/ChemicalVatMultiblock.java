package com.igteam.immersivegeology.common.block.multiblock.chemicalvat;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.common.block.multiblock.IGClientMultiblockProperties;
import com.igteam.immersivegeology.common.block.multiblock.IGTemplateMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ChemicalVatMultiblock extends IGTemplateMultiblock {
    public static final ChemicalVatMultiblock INSTANCE = new ChemicalVatMultiblock();

    private ChemicalVatMultiblock()
    {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/chemicalvat"),
            new BlockPos(3,0,0),
            new BlockPos(3,0,2),
            new BlockPos(4,4,3),
            IGMultiblockHolder.CHEMICALVAT);
    }

    @Override
    public float getManualScale(){
        return 3;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new IGClientMultiblockProperties(this, 0, 0, 0));
    }
}
