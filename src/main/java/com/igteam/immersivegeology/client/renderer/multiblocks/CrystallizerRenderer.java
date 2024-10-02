/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CrystallizerLogic;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;

public class CrystallizerRenderer implements BlockEntityRenderer<MultiblockBlockEntityMaster<CrystallizerLogic.State>>
{

    public CrystallizerRenderer(){};

    @Override
    public void render(MultiblockBlockEntityMaster<CrystallizerLogic.State> tile, float pPartialTick, PoseStack poseStack, MultiBufferSource buffer, int pPackedLight, int pPackedOverlay) {
        IMultiblockBEHelperMaster<CrystallizerLogic.State> helper = tile.getHelper();
        IMultiblockContext<CrystallizerLogic.State> context = helper.getContext();
    }
}
