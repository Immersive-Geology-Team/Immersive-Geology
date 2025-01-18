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
import com.igteam.immersivegeology.common.block.multiblocks.logic.PelletizerLogic;
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

public class PelletizerRenderer extends IGBlockEntityRenderer<MultiblockBlockEntityMaster<PelletizerLogic.State>>
{
    public static final String DISH_NAME = "pelletizer_dish";
    public static final String FUNNEL_NAME = "pelletizer_funnel";

    public static IGDynamicModel DISH, FUNNEL;

    @Override
    public void render(MultiblockBlockEntityMaster<PelletizerLogic.State> tile, float pPartialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay)
    {
        IMultiblockBEHelperMaster<PelletizerLogic.State> helper = tile.getHelper();
        IMultiblockContext<PelletizerLogic.State> context = helper.getContext();

        final MultiblockOrientation orientation = context.getLevel().getOrientation();
        final PelletizerLogic.State state = context.getState();
        float rot = state.getRotation();
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        Direction dir = orientation.front();
        boolean isActive = state.shouldRenderActive();
        poseStack.pushPose();
            rotateForFacing(poseStack, dir);
            poseStack.pushPose();
                poseStack.translate(-0.5,1.90625,0.21875);
                float angleDrum = isActive ? (rot) - pPartialTick : rot;
                poseStack.mulPose(new Quaternionf().rotateAxis(30 * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)));
                poseStack.pushPose();
                    poseStack.mulPose(new Quaternionf().rotateAxis(angleDrum * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)));
                    renderDynamicModel(DISH, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay);
                poseStack.popPose();
            poseStack.popPose();
        poseStack.popPose();
    }

    private void renderDynamicModel(IGDynamicModel model, PoseStack matrix, MultiBufferSource buffer, Direction facing, Level level, BlockPos pos, int light, int overlay)
    {
        matrix.pushPose();
        List<BakedQuad> quads = model.get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null);
        rotateForFacing(matrix, facing);

        RenderUtils.renderModelTESRFancy(quads, buffer.getBuffer(RenderType.cutoutMipped()), matrix, level, pos, false, 0xf0f0f0, light);
        matrix.popPose();
    }

}
