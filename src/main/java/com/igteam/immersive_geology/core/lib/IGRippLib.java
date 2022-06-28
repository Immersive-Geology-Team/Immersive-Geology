package com.igteam.immersive_geology.core.lib;

import blusunrize.immersiveengineering.client.ClientUtils;
import com.igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

//This class here holds a BUNCH of methods that IG relies on, incidentally, most are from other mods and or libraries, although most of it is modified a fair amount.
public class IGRippLib {

    //I suspect that this method is better than renderFluid2.
    public static void renderFluid(Fluid fluid, World world, BlockPos pos, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay, float scale, float height){
            final ResourceLocation texture = fluid.getAttributes().getStillTexture();
            final int[] color = unpackColor(fluid.getAttributes().getColor(world, pos));
            final TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(texture);
            renderBlockSprite(buffer.getBuffer(RenderType.getTranslucent()), matrix, sprite, light, overlay, color, scale, height);
    }

    public static void renderFluid2(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, FluidStack fluidStack, float alpha, float heightPercentage, int combinedLight){
        IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(RenderType.getTranslucent());
        TextureAtlasSprite sprite = ClientUtils.mc().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidStack.getFluid().getAttributes().getStillTexture(fluidStack));
        int color = fluidStack.getFluid().getAttributes().getColor(fluidStack);
        alpha *= (color >> 24 & 255) / 255f;
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;

