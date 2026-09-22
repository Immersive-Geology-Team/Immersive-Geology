package com.igteam.immersivegeology.client.renderer.multiblocks;

import blusunrize.immersiveengineering.client.ClientUtils;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityBallmill;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BallmillLogic;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BallmillRenderer extends TileEntitySpecialRenderer<TileEntityBallmill>
{
	public static final IGDynamicModel DRUM =
			new IGDynamicModel("models/block/multiblock/obj/ballmill/drum.obj", "gear");
	public static final IGDynamicModel AXLE =
			new IGDynamicModel("models/block/multiblock/obj/ballmill/axle.obj", "axle");

	@Override
	public void render(TileEntityBallmill te, double x, double y, double z,
					   float partialTicks, int destroyStage, float alpha)
	{
		if(te==null||!te.formed||te.isDummy()) return;

		BallmillLogic.State state = te.getState();
		if(state==null) return;

		float rot = state.getRotation();
		boolean active = state.shouldRenderActive();
		float angleDrum = active?rot+partialTicks: rot;
		float angleAxle = active?((rot*2f+12f)%360f)+partialTicks: (rot*2f+12f)%360f;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.blendFunc(770, 771);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		RenderHelper.disableStandardItemLighting();
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		rotateForFacing(te.getFacing());

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.905, 2.125, 0.5);
		GlStateManager.rotate(angleDrum, 1, 0, 0);
		drawModel(DRUM, tessellator, buffer, te.getPos());
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(1.34375, 0.775, 0.9375);
		GlStateManager.rotate(-angleAxle, 1, 0, 0);
		drawModel(AXLE, tessellator, buffer, te.getPos());
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
	}

	private void rotateForFacing(EnumFacing facing)
	{
		switch(facing)
		{
			case SOUTH -> {
				GlStateManager.translate(1, 0, 1);
				GlStateManager.rotate(180, 0, 1, 0);
			}
			case WEST -> {
				GlStateManager.translate(0, 0, 1);
				GlStateManager.rotate(90, 0, 1, 0);
			}
			case EAST -> {
				GlStateManager.translate(1, 0, 0);
				GlStateManager.rotate(270, 0, 1, 0);
			}
			default -> {
			}
		}
	}

	private void drawModel(IGDynamicModel model, Tessellator tessellator, BufferBuilder buffer, BlockPos pos)
	{
		IBakedModel baked = model.get();
		if(baked==null) return;

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		ClientUtils.renderModelTESRFast(baked.getQuads(null, null, 0), buffer, getWorld(), pos);
		tessellator.draw();
	}
}
