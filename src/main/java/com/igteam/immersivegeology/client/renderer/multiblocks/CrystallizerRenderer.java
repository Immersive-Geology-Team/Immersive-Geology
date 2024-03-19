package com.igteam.immersivegeology.client.renderer.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.client.render.tile.IEMultiblockRenderer;
import com.igteam.immersivegeology.common.blocks.multiblocks.logic.CrystallizerLogic;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class CrystallizerRenderer extends IEMultiblockRenderer<CrystallizerLogic.State> {
    @Override
    public void render(@NotNull IMultiblockContext<CrystallizerLogic.State> context, float v, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int i, int i1) {
        boolean isValid = context.isValid().getAsBoolean();
        if(isValid){
            BlockPos origin = context.getLevel().getAbsoluteOrigin();
            Direction facing = context.getLevel().getOrientation().front();

            poseStack.pushPose();
            switch (facing){
                case NORTH: {

                }
                case SOUTH: {
                    poseStack.rotateAround(new Quaternionf(0F, 180F, 0F, 0f), origin.getX(), origin.getY(), origin.getZ());
                }
                case EAST: {
                    poseStack.rotateAround(new Quaternionf(0F, 270F, 0F, 0f), origin.getX(), origin.getY(), origin.getZ());
                }
                case WEST: {
                    poseStack.rotateAround(new Quaternionf(0F, 90F, 0F, 0f), origin.getX(), origin.getY(), origin.getZ());
                }
                default:
                    break;
            }
            poseStack.popPose();
        }

    }
}
