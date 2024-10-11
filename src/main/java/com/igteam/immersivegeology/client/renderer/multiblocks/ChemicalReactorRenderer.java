/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.client.BlockOverlayUtils;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import com.igteam.immersivegeology.client.renderer.IGBlockEntityRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic.ChemicalReactorTanks;
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic.State;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import java.util.Iterator;
import java.util.List;

public class ChemicalReactorRenderer extends IGBlockEntityRenderer<MultiblockBlockEntityMaster<ChemicalReactorLogic.State>>
{

    public ChemicalReactorRenderer(){};

    @Override
    public void render(MultiblockBlockEntityMaster<ChemicalReactorLogic.State> master, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1)
    {
        IMultiblockBEHelperMaster<State> helper = master.getHelper();
        ChemicalReactorLogic.State state = helper.getState();
        ChemicalReactorTanks tanks = state.tanks;
        int tank_capacity = tanks.getCapacity();
        FluidStack leftTank = tanks.leftInput().getFluid();
        FluidStack rightTank = tanks.rightInput().getFluid();
        FluidStack backTank = tanks.backInput().getFluid();
        FluidStack outputTank = tanks.output().getFluid();

        MultiblockOrientation rotation = helper.getContext().getLevel().getOrientation();
        boolean mirrored = rotation.mirrored();
        rotateForFacing(poseStack, rotation.front());

        if(!leftTank.isEmpty())
        {
            BlockPos tankPos = tanks.getLeftTankPos(mirrored);
            float fillPercent = (float) leftTank.getAmount()/ tank_capacity;
            renderFluidInTank(master.getLevel(), tankPos, leftTank, poseStack, multiBufferSource, fillPercent);
        }

        if(!rightTank.isEmpty())
        {
            BlockPos tankPos = tanks.getRightTankPos(mirrored);
            float fillPercent = (float) rightTank.getAmount() / tank_capacity;
            renderFluidInTank(master.getLevel(), tankPos, rightTank, poseStack, multiBufferSource, fillPercent);
        }

        if(!backTank.isEmpty())
        {
            BlockPos tankPos = tanks.getBackTankPos(mirrored);
            float fillPercent = (float) backTank.getAmount() / tank_capacity;
            renderFluidInTank(master.getLevel(), tankPos, backTank, poseStack, multiBufferSource, fillPercent);
        }

        if(!outputTank.isEmpty())
        {
            BlockPos tankPos = tanks.getOutputTankPos(mirrored);
            float fillPercent = (float) outputTank.getAmount() / tank_capacity;
            renderFluidInTank(master.getLevel(), tankPos, outputTank, poseStack, multiBufferSource, fillPercent);
        }
    }



    // Code is Fluid Render Code is sourced from ITank by EwyBoy
    private void renderFluidInTank(BlockAndTintGetter world, BlockPos pos, FluidStack fluidStack, PoseStack matrix, MultiBufferSource buffer, float percent) {
        matrix.pushPose();
        matrix.translate(0.5d, 0.5d, 0.5d);
        // This offest ensures the tank render is aligned to the pixels of the multiblock.
        float offset = 0.15625f;
        matrix.translate(pos.getX() + offset, pos.getY(), pos.getZ() - offset);
        Matrix4f matrix4f = matrix.last().pose();
        Matrix3f matrix3f = matrix.last().normal();

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions fluidAttributes = IClientFluidTypeExtensions.of(fluid);
        TextureAtlasSprite fluidTexture = getFluidStillSprite(fluidAttributes, fluidStack);

        int color = fluidAttributes.getTintColor(fluidStack);

        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
        float faceoffset = 0.6875f;
        for(int i = 0; i < 2; i++)
        {
            matrix.translate(0,i * percent,0);
            renderFluidSides(faceoffset, fluidTexture, matrix, matrix4f, matrix3f, builder, color, percent);
        }

        // Top
        if (percent != 1.0f){
            this.renderTopFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
            matrix.translate(faceoffset,0,0);
            this.renderTopFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);

            matrix.translate(0,0,-faceoffset);
            this.renderTopFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
            matrix.translate(-faceoffset,0,0);
            this.renderTopFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
        }




