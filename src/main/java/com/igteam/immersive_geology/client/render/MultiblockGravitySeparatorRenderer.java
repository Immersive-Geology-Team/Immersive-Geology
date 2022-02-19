package com.igteam.immersive_geology.client.render;

import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import com.igteam.immersive_geology.common.crafting.recipes.recipe.SeparatorRecipe;
import com.igteam.immersive_geology.common.block.tileentity.GravitySeparatorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Iterator;

@OnlyIn(Dist.CLIENT)
public class MultiblockGravitySeparatorRenderer extends TileEntityRenderer<GravitySeparatorTileEntity> {
    public MultiblockGravitySeparatorRenderer(TileEntityRendererDispatcher dispatcher){
        super(dispatcher);
    }

    //Ripped from IP's Pumpjack
    @Override
    public void render(GravitySeparatorTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if(te != null && !te.isDummy()) {
            transform.push();
            Direction rotation = te.getFacing();
            switch (rotation) {
                case NORTH:
                    transform.rotate(new Quaternion(0, 90F, 0, true));
                    transform.translate(-6, 0, -1);
                    break;
                case EAST:
                    transform.translate(-5, 0, -1);
                    break;
                case SOUTH:
                    transform.rotate(new Quaternion(0, 270F, 0, true));
                    transform.translate(-5, 0, -2);
                    break;
                case WEST:
                    transform.rotate(new Quaternion(0, 180F, 0, true));
                    transform.translate(-6, 0, -2);
                    break;
                default:
                    break;
            }

            GravitySeparatorTileEntity master = te.master();

            float radius = 2;
            float ix, iy, iz;
            if(master != null){
                Iterator<PoweredMultiblockTileEntity.MultiblockProcess<SeparatorRecipe>> queueIterator = master.processQueue.iterator();

                while(queueIterator.hasNext()){
                    PoweredMultiblockTileEntity.MultiblockProcessInWorld<SeparatorRecipe> wrapper = (PoweredMultiblockTileEntity.MultiblockProcessInWorld<SeparatorRecipe>) queueIterator.next();
                    ItemStack item = wrapper.inputItems.get(0);
                    float progress = (float) wrapper.processTick;
                    float angle = (float) Math.toRadians((progress % (wrapper.maxTicks / 4)  / (wrapper.maxTicks / 4)) * 360f);
                    ix = (float) (Math.cos(angle) * radius);
                    iz = (float) (6 - (7 * (progress * 0.0045)));
                    iy = (float) (Math.sin(angle) * radius);
                    float yoffset = 2.7f;
                    float xoffset = 8.65f;
                    ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
                    transform.push();
                    float scale = .625f;
                    transform.rotate(new Quaternion(new Vector3f(1, 0, 0), -90, true));
                    transform.scale(scale, scale, 1);
                    transform.translate(0, 0, iz);
                    transform.translate(0, -yoffset + iy, 0);
                    transform.translate(xoffset + ix, 0, 0);
                    itemRender.renderItem(item, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, transform, buffer);
                    transform.pop();
                }
            }
            transform.pop();
        }

    }
}
