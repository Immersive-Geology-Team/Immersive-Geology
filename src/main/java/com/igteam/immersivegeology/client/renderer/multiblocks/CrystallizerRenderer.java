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

        boolean isValid = context.isValid().getAsBoolean();
        if(isValid){
            BlockPos origin = context.getLevel().getAbsoluteOrigin();
            Direction facing = context.getLevel().getOrientation().front();
            poseStack.translate(0, -8, 0);
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
