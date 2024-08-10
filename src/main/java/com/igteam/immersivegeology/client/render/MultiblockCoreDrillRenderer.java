package com.igteam.immersivegeology.client.render;

import com.igteam.immersivegeology.common.block.multiblock.coredrill.CoreDrillTileEntity;
import com.igteam.immersivegeology.common.block.multiblock.gravityseparator.GravitySeparatorTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;

import javax.annotation.Nonnull;

public class MultiblockCoreDrillRenderer implements BlockEntityRenderer<CoreDrillTileEntity> {

    @Override
    public boolean shouldRenderOffScreen(@Nonnull CoreDrillTileEntity te){
        return true;
    }
    @Override
    public void render(CoreDrillTileEntity te, float partialTicks, PoseStack transform, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        if(te.formed && !te.isDummy()) {
            if (te.shouldRenderAsActive()) {
                transform.pushPose();
                {
                    Direction rotation = te.getFacing();
                    switch (rotation) {
                        case NORTH -> {
                            transform.translate(0, 0, 0);
                        }
                        case SOUTH -> {
                            transform.mulPose(new Quaternion(0F, 180F, 0F, true));
                            transform.translate(0, 0, 0);
                        }
                        case EAST -> {
                            transform.mulPose(new Quaternion(0, 270F, 0, true));
                            transform.translate(0, 0, 0);
                        }
                        case WEST -> {
                            transform.mulPose(new Quaternion(0, 90F, 0, true));
                            transform.translate(0 ,0, 0);
                        }
                        default -> {
                        }
                    }
                }
                transform.popPose();
            }
        }
    }
}
