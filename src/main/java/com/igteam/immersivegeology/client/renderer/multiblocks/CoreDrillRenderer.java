package com.igteam.immersivegeology.client.renderer.multiblocks;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.client.renderer.IGBlockEntityRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic.State;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class CoreDrillRenderer extends IGBlockEntityRenderer<MultiblockBlockEntityMaster<CoreDrillLogic.State>>
{
    public static final String DRILL_BIT_NAME = "coredrill_bit";
    public static final String DRILL_ENGINE_NAME = "coredrill_engine";
    public static final String DRILL_ENGINE_SUPPORT_NAME = "coredrill_vagons";
    public static final String DRILL_GEARSET_NAME = "coredrill_gears";

    public static IGDynamicModel DRILL_BIT;
    public static IGDynamicModel DRILL_ENGINE;
    public static IGDynamicModel DRILL_ENGINE_SUPPORT;
    public static IGDynamicModel DRILL_GEARSET;

    @Override
    public void render(MultiblockBlockEntityMaster<CoreDrillLogic.State> tile, float pPartialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay)
    {
        final IMultiblockBEHelperMaster<CoreDrillLogic.State> helper = tile.getHelper();
        final IMultiblockContext<CoreDrillLogic.State> context = helper.getContext();
        final MultiblockOrientation orientation = context.getLevel().getOrientation();
        final State state = context.getState();
        float drill_height = state.getDrillHeight();
        float gear_angle = state.getGearClockwiseAngle();
        float counter_gear_angle = state.getGearCounterClockwiseAngle();
        Direction dir = orientation.front();

        poseStack.pushPose();
            boolean active = state.shouldRenderActive();
            float angle = state.getDrillAngle() + (active?18*pPartialTick : 0);
            poseStack.translate(-2.5, -1.9375 + drill_height, -2.5);
            poseStack.mulPose(new Quaternionf().rotateAxis(-angle * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)));
            renderDynamicModel(DRILL_BIT, poseStack, buffer, dir, pPackedLight, pPackedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
            poseStack.translate(-4.5, -7.875 + drill_height, -4.5);
            renderDynamicModel(DRILL_ENGINE, poseStack, buffer, dir, pPackedLight, pPackedOverlay);

            poseStack.pushPose();
                poseStack.mulPose(new Quaternionf().rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)));
                poseStack.translate(-4, 1, 0);
                renderDynamicModel(DRILL_ENGINE_SUPPORT, poseStack, buffer, dir, pPackedLight, pPackedOverlay);
                poseStack.translate(-2.125, 6.2, 2);
                poseStack.pushPose();
                    poseStack.pushPose();
                        poseStack.mulPose(new Quaternionf().rotateAxis(-gear_angle * Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
                        renderDynamicModel(DRILL_GEARSET, poseStack, buffer, dir, pPackedLight, pPackedOverlay);
                    poseStack.popPose();

                    poseStack.translate(2.25, 0, 0);
                    poseStack.pushPose();
                        poseStack.mulPose(new Quaternionf().rotateAxis(-counter_gear_angle * Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
                        renderDynamicModel(DRILL_GEARSET, poseStack, buffer, dir, pPackedLight, pPackedOverlay);
                    poseStack.popPose();

                    poseStack.translate(3.75, 0, 0);
                    poseStack.pushPose();
                        poseStack.mulPose(new Quaternionf().rotateAxis(-gear_angle * Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
                        renderDynamicModel(DRILL_GEARSET, poseStack, buffer, dir, pPackedLight, pPackedOverlay);
                    poseStack.popPose();

                    poseStack.translate(2.25, 0, 0);
                    poseStack.pushPose();
                        poseStack.mulPose(new Quaternionf().rotateAxis(-counter_gear_angle * Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
                        renderDynamicModel(DRILL_GEARSET, poseStack, buffer, dir, pPackedLight, pPackedOverlay);
                    poseStack.popPose();
                poseStack.popPose();
            poseStack.popPose();
        poseStack.popPose();
    }

    private void renderDynamicModel(IGDynamicModel model, PoseStack matrix, MultiBufferSource buffer, Direction facing, int light, int overlay)
    {

        matrix.pushPose();
        List<BakedQuad> quads = model.get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null);
        rotateForFacing(matrix, facing);
        RenderUtils.renderModelTESRFast(quads, buffer.getBuffer(RenderType.cutoutMipped()), matrix, light, overlay);
        matrix.popPose();
    }
}
