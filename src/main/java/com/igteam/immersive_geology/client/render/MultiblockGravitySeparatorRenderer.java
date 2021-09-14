package com.igteam.immersive_geology.client.render;

import com.igteam.immersive_geology.client.model.IGModel;
import com.igteam.immersive_geology.client.model.IGModels;
import com.igteam.immersive_geology.client.model.ModelGravitySeparator;
import com.igteam.immersive_geology.common.block.tileentity.GravitySeparatorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class MultiblockGravitySeparatorRenderer extends TileEntityRenderer<GravitySeparatorTileEntity> {
    public MultiblockGravitySeparatorRenderer(TileEntityRendererDispatcher dispatcher){
        super(dispatcher);
    }

    //Ripped from IP's Pumpjack
    @Override
    public void render(GravitySeparatorTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if(te != null && !te.isDummy()){
            transform.push();
            Direction rotation = te.getFacing();
            switch(rotation){
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
//
//            ModelGravitySeperator model;
//            if((model = (ModelGravitySeperator) gravityseperator_center.get()) != null){
//                float ticks = te.activeTicks + (te.wasActive ? partialTicks : 0);
//                model.ticks = 1.5F * ticks;
//
//                model.render(transform, buffer.getBuffer(model.getRenderType(ModelGravitySeperator.TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
//            }
            transform.pop();
        }
    }
}
