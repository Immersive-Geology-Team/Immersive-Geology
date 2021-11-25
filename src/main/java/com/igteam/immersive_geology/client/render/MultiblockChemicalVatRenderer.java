package com.igteam.immersive_geology.client.render;

import com.igteam.immersive_geology.client.model.IGModel;
import com.igteam.immersive_geology.client.model.IGModels;
import com.igteam.immersive_geology.client.model.ModelChemicalVat;
import com.igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.lib.IGRippLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MultiblockChemicalVatRenderer extends TileEntityRenderer<ChemicalVatTileEntity> {

    public MultiblockChemicalVatRenderer(TileEntityRendererDispatcher dispatcher){
        super(dispatcher);
    }

    @Override
    public boolean isGlobalRenderer(ChemicalVatTileEntity te) {
        return true;
    }

    private static final Supplier<IGModel> stirrer = IGModels.getSupplier(ModelChemicalVat.ID);

    @Override
    public void render(ChemicalVatTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if(te != null && te.formed && !te.isDummy()){

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

                ChemicalVatTileEntity master = te.master();

                FluidTank tankPrimary = master.tanks[0];
                FluidTank tankSecondary = master.tanks[1];
                FluidTank tankOutput = master.tanks[2];

                FluidStack fs1 = tankPrimary.getFluid();
                FluidStack fs2 = tankSecondary.getFluid();
                FluidStack outputFluid = tankOutput.getFluid();

                float fillAmount = ((float)(tankPrimary.getFluidAmount() + tankSecondary.getFluidAmount())) / (24 * FluidAttributes.BUCKET_VOLUME);
                if((!tankSecondary.isEmpty() || !tankPrimary.isEmpty())) {
                    transform.push();
                    //move the fluid render to inside the glass container area on the model, yes magic number bad, but magic number work!
                    switch(master.getFacing()){
                        case NORTH:
                            transform.translate(-3.6D, 1.26, -2.5D);
                            break;
                        case WEST:
                            transform.translate(0.45D, 1.26, 0.38D);
                            break;
                        case SOUTH:
                            transform.translate(-0.2D, 1.26, 0.35D); //TODO adjust positioning
                            break;
                        case EAST:
                            transform.translate(-1D,1.26D,1D); //TODO adjust positioning
                            break;
                    }

                    ModelChemicalVat model = (ModelChemicalVat) stirrer.get();
                    if(model != null){
                        float ticks = te.activeTicks + partialTicks;
                        model.ticks = 1.5F * ticks;
                        model.render(transform, buffer.getBuffer(model.getRenderType(ModelChemicalVat.TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0f, 1.0f, 1.0f, 1.0f);
                    }

                    //display output fluid first, then choose primary over secondary.
                    IGRippLib.renderFluid(outputFluid.isEmpty() ? (fs1.isEmpty() ? fs2.getFluid() : fs1.getFluid()): outputFluid.getFluid(), te.getWorldNonnull(), te.getPos(), transform, buffer, combinedLightIn, combinedOverlayIn, 2.15f, fillAmount);
                    transform.pop();
                }
            }
            transform.pop();
        }
    }

    public static ResourceLocation modLoc(String name) {
        return new ResourceLocation(IGLib.MODID, name);
    }
}
