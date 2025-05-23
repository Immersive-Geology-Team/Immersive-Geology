/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer.multiblocks;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.client.render.tile.CrusherRenderer;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.client.renderer.IGBlockEntityRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BallmillLogic;
import com.igteam.immersivegeology.common.config.IGClientConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class BallmillRenderer extends IGBlockEntityRenderer<MultiblockBlockEntityMaster<BallmillLogic.State>>
{
    public static final String DRUM_NAME = "drum";
    public static final String AXLE_NAME = "axle";

    public static IGDynamicModel DRUM, AXLE;

    public BallmillRenderer() {}

    @Override
    public void render(MultiblockBlockEntityMaster<BallmillLogic.State> tile, float pPartialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay)
    {
        boolean doRendering = IGClientConfig.doSpecialRenderBallmill.get();
        if(!doRendering) return;

        IMultiblockBEHelperMaster<BallmillLogic.State> helper = tile.getHelper();
        IMultiblockContext<BallmillLogic.State> context = helper.getContext();

        final MultiblockOrientation orientation = context.getLevel().getOrientation();
        final BallmillLogic.State state = context.getState();
        float rot = state.getRotation();
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        Direction dir = orientation.front();
        boolean isActive = state.shouldRenderActive();
        boolean isMirrored = context.getLevel().getOrientation().mirrored();
        float rotationDir = isMirrored ? -1 : 1;
        if(isMirrored)
        {
            dir = dir.getOpposite();
        }

        poseStack.pushPose();
            rotateForFacing(poseStack, dir);
            poseStack.pushPose();
                poseStack.translate(0.905,2.125,0.5);
                float angleDrum = isActive ? (rot) + pPartialTick : rot;

                poseStack.mulPose(new Quaternionf().rotateAxis((angleDrum * rotationDir) * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)));
                renderDynamicModel(DRUM, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay);
            poseStack.popPose();

            poseStack.pushPose();
                float z = isMirrored ? 0.0625f : 0.9375f;
                poseStack.translate(1.34375,0.775, z);

                float angleAxle = isActive ? (((rot * 2f) + 12) % 360) + pPartialTick : (((rot * 2f) + 12) % 360);
                poseStack.mulPose(new Quaternionf().rotateAxis((angleAxle * -rotationDir) * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)));

                renderDynamicModel(AXLE, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay);
            poseStack.popPose();
        poseStack.popPose();
    }

    private void renderDynamicModel(IGDynamicModel model, PoseStack matrix, MultiBufferSource buffer, Direction facing, Level level, BlockPos pos, int light, int overlay)
    {
        matrix.pushPose();
        List<BakedQuad> quads = model.get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, (RenderType)null);
        rotateForFacing(matrix, facing);
        RenderUtils.renderModelTESRFancy(quads,  buffer.getBuffer(RenderType.solid()), matrix, level, pos, false, 0xffffff, light);

        matrix.popPose();
    }

}
