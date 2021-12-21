package com.igteam.immersive_geology.client.render;

import com.igteam.immersive_geology.common.block.tileentity.BloomeryTileEntity;
import com.igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class BloomeryRenderer extends TileEntityRenderer<BloomeryTileEntity> {
    public BloomeryRenderer(TileEntityRendererDispatcher dispatcher){
        super(dispatcher);
    }

    @Override
    public boolean isGlobalRenderer(BloomeryTileEntity te) {
        return true;
    }

    //Ripped from IP's Pumpjack
    @Override
    public void render(BloomeryTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if(!te.isDummy()){
            if(te.isBurning()){
                IVertexBuilder buf = buffer.getBuffer(IGRenderTypes.BLOOMERY_ACTIVE).getVertexBuilder();
                transform.push();
                Matrix4f mat = transform.getLast().getMatrix();
                int ux = 32, vy = 32;
                int w = 32, h = 32;
                float br = 0.75f;

                float uw = w/32f, vh = h/32f, u0 = ux/32f, v0 = vy / 32f, u1 = u0 + uw, v1 = v0 + vh;

                buf.pos(mat, 0,0.5f, w/16f).color(br,br,br, 1.0f).tex(u1, v1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(1,1,1).endVertex();
                buf.pos(mat, 0,0.5f + h / 16f, w/16f).color(br,br,br, 1.0f).tex(u1, v1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(1,1,1).endVertex();
                buf.pos(mat, 0,0.5f + h / 16f, 0f).color(br,br,br, 1.0f).tex(u1, v1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(1,1,1).endVertex();
                buf.pos(mat, 0,0.5f, w/0f).color(br,br,br, 1.0f).tex(u1, v1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(1,1,1).endVertex();

                transform.pop();
            }
        }
    }
}
