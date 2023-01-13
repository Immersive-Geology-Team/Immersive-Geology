package igteam.immersive_geology.client.render.tileentity;

import igteam.immersive_geology.client.render.IGRenderTypes;
import igteam.immersive_geology.common.block.tileentity.BloomeryTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BloomeryRenderer extends TileEntityRenderer<BloomeryTileEntity> {
    public BloomeryRenderer(TileEntityRendererDispatcher dispatcher){
        super(dispatcher);
    }

    @Override
    public boolean isGlobalRenderer(BloomeryTileEntity te) {
        return true;
    }

    @Override
    public void render(BloomeryTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if(!te.isDummy()){
            if(te.isBurning()){

                combinedOverlayIn = OverlayTexture.NO_OVERLAY;
                transform.push();
                {
                    Direction rotation = te.getFacing();
                    switch (rotation){
                        case NORTH:
                            transform.translate(1,0,0);
                            transform.rotate(new Quaternion(0f, 270F, 0F, true));
                            break;
                        case EAST:
                            transform.rotate(new Quaternion(0f, 180F, 0F, true));
                            transform.translate(-1,0,-1);
                            break;
                        case WEST:
                            break;
                        case SOUTH:
                            transform.rotate(new Quaternion(0f, 90F, 0F, true));
                            transform.translate(-1,0,0);
                            break;
                    }

                    renderBloomeryActiveFront(te, transform, buffer, combinedOverlayIn);
                    renderBloomeryActiveTop(te, transform, buffer, combinedOverlayIn);
                }
                transform.pop();
            }
        }
    }


    private void renderBloomeryActiveFront(BloomeryTileEntity te, MatrixStack transform, IRenderTypeBuffer buffer, int combinedOverlayIn){
        IVertexBuilder buf = buffer.getBuffer(IGRenderTypes.BLOOMERY_ACTIVE);
        Direction rotation = te.getFacing();
        transform.push();
        Matrix4f mat = transform.getLast().getMatrix();

        int ux = 16, vy = 0;
        int w = 16, h = 16;
        float br = 1;
        float uw = w/48f, vh = h/48f, u0 = ux/48f, v0 = vy / 48f, u1 = u0 + uw, v1 = v0 + vh;
        World world = te.getWorld();

        assert world != null;
        int scuffedLight = 256 * ((1 + world.getLight(te.getPos().offset(rotation, 1))) / 16); //No idea why, but the normal combinedLightIn is just broken, so... We made our own.

        buf.pos(mat, 1.0015f,0f + h/16f, 0.0f).color(br,br,br,br).tex(u1, v0).overlay(combinedOverlayIn).lightmap(scuffedLight).normal(1,1,1).endVertex();
        buf.pos(mat, 1.0015f,0f + h/16f, w/16f).color(br,br,br,br).tex(u0, v0).overlay(combinedOverlayIn).lightmap(scuffedLight).normal(1,1,1).endVertex();
        buf.pos(mat, 1.0015f,0f, w/16f).color(br,br,br,br).tex(u0, v1).overlay(combinedOverlayIn).lightmap(scuffedLight).normal(1,1,1).endVertex();
        buf.pos(mat, 1.0015f,0f, 0.0f).color(br,br,br,br).tex(u1, v1).overlay(combinedOverlayIn).lightmap(scuffedLight).normal(1,1,1).endVertex();

        transform.pop();
    }

    private void renderBloomeryActiveTop(BloomeryTileEntity te, MatrixStack transform, IRenderTypeBuffer buffer, int combinedOverlayIn){
        IVertexBuilder buf = buffer.getBuffer(IGRenderTypes.BLOOMERY_ACTIVE);
        Direction rotation = te.getFacing();
        transform.push();
        Matrix4f mat = transform.getLast().getMatrix();

        int ux = 24, vy = 24;
        int w = 8, h = 8;
        float br = 1;
        float uw = w/48f, vh = h/48f, u0 = ux/48f, v0 = vy / 48f, u1 = u0 + uw, v1 = v0 + vh;
        World world = te.getWorld();

        assert world != null;
        int scuffedLight = 256 * ((1 + world.getLight(te.getPos().offset(rotation, 1))) / 16); //No idea why, but the normal combinedLightIn is just broken, so... We made our own.

        float xoffset = 0.25f;
        float woffset = 0.25f;

        buf.pos(mat, xoffset + 0.0015f,1.35002f + h/20f, woffset + 0.0f).color(br,br,br,br).tex(u1, v0).overlay(combinedOverlayIn).lightmap(scuffedLight).normal(1,1,1).endVertex();
        buf.pos(mat, xoffset + 0.0015f,1.35002f + h/20f, woffset + w/16f).color(br,br,br,br).tex(u0, v0).overlay(combinedOverlayIn).lightmap(scuffedLight).normal(1,1,1).endVertex();
        buf.pos(mat, xoffset + 0.5f,1.35002f + h/20f, woffset + w/16f).color(br,br,br,br).tex(u0, v1).overlay(combinedOverlayIn).lightmap(scuffedLight).normal(1,1,1).endVertex();
        buf.pos(mat, xoffset + 0.5f,1.35102f + h/20f, woffset + 0.0f).color(br,br,br,br).tex(u1, v1).overlay(combinedOverlayIn).lightmap(scuffedLight).normal(1,1,1).endVertex();

        transform.pop();
    }
}
