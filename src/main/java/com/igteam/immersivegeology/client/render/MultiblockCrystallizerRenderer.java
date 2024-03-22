package com.igteam.immersivegeology.client.render;

import com.igteam.immersivegeology.common.block.multiblock.crystallizer.CrystallizerTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;

import javax.annotation.Nonnull;

public class MultiblockCrystallizerRenderer implements BlockEntityRenderer<CrystallizerTileEntity> {

    @Override
    public boolean shouldRenderOffScreen(@Nonnull CrystallizerTileEntity te){
        return true;
    }
    @Override
    public void render(CrystallizerTileEntity te, float partialTicks, PoseStack transform, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        if(te.formed && !te.isDummy()) {
            if (te.shouldRenderAsActive()) {
                transform.pushPose();
                {
                    Direction rotation = te.getFacing();
                    switch (rotation) {
                        case NORTH -> {
                            transform.translate(3, 0, 4);
                        }
                        case SOUTH -> {
                            transform.mulPose(new Quaternion(0F, 180F, 0F, true));
                            transform.translate(2, 0, 3);
                        }
                        case EAST -> {
                            transform.mulPose(new Quaternion(0, 270F, 0, true));
                            transform.translate(3, 0, 3);
                        }
                        case WEST -> {
                            transform.mulPose(new Quaternion(0, 90F, 0, true));
                            transform.translate(2, 0, 4);
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
