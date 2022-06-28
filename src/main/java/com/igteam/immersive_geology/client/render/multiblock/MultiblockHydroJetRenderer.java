package com.igteam.immersive_geology.client.render.multiblock;

import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.igteam.immersive_geology.client.model.IGModel;
import com.igteam.immersive_geology.client.model.IGModels;
import com.igteam.immersive_geology.client.model.ModelChemicalVat;
import com.igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import com.igteam.immersive_geology.common.block.tileentity.HydroJetCutterTileEntity;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.lib.IGRippLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import igteam.immersive_geology.main.IGMultiblockProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MultiblockHydroJetRenderer extends TileEntityRenderer<HydroJetCutterTileEntity> {

    public static DynamicModel<Void> ARM;


    public MultiblockHydroJetRenderer(TileEntityRendererDispatcher dispatcher){
        super(dispatcher);
    }

    @Override
    public boolean isGlobalRenderer(HydroJetCutterTileEntity te) {
        return true;
    }

    @Override
    public void render(HydroJetCutterTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        //this is where we can put in special rendering things!
        if(te.formed && !te.isDummy()){

            transform.push();
            {
                Direction rotation = te.getFacing();
                switch (rotation){
                    case NORTH: {

                    }
                    case SOUTH: {
                        transform.rotate(new Quaternion(0F, 180F, 0F, true));
                    }
                    case EAST: {
                        transform.rotate(new Quaternion(0F, 270F, 0F, true));
                    }
                    case WEST: {
                        transform.rotate(new Quaternion(0F, 90F, 0F, true));
                    }
                    default:
                        break;
                }

                transform.push();
                renderArm(transform, te, buffer, combinedLightIn);
                transform.pop();
            }
            transform.pop();
        }
    }

    private void renderArm(MatrixStack matrix, HydroJetCutterTileEntity te, IRenderTypeBuffer bufferIn, int combinedOverlayIn)
    {
        matrix.push();
        matrix.translate(-.5, -.5, -.5);
        BlockState state = te.getWorld().getBlockState(te.getPos());
        if(state.getBlock() != IGMultiblockProvider.hydrojet_cutter) {
            IBakedModel model = ARM.get(null);
            BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
            blockRenderer.getBlockModelRenderer().renderModel(te.getWorldNonnull(), model, state, te.getPos(), matrix,
                    bufferIn.getBuffer(RenderType.getSolid()), true,
                    te.getWorld().rand, 0, combinedOverlayIn, EmptyModelData.INSTANCE);
        }
        matrix.pop();
    }

    public static ResourceLocation modLoc(String name) {
        return new ResourceLocation(IGLib.MODID, name);
    }
}
