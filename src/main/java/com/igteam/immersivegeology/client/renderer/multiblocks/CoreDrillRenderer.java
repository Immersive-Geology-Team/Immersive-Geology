package com.igteam.immersivegeology.client.renderer.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.client.renderer.IGBlockEntityRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;

public class CoreDrillRenderer extends IGBlockEntityRenderer<MultiblockBlockEntityMaster<CoreDrillLogic.State>>
{
    @Override
    public void render(MultiblockBlockEntityMaster<CoreDrillLogic.State> tile, float pPartialTick, PoseStack poseStack, MultiBufferSource buffer, int pPackedLight, int pPackedOverlay)
    {
        IMultiblockBEHelperMaster<CoreDrillLogic.State> helper = tile.getHelper();
        IMultiblockContext<CoreDrillLogic.State> context = helper.getContext();

    }
}