        renderQuads(matrixStack.getLast().getMatrix(), vertexBuilder, sprite, red, green, blue, alpha, heightPercentage, combinedLight, 2.5f);
    }

    private static final float SIDE_MARGIN = (float) 0.01f, MIN_Y = 1 / 16f, MAX_Y = 1 - MIN_Y;

    private static void renderQuads(Matrix4f matrix, IVertexBuilder vertexBuilder, TextureAtlasSprite sprite, float r, float g, float b, float alpha, float heightPercentage, int light, float scale){
        float height = (MIN_Y + (MAX_Y - MIN_Y) * heightPercentage);
        float minU = sprite.getInterpolatedU(SIDE_MARGIN * 16), maxU = sprite.getInterpolatedU((1 - SIDE_MARGIN) * 16);
        float minV = sprite.getInterpolatedV(MIN_Y * 16), maxV = sprite.getInterpolatedV(height * 16);
        // min z
        vertexBuilder.pos(matrix, (SIDE_MARGIN) * scale, MIN_Y, SIDE_MARGIN).color(r, g, b, alpha).tex(minU, minV).lightmap(light).normal(0, 0, -1).endVertex();
        vertexBuilder.pos(matrix, (SIDE_MARGIN) * scale, height* scale, (SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(minU, maxV).lightmap(light).normal(0, 0, -1).endVertex();
        vertexBuilder.pos(matrix, (1 - SIDE_MARGIN) * scale, height* scale, (SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(maxU, maxV).lightmap(light).normal(0, 0, -1).endVertex();
        vertexBuilder.pos(matrix, (1 - SIDE_MARGIN) * scale, MIN_Y, (SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(maxU, minV).lightmap(light).normal(0, 0, -1).endVertex();
        // max z
        vertexBuilder.pos(matrix, (SIDE_MARGIN) * scale, MIN_Y, (1 - SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(minU, minV).lightmap(light).normal(0, 0, 1).endVertex();
        vertexBuilder.pos(matrix, (1 - SIDE_MARGIN) * scale, MIN_Y, (1 - SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(maxU, minV).lightmap(light).normal(0, 0, 1).endVertex();
        vertexBuilder.pos(matrix, (1 - SIDE_MARGIN) * scale, height* scale, (1 - SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(maxU, maxV).lightmap(light).normal(0, 0, 1).endVertex();
        vertexBuilder.pos(matrix, (SIDE_MARGIN) * scale, height* scale, (1 - SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(minU, maxV).lightmap(light).normal(0, 0, 1).endVertex();
        // min x
        vertexBuilder.pos(matrix, (SIDE_MARGIN) * scale, MIN_Y, (SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(minU, minV).lightmap(light).normal(-1, 0, 0).endVertex();
        vertexBuilder.pos(matrix, (SIDE_MARGIN) * scale, MIN_Y, (1 - SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(maxU, minV).lightmap(light).normal(-1, 0, 0).endVertex();
        vertexBuilder.pos(matrix, (SIDE_MARGIN) * scale, height* scale, (1 - SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(maxU, maxV).lightmap(light).normal(-1, 0, 0).endVertex();
        vertexBuilder.pos(matrix, (SIDE_MARGIN) * scale, height* scale, (SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(minU, maxV).lightmap(light).normal(-1, 0, 0).endVertex();
        // max x
        vertexBuilder.pos(matrix, (1 - SIDE_MARGIN) * scale, MIN_Y, (SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(minU, minV).lightmap(light).normal(1, 0, 0).endVertex();
        vertexBuilder.pos(matrix, (1 - SIDE_MARGIN) * scale, height* scale, (SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(minU, maxV).lightmap(light).normal(1, 0, 0).endVertex();
        vertexBuilder.pos(matrix, (1 - SIDE_MARGIN) * scale, height* scale, (1 - SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(maxU, maxV).lightmap(light).normal(1, 0, 0).endVertex();
        vertexBuilder.pos(matrix, (1 - SIDE_MARGIN) * scale, MIN_Y, (1 - SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(maxU, minV).lightmap(light).normal(1, 0, 0).endVertex();
        // top
        if(heightPercentage < 1){
            minV = sprite.getInterpolatedV(SIDE_MARGIN * 16);
            maxV = sprite.getInterpolatedV((1 - SIDE_MARGIN) * 16);
            vertexBuilder.pos(matrix, (SIDE_MARGIN) * scale, height * scale, (SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(minU, minV).lightmap(light).normal(0, 1, 0).endVertex();
            vertexBuilder.pos(matrix, (SIDE_MARGIN) * scale, height * scale, (1 - SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(minU, maxV).lightmap(light).normal(0, 1, 0).endVertex();
            vertexBuilder.pos(matrix, (1 - SIDE_MARGIN) * scale, height * scale, (1 - SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(maxU, maxV).lightmap(light).normal(0, 1, 0).endVertex();
            vertexBuilder.pos(matrix, (1 - SIDE_MARGIN) * scale, height * scale, (SIDE_MARGIN) * scale).color(r, g, b, alpha).tex(maxU, minV).lightmap(light).normal(0, 1, 0).endVertex();
        }
    }

    public static void renderBlockSprite (IVertexBuilder builder, MatrixStack stack, TextureAtlasSprite sprite, int light, int overlay, int[] color, float scale, float height) {
        renderBlockSprite(builder, stack.getLast().getMatrix(), sprite, light, overlay, 0f, 1f, 0f, 1f, 0f, 1f, color, scale, height);
    }

    public static void renderBlockSprite (IVertexBuilder builder, Matrix4f pos, TextureAtlasSprite sprite, int light, int overlay, float x1, float x2, float y1, float y2, float z1, float z2, int[] color, float scale, float height) {
        renderSpriteSide(builder, pos, sprite, Direction.DOWN, light, overlay, x1, x2, y1, y2, z1, z2, color, scale, height);
        renderSpriteSide(builder, pos, sprite, Direction.UP, light, overlay, x1, x2, y1, y2, z1, z2, color, scale, height);
        renderSpriteSide(builder, pos, sprite, Direction.NORTH, light, overlay, x1, x2, y1, y2, z1, z2, color, scale, height);
        renderSpriteSide(builder, pos, sprite, Direction.SOUTH, light, overlay, x1, x2, y1, y2, z1, z2, color, scale, height);
        renderSpriteSide(builder, pos, sprite, Direction.WEST, light, overlay, x1, x2, y1, y2, z1, z2, color, scale, height);
        renderSpriteSide(builder, pos, sprite, Direction.EAST, light, overlay, x1, x2, y1, y2, z1, z2, color, scale, height);
    }

    public static void renderSpriteSide (IVertexBuilder builder, Matrix4f pos, TextureAtlasSprite sprite, Direction side, int light, int overlay, float x1, float x2, float y1, float y2, float z1, float z2, int[] color, float scale, float height) {

        // Convert block size to pixel size
        final double px1 = x1 * 16;
        final double px2 = x2 * 16;
        final double py1 = y1 * 16;
        final double py2 = y2 * 16;
        final double pz1 = z1 * 16;
        final double pz2 = z2 * 16;

        if (side == Direction.DOWN) {
            final float u1 = sprite.getInterpolatedU(px1);
            final float u2 = sprite.getInterpolatedU(px2);
            final float v1 = sprite.getInterpolatedV(pz1);
            final float v2 = sprite.getInterpolatedV(pz2);
            builder.pos(pos, x1 * scale, (y1 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v2).overlay(overlay).lightmap(light).normal(0f, -1f, 0f).endVertex();
            builder.pos(pos, x1 * scale, (y1 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v1).overlay(overlay).lightmap(light).normal(0f, -1f, 0f).endVertex();
            builder.pos(pos, x2 * scale, (y1 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v1).overlay(overlay).lightmap(light).normal(0f, -1f, 0f).endVertex();
            builder.pos(pos, x2 * scale, (y1 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v2).overlay(overlay).lightmap(light).normal(0f, -1f, 0f).endVertex();
        }

        if (side == Direction.UP) {
            final float u1 = sprite.getInterpolatedU(px1);
            final float u2 = sprite.getInterpolatedU(px2);
            final float v1 = sprite.getInterpolatedV(pz1);
            final float v2 = sprite.getInterpolatedV(pz2);
            builder.pos(pos, x1 * scale, (y2 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v2).overlay(overlay).lightmap(light).normal(0f, 1f, 0f).endVertex();
            builder.pos(pos, x2 * scale, (y2 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v2).overlay(overlay).lightmap(light).normal(0f, 1f, 0f).endVertex();
            builder.pos(pos, x2 * scale, (y2 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v1).overlay(overlay).lightmap(light).normal(0f, 1f, 0f).endVertex();
            builder.pos(pos, x1 * scale, (y2 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v1).overlay(overlay).lightmap(light).normal(0f, 1f, 0f).endVertex();
        }

        if (side == Direction.NORTH) {
            final float u1 = sprite.getInterpolatedU(px1);
            final float u2 = sprite.getInterpolatedU(px2);
            final float v1 = sprite.getInterpolatedV(py1);
            final float v2 = sprite.getInterpolatedV(py2);
            builder.pos(pos, x1 * scale, (y1 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v1).overlay(overlay).lightmap(light).normal(0f, 0f, -1f).endVertex();
            builder.pos(pos, x1 * scale, (y2 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v2).overlay(overlay).lightmap(light).normal(0f, 0f, -1f).endVertex();
            builder.pos(pos, x2 * scale, (y2 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v2).overlay(overlay).lightmap(light).normal(0f, 0f, -1f).endVertex();
            builder.pos(pos, x2 * scale, (y1 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v1).overlay(overlay).lightmap(light).normal(0f, 0f, -1f).endVertex();
        }

        if (side == Direction.SOUTH) {
            final float u1 = sprite.getInterpolatedU(px1);
            final float u2 = sprite.getInterpolatedU(px2);
            final float v1 = sprite.getInterpolatedV(py1);
            final float v2 = sprite.getInterpolatedV(py2);
            builder.pos(pos, x2 * scale, (y1 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v1).overlay(overlay).lightmap(light).normal(0f, 0f, 1f).endVertex();
            builder.pos(pos, x2 * scale, (y2 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v2).overlay(overlay).lightmap(light).normal(0f, 0f, 1f).endVertex();
            builder.pos(pos, x1 * scale, (y2 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v2).overlay(overlay).lightmap(light).normal(0f, 0f, 1f).endVertex();
            builder.pos(pos, x1 * scale, (y1 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v1).overlay(overlay).lightmap(light).normal(0f, 0f, 1f).endVertex();
        }

        if (side == Direction.WEST) {
            final float u1 = sprite.getInterpolatedU(py1);
            final float u2 = sprite.getInterpolatedU(py2);
            final float v1 = sprite.getInterpolatedV(pz1);
            final float v2 = sprite.getInterpolatedV(pz2);
            builder.pos(pos, x1 * scale, (y1 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v2).overlay(overlay).lightmap(light).normal(-1f, 0f, 0f).endVertex();
            builder.pos(pos, x1 * scale, (y2 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v2).overlay(overlay).lightmap(light).normal(-1f, 0f, 0f).endVertex();
            builder.pos(pos, x1 * scale, (y2 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v1).overlay(overlay).lightmap(light).normal(-1f, 0f, 0f).endVertex();
            builder.pos(pos, x1 * scale, (y1 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v1).overlay(overlay).lightmap(light).normal(-1f, 0f, 0f).endVertex();
        }

        if (side == Direction.EAST) {
            final float u1 = sprite.getInterpolatedU(py1);
            final float u2 = sprite.getInterpolatedU(py2);
            final float v1 = sprite.getInterpolatedV(pz1);
            final float v2 = sprite.getInterpolatedV(pz2);
            builder.pos(pos, x2 * scale, (y1 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v1).overlay(overlay).lightmap(light).normal(1f, 0f, 0f).endVertex();
            builder.pos(pos, x2 * scale, (y2 * scale) * height, z1 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v1).overlay(overlay).lightmap(light).normal(1f, 0f, 0f).endVertex();
            builder.pos(pos, x2 * scale, (y2 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u2, v2).overlay(overlay).lightmap(light).normal(1f, 0f, 0f).endVertex();
            builder.pos(pos, x2 * scale, (y1 * scale) * height, z2 * scale).color(color[1], color[2], color[3], color[0]).tex(u1, v2).overlay(overlay).lightmap(light).normal(1f, 0f, 0f).endVertex();
        }
    }

    public static int[] unpackColor (int color) {

        final int[] colors = new int[4];
        colors[0] = color >> 24 & 0xff; // alpha
        colors[1] = color >> 16 & 0xff; // red
        colors[2] = color >> 8 & 0xff; // green
        colors[3] = color & 0xff; // blue
        return colors;
    }

    public static float lerp(float point1, float point2, float alpha){
        return point1 + alpha * (point2 - point1);
    }
}
