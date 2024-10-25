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
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.client.renderer.IGBlockEntityRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BallmillLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic.State;
import com.mojang.blaze3d.vertex.PoseStack;
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
    public static final String CHAMBER_NAME = "ballmill_chamber";

    public static IGDynamicModel CHAMBER;

    @Override
    public void render(MultiblockBlockEntityMaster<BallmillLogic.State> tile, float pPartialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay)
    {
        IMultiblockBEHelperMaster<BallmillLogic.State> helper = tile.getHelper();
        IMultiblockContext<BallmillLogic.State> context = helper.getContext();

        final MultiblockOrientation orientation = context.getLevel().getOrientation();
        final BallmillLogic.State state = context.getState();
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        Direction dir = orientation.front();
        boolean isActive = state.rsState.isEnabled(context);
        poseStack.pushPose();
        poseStack.translate(0.5,2,2.21875);
        float angle = isActive ? (state.getRotation() * 3) + pPartialTick : 0;
        poseStack.mulPose(new Quaternionf().rotateAxis(angle * Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
        renderDynamicModel(CHAMBER, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay);
        poseStack.popPose();
    }

    private void renderDynamicModel(IGDynamicModel model, PoseStack matrix, MultiBufferSource buffer, Direction facing, Level level, BlockPos pos, int light, int overlay)
    {
        matrix.pushPose();
        List<BakedQuad> quads = model.get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null);
        rotateForFacing(matrix, facing);

        // TODO Confirm if we can use a hardcoded value.
        // Overlay only contains a few bits of info (0xA0000) so we need to format this into something that we can use
        // This calculation creates '0xA0A0A0' which is about right for the color we need.
        int overlayCol = ((overlay | (overlay >> 8) | (overlay >> 16)) << 4);
        RenderUtils.renderModelTESRFancy(quads, buffer.getBuffer(RenderType.cutoutMipped()), matrix, level, pos, false, 0xf0f0f0, light);
        matrix.popPose();
    }

}
