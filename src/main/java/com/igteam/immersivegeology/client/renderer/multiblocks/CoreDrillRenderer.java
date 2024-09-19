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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;

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
        boolean active = state.shouldRenderActive();

        boolean isMirrored = orientation.mirrored();

        // magic numbers quarter pixel and half pixel respectively
        float drill_height = state.getDrillHeight() + (active ? ((0.015051525f * pPartialTick) * (state.getDrillDirection() ? 1 : -1)) : 0);
        float gear_angle = state.getGearClockwiseAngle() + ((0.03125f * pPartialTick) * (state.getDrillDirection() ? -1 : 1));
        float counter_gear_angle = state.getGearCounterClockwiseAngle() + ((0.03125f * pPartialTick) * (state.getDrillDirection() ? 1 : -1));
        float shake = state.getDrillShake();
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        Direction dir = orientation.front();

        boolean flipSupportAngle = dir.equals(Direction.EAST) || dir.equals(Direction.WEST);

        if(isMirrored) poseStack.translate(-MIRRORED_OFFSETS.get(dir).x(), MIRRORED_OFFSETS.get(dir).y(), -MIRRORED_OFFSETS.get(dir).z());
        poseStack.pushPose();
            poseStack.translate(DRILL_BIT_OFFSETS.get(dir).x(), DRILL_BIT_OFFSETS.get(dir).y() + drill_height, DRILL_BIT_OFFSETS.get(dir).z());
            poseStack.pushPose();
                float angle = state.getDrillAngle() + (state.getDrillSpeed()*pPartialTick);
                poseStack.mulPose(new Quaternionf().rotateAxis(angle * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)));
                renderDynamicModel(DRILL_BIT, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay);
            poseStack.popPose();
        poseStack.popPose();

        poseStack.pushPose();
            poseStack.translate(DRILL_ENGINE_OFFSETS.get(dir).x() + shake, DRILL_ENGINE_OFFSETS.get(dir).y() + drill_height, DRILL_ENGINE_OFFSETS.get(dir).z() + shake);
            renderDynamicModel(DRILL_ENGINE, poseStack, buffer, dir, level, pos, pPackedLight, pPackedOverlay);

            poseStack.pushPose();
                poseStack.mulPose(new Quaternionf().rotateAxis((flipSupportAngle ? -90 : 90) * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)));
                poseStack.translate(DRILL_SUPPORT_OFFSETS.get(dir).x(), DRILL_SUPPORT_OFFSETS.get(dir).y(), DRILL_SUPPORT_OFFSETS.get(dir).z());
                renderDynamicModel(DRILL_ENGINE_SUPPORT, poseStack, buffer, dir, level, pos, pPackedLight, pPackedOverlay);
                poseStack.translate(DRILL_GEAR_OFFSETS.get(dir).get(0).x(), DRILL_GEAR_OFFSETS.get(dir).get(0).y(), DRILL_GEAR_OFFSETS.get(dir).get(0).z());

                poseStack.mulPose(new Quaternionf().rotateAxis((flipSupportAngle ? 90 : 0) * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)));
                poseStack.pushPose();
                    poseStack.pushPose();
                        poseStack.mulPose(new Quaternionf().rotateAxis((gear_angle * (flipSupportAngle ? 1 : -1)) * Mth.DEG_TO_RAD, DRILL_GEAR_ROTATION_OFFSETS.get(dir)));
                        renderDynamicModel(DRILL_GEARSET, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay);
                    poseStack.popPose();

                    poseStack.translate(DRILL_GEAR_OFFSETS.get(dir).get(1).x(), DRILL_GEAR_OFFSETS.get(dir).get(1).y(), DRILL_GEAR_OFFSETS.get(dir).get(1).z());
                    poseStack.pushPose();
                        poseStack.mulPose(new Quaternionf().rotateAxis((counter_gear_angle * (flipSupportAngle ? 1 : -1)) * Mth.DEG_TO_RAD, DRILL_GEAR_ROTATION_OFFSETS.get(dir)));
                        renderDynamicModel(DRILL_GEARSET, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay);
                    poseStack.popPose();

                    poseStack.translate(DRILL_GEAR_OFFSETS.get(dir).get(2).x(), DRILL_GEAR_OFFSETS.get(dir).get(2).y(), DRILL_GEAR_OFFSETS.get(dir).get(2).z());
                    poseStack.pushPose();
                        poseStack.mulPose(new Quaternionf().rotateAxis((gear_angle * (flipSupportAngle ? 1 : -1)) * Mth.DEG_TO_RAD, DRILL_GEAR_ROTATION_OFFSETS.get(dir)));
                        renderDynamicModel(DRILL_GEARSET, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay);
                    poseStack.popPose();

                    poseStack.translate(DRILL_GEAR_OFFSETS.get(dir).get(3).x(), DRILL_GEAR_OFFSETS.get(dir).get(3).y(), DRILL_GEAR_OFFSETS.get(dir).get(3).z());
                    poseStack.pushPose();
                        poseStack.mulPose(new Quaternionf().rotateAxis((counter_gear_angle * (flipSupportAngle ? 1 : -1)) * Mth.DEG_TO_RAD, DRILL_GEAR_ROTATION_OFFSETS.get(dir)));
                        renderDynamicModel(DRILL_GEARSET, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay);
                    poseStack.popPose();
                poseStack.popPose();
            poseStack.popPose();
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

    // Static Directional Offset Entries
    public static final Map<Direction, Vector3fc> MIRRORED_OFFSETS = Map.of(
            Direction.NORTH, new Vector3f(-6,0,0),
            Direction.EAST, new Vector3f(0,0,-6),
            Direction.SOUTH, new Vector3f(6,0,0),
            Direction.WEST, new Vector3f(0,0,6)
    );

    public static final Map<Direction, Vec3> DRILL_BIT_OFFSETS = Map.of(
            Direction.NORTH, new Vec3(-2.5,-1.9375,-2.5),
            Direction.EAST, new Vec3(3.5,-1.9375,-2.5),
            Direction.SOUTH, new Vec3(3.5,-1.9375,3.5),
            Direction.WEST, new Vec3(-2.5,-1.9375,3.5)
    );

    public static final Map<Direction, Vec3> DRILL_ENGINE_OFFSETS = Map.of(
            Direction.NORTH, new Vec3(-4.5,-7.875,-4.5),
            Direction.EAST, new Vec3(4.5,-7.875,-4.5),
            Direction.SOUTH, new Vec3(4.5,-7.875,4.5),
            Direction.WEST, new Vec3(-4.5,-7.875,4.5)
    );

    public static final Map<Direction, Vec3> DRILL_SUPPORT_OFFSETS = Map.of(
            Direction.NORTH, new Vec3(-4, 1, 0),
            Direction.EAST, new Vec3(3, 1, -1),
            Direction.SOUTH, new Vec3(2, 1, 0),
            Direction.WEST, new Vec3(-3, 1, -1)
    );

    public static final Map<Direction, Vector3fc> DRILL_GEAR_ROTATION_OFFSETS = Map.of(
            Direction.NORTH, new Vector3f(0, 0, 1),
            Direction.EAST, new Vector3f(0, 0, -1),
            Direction.SOUTH, new Vector3f(0, 0, -1),
            Direction.WEST, new Vector3f(0, 0, -1)
    );

    public static final Map<Direction, Map<Integer, Vec3>> DRILL_GEAR_OFFSETS = Map.of(
            Direction.NORTH, Map.of(
                    0, new Vec3(-2.125, 6.2, 2),
                    1, new Vec3(2.25, 0, 0),
                    2, new Vec3(3.75, 0, 0),
                    3, new Vec3(2.25, 0, 0)),

            Direction.EAST, Map.of(
                    0, new Vec3(-1, 6.2, 6.125),
                    1, new Vec3(2.25, 0, 0),
                    2, new Vec3(3.75, 0, 0),
                    3, new Vec3(2.25, 0, 0)),

            Direction.SOUTH, Map.of(
                    0, new Vec3(3.125, 6.2, -1),
                    1, new Vec3(-2.25, 0, 0),
                    2, new Vec3(-3.75, 0, 0),
                    3, new Vec3(-2.25, 0, 0)),

            Direction.WEST, Map.of(
                    0, new Vec3(2, 6.2, 3.125),
                    1, new Vec3(2.25, 0, 0),
                    2, new Vec3(3.75, 0, 0),
                    3, new Vec3(2.25, 0, 0))
    );
}
