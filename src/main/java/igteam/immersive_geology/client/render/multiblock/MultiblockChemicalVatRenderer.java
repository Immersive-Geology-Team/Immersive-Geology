package igteam.immersive_geology.client.render.multiblock;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import igteam.api.processing.recipe.VatRecipe;
import igteam.immersive_geology.client.model.IGModel;
import igteam.immersive_geology.client.model.IGModels;
import igteam.immersive_geology.client.model.ModelChemicalVat;
import igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.core.lib.IGRippLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MultiblockChemicalVatRenderer extends TileEntityRenderer<ChemicalVatTileEntity> {

    public MultiblockChemicalVatRenderer(TileEntityRendererDispatcher dispatcher){
        super(dispatcher);
    }

    @Override
    public boolean isGlobalRenderer(ChemicalVatTileEntity te) {
        return false;
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
                renderItemSpin(master, transform, combinedLightIn, combinedOverlayIn, buffer, fillAmount);

                    transform.push();
                        //move the fluid render to inside the glass container area on the model, yes magic number bad, but magic number work! ~Muddykat
                    switch(master.getFacing()){
                        case NORTH:
                            transform.translate(-3.6D, 1.26, -2.6D);
                            if(!master.getIsMirrored()) {
                                transform.translate(4.05D, 0, 0);
                            }
                            break;
                        case WEST:
                            transform.translate(0.45D, 1.26, 0.38D);
                            if(!master.getIsMirrored()) {
                                transform.translate(-4.05D, 0, 0);
                            }
                            break;
                        case SOUTH:
                            transform.translate(0.45D, 1.26, -0.55D);
                            if(!master.getIsMirrored()) {
                                transform.translate(-4.05D, 0, 0);
                            }
                            break;
                        case EAST:
                            transform.translate(-1.55D,1.26D,1.45D);
                            if(!master.getIsMirrored()) {
                                transform.translate(0, 0, -4.05D);
                            }
                            break;
                    }

                    transform.push();
                        ModelChemicalVat model = (ModelChemicalVat) stirrer.get();
                        if(model != null){
                            float ticks = master.getActiveTicks() + partialTicks;
                            float old_tick = model.ticks;
                            model.ticks = master.shouldStir() ? ticks : 0;
                            transform.translate(1.125,0,1.125);

                            model.render(transform, buffer.getBuffer(model.getRenderType(ModelChemicalVat.TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0f, 1.0f, 1.0f, 1.0f);
                        }
                    transform.pop();


                if((!tankSecondary.isEmpty() || !tankPrimary.isEmpty())) {
                    transform.push();
                        IGRippLib.renderFluid(outputFluid.isEmpty() ? (fs1.isEmpty() ? fs2.getFluid() : fs1.getFluid()): outputFluid.getFluid(), te.getWorldNonnull(), te.getPos(), transform, buffer, combinedLightIn, combinedOverlayIn, 2.15f, fillAmount);
                    transform.pop();
                }
                transform.pop();
            }
            transform.pop();
        }
    }

    private void renderItemSpin(ChemicalVatTileEntity master, MatrixStack transform, int combinedLightIn, int combinedOverlayIn, IRenderTypeBuffer buffer, float fillAmount) {
        Optional<PoweredMultiblockTileEntity.MultiblockProcess<VatRecipe>> recipe = master.processQueue.stream().findFirst();
        if(recipe.isPresent()) {
            List<IngredientWithSize> itemInputs = recipe.get().recipe.getItemInputs();

            if(!itemInputs.isEmpty()){
                ItemStack inputItem = itemInputs.get(0).getRandomizedExampleStack(0);
                // Define the list of points representing positions along the spiral
                List<Vector3f> spiralPoints = new ArrayList<>();
                int numPoints = 100; // Adjust as needed
                float rotationOffset = (float) Math.toRadians(90);
                // Generate spiral points
                for (int i = 0; i < numPoints; i++) {
                    float progress = (float) i / numPoints;
                    float angle = (float) (progress * 4.33f * Math.PI + rotationOffset); // 3 full cycles
                    float radius = 1f;
                    float x = (float) (Math.cos(angle) * radius);
                    float y = (float) (Math.sin(angle) * radius);
                    float z = 1f - (1.2f * (progress * 0.5f)); // Control the height of the item
                    spiralPoints.add(new Vector3f(8.9f + x, y - 2.33f, z));
                }


                int processTick = recipe.get().processTick;
                int maxTicks = recipe.get().maxTicks;

                float progress = ((float) processTick / maxTicks);

                if(progress > 0.5){
                    inputItem = recipe.get().recipe.getRecipeOutput();
                }


                // Use the progress variable to interpolate between points
                int index = (int) (progress * (numPoints - 1));
                Vector3f startPoint = spiralPoints.get(index);
                Vector3f endPoint = spiralPoints.get(Math.min(numPoints - 1, index + 1));
                float lerpFactor = progress * (numPoints - 1) - index;

                // Interpolate between the start and end points using lerp
                float interpolatedX = startPoint.getX() + (endPoint.getX() - startPoint.getX()) * lerpFactor;
                float interpolatedY = startPoint.getY() + (endPoint.getY() - startPoint.getY()) * lerpFactor;
                float interpolatedZ = startPoint.getZ() + (endPoint.getZ() - startPoint.getZ()) * lerpFactor;

                ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
                transform.push();
                switch(master.getFacing()){
                    case NORTH:
                        transform.translate(-8.1D, 1.26, -3D);
                        if(!master.getIsMirrored()) {
                            transform.translate(4.05D, 0, 0);
                        }
                        break;
                    case WEST:
                        transform.translate(-4D, 1.26, 0D);
                        if(!master.getIsMirrored()) {
                            transform.translate(-4D, 0, 0D);
                        }
                        break;
                    case SOUTH:
                        transform.translate(-4D, 1.26, -1D);
                        if(!master.getIsMirrored()) {
                            transform.translate(-4.05D, 0, 0);
                        }
                        break;
                    case EAST:
                        transform.translate(-6D,1.26D,1D);
                        if(!master.getIsMirrored()) {
                            transform.translate(0, 0, -4.05D);
                        }
                        break;
                }
                float scale = .625f;
                transform.rotate(new Quaternion(new Vector3f(1, 0, 0), -90, true));
                transform.scale(scale, scale, 1);
                transform.translate(0, 0, interpolatedZ * fillAmount);
                transform.translate(0, interpolatedY, 0);
                transform.translate(interpolatedX, 0, 0);
                transform.rotate(new Quaternion(new Vector3f(0.9f, 0, 0), 90 * progress, true));
                itemRender.renderItem(inputItem, ItemCameraTransforms.TransformType.GUI, combinedLightIn, combinedOverlayIn, transform, buffer);
                transform.pop();
            }
        }
    }


    public static ResourceLocation modLoc(String name) {
        return new ResourceLocation(IGLib.MODID, name);
    }
}
