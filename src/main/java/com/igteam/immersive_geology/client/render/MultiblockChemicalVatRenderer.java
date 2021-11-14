package com.igteam.immersive_geology.client.render;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.render.tile.TileRenderUtils;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.client.utils.TransformingVertexBuilder;
import com.igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.lib.IGRippLib;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;
import org.jline.utils.Colors;

import java.awt.*;
import java.util.List;
import java.util.Random;

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

    @OnlyIn(Dist.CLIENT)
    List<BakedQuad> list;

    Random RAND = new Random();

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

                    //WEST
                    if(master.getFacing().equals(Direction.WEST))
                    transform.translate(0.45D, 1.26, 0.38D); //move the fluid render to inside the glass container area on the model, yes magic number bad, but magic number work!

                    //display output fluid first, then choose primary over secondary.
                    IGRippLib.renderFluid(outputFluid.isEmpty() ? (fs1.isEmpty() ? fs2.getFluid() : fs1.getFluid()): outputFluid.getFluid(), te.getWorldNonnull(), te.getPos(), transform, buffer, combinedLightIn, combinedOverlayIn, 2.15f, fillAmount);
                    transform.pop();
                }
            }
            transform.pop();
        }
    }
}
