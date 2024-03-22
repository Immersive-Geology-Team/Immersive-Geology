package com.igteam.immersivegeology.common.block.multiblock.crystallizer;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.common.block.multiblock.IGClientMultiblockProperties;
import com.igteam.immersivegeology.common.block.multiblock.IGTemplateMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class CrystallizerMultiblock extends IGTemplateMultiblock {
    public static final CrystallizerMultiblock INSTANCE = new CrystallizerMultiblock();

    private CrystallizerMultiblock(){
        super(new ResourceLocation(IGLib.MODID, "multiblocks/crystallizer"),
                new BlockPos(0,0,0),
                new BlockPos(1,2,1),
                new BlockPos(3,3,3),
                IGMultiblockHolder.CRYSTALLIZER);
    }

    @Override
    public float getManualScale(){
        return 16;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new IGClientMultiblockProperties(this, 2.5, 0.5, 2.5));
    }
}
