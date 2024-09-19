package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.OptionalDouble;
import java.util.function.Consumer;

public class IGCrystalizerMultiblock extends IGTemplateMultiblock {

	public static final IGCrystalizerMultiblock INSTANCE = new IGCrystalizerMultiblock();

	public IGCrystalizerMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/crystallizer"), new BlockPos(0,0,0), new BlockPos(1,2,1), new BlockPos(3,3,3), IGMultiblockProvider.CRYSTALLIZER);
    }

    @Override
    public float getManualScale() {
        return 12.0f;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new CrystallizerClientData(INSTANCE, 0.5, 0.5, 0.5));
    }

    public static class CrystallizerClientData extends IGClientMultiblockProperties {
        public CrystallizerClientData(IGTemplateMultiblock multiblock, double offX, double offY, double offZ){
            super(multiblock, offX, offY, offZ);
        }
	}
}
