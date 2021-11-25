package com.igteam.immersive_geology.client.render;

import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.SeparatorRecipe;
import com.igteam.immersive_geology.common.block.tileentity.GravitySeparatorTileEntity;
import com.igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
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
public class MultiblockReverberationFurnaceRenderer extends TileEntityRenderer<ReverberationFurnaceTileEntity> {
    public MultiblockReverberationFurnaceRenderer(TileEntityRendererDispatcher dispatcher){
        super(dispatcher);
    }

    //Ripped from IP's Pumpjack
    @Override
    public void render(ReverberationFurnaceTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if(te != null && !te.isDummy()) {
            transform.push();
            Direction rotation = te.getFacing();
            //TODO Update translation settings to reflect this tile entity
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

            ReverberationFurnaceTileEntity master = te.master();
            transform.pop();
        }

    }
}