        matrix.popPose();
    }

    private void renderFluidSides(float faceoffset, TextureAtlasSprite fluidTexture, PoseStack matrix, Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer builder, int color, float percent)
    {
        //f1
        // Move the water pane to the far side of the tank.
        matrix.translate(0,0,-faceoffset);
        this.renderNorthFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
        // Render another pane for the neighbor.
        matrix.translate(faceoffset, 0,0);
        this.renderNorthFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
        matrix.translate(-faceoffset, 0,0);
        matrix.translate(0,0,faceoffset);

        // Rotate our working space
        matrix.mulPose(Axis.YP.rotationDegrees(90));

        //f2
        // Already in good position, so we don't need to worry about location for this one.
        this.renderNorthFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
        // We need to extend water display to cover other block.
        matrix.translate(faceoffset,0,0);
        this.renderNorthFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
        // Then we need to ensure we return to original translation value.
        matrix.translate(-faceoffset,0,0);

        // Rotate our working space
        matrix.mulPose(Axis.YP.rotationDegrees(90));

        //f3
        // We are in a good position.
        this.renderNorthFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
        // we need to cover the other block, so move the render location
        matrix.translate(-faceoffset,0,0);
        // render the face
        this.renderNorthFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
        // move back.
        matrix.translate(faceoffset,0,0);

        // Rotate our working space
        matrix.mulPose(Axis.YP.rotationDegrees(90));

        //f4
        // We are in a bad position, we need to move to the correct location first
        matrix.translate(0,0,-faceoffset);

        this.renderNorthFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);

        // Now we need to cover neighboring block.
        matrix.translate(-faceoffset,0,0);
        this.renderNorthFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
        matrix.translate(faceoffset,0,0);

        matrix.translate(0,0,faceoffset);
        matrix.mulPose(Axis.YP.rotationDegrees(90));
    }

    private void renderTopFluidFace(TextureAtlasSprite sprite, Matrix4f matrix4f, Matrix3f normalMatrix, VertexConsumer builder, int color, float percent) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        float width = 11 / 16f;
        float height = 16 / 16f;

        float minU = sprite.getU(3);
        float maxU = sprite.getU(13);
        float minV = sprite.getV(3);
        float maxV = sprite.getV(13);

        builder.vertex(matrix4f, -width / 2, -height / 2 + percent * height, -width / 2).color(r, g, b, a)
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, -width / 2, -height / 2 + percent * height, width / 2).color(r, g, b, a)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, width / 2, -height / 2 + percent * height, width / 2).color(r, g, b, a)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, width / 2, -height / 2 + percent * height, -width / 2).color(r, g, b, a)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();
    }

    private void renderNorthFluidFace(TextureAtlasSprite sprite, Matrix4f matrix4f, Matrix3f normalMatrix, VertexConsumer builder, int color, float percent) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        float width = 11 / 16f;
        float height = 16 / 16f;

        float minU = sprite.getU(3);
        float maxU = sprite.getU(13);
        float minV = sprite.getV(1);
        float maxV = sprite.getV(15 * percent);

        builder.vertex(matrix4f, -width / 2, -height / 2 + height * percent, (-width / 2) + 0.001f).color(r, g, b, a)
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.vertex(matrix4f, width / 2, -height / 2 + height * percent, (-width / 2) + 0.001f).color(r, g, b, a)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.vertex(matrix4f, width / 2, -height / 2, (-width / 2) + 0.001f).color(r, g, b, a)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.vertex(matrix4f, -width / 2, -height / 2, (-width / 2) + 0.001f).color(r, g, b, a)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();
    }

    private TextureAtlasSprite getFluidStillSprite(IClientFluidTypeExtensions properties, FluidStack fluidStack) {
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(properties.getStillTexture(fluidStack));
    }

    private TextureAtlasSprite getFluidFlowingSprite(IClientFluidTypeExtensions properties, FluidStack fluidStack) {
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(properties.getFlowingTexture(fluidStack));
    }
}
