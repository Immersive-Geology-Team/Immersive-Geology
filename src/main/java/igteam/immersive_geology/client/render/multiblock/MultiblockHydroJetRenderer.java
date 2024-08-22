package igteam.immersive_geology.client.render.multiblock;

import blusunrize.immersiveengineering.client.fx.CustomParticleManager;
import blusunrize.immersiveengineering.client.fx.IEParticles;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.recipe.HydrojetRecipe;
import igteam.immersive_geology.common.block.tileentity.HydroJetCutterTileEntity;
import igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import igteam.api.IGApi;
import igteam.api.main.IGMultiblockProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

import java.math.MathContext;
import java.util.List;

import static igteam.immersive_geology.client.render.helper.IGRenderHelper.piecewiseLerp;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MultiblockHydroJetRenderer extends TileEntityRenderer<HydroJetCutterTileEntity> {
    public static DynamicModel<Direction> ARM;

    public static DynamicModel<Direction> HEAD;


    public MultiblockHydroJetRenderer(TileEntityRendererDispatcher dispatcher){
        super(dispatcher);
    }

    @Override
    public boolean isGlobalRenderer(HydroJetCutterTileEntity te) {
        return false;
    }


    @Override
    public void render(HydroJetCutterTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        float machineProgress = 0.0f;

        //this is where we can put in special rendering things!
        if(te.formed && !te.isDummy()) {
            HydroJetCutterTileEntity master = te.master();
            if(master != null) {
                if(master.processQueue.stream().findFirst().isPresent() && master.shouldRenderActive()) {
                    PoweredMultiblockTileEntity.MultiblockProcess<HydrojetRecipe> wrapper = master.processQueue.stream().findFirst().get();
                    machineProgress = ((float) wrapper.processTick / (float) wrapper.maxTicks);
                    te.item = wrapper.recipe.getItemInput().getRandomizedExampleStack(0);
                    te.itemPile = te.item.copy();
                    te.itemPile.setCount(master.processQueue.size());
                } else {
                    te.item = ItemStack.EMPTY;
                    te.itemPile = ItemStack.EMPTY;
                }
            }
            ItemStack item = te.item;
            ItemStack itemPile = te.itemPile;

            transform.push();
            {
                transform.push();
                    Direction rotation = master.getFacing();

                    transform.push();
                        switch (rotation){
                            case WEST:
                                transform.translate(0, 0, -3);
                                if(master.getIsMirrored()){
                                    transform.rotate(new Quaternion(0, 180f,0, true));
                                    transform.translate(-3, 0, -7);
                                }
                                renderArm(transform, te, buffer, combinedLightIn, combinedOverlayIn, true, machineProgress);

                                break;
                            case NORTH:
                                if(master.getIsMirrored()) {
                                    transform.rotate(new Quaternion(0, 180f, 0, true));
                                    transform.translate(-1f,0f,-3f);
                                }
                                renderArm(transform, te, buffer, combinedLightIn, combinedOverlayIn, false, machineProgress);
                                break;
                            case EAST:
                                transform.translate(-3.5f, 0, 0f);
                                if(master.getIsMirrored()) {
                                    transform.rotate(new Quaternion(0, 180f, 0, true));
                                    transform.translate(-6, 0, -1);
                                }
                                renderArm(transform, te, buffer, combinedLightIn, combinedOverlayIn, true, machineProgress);
                                break;
                            case SOUTH:
                                transform.translate(-3, 0, -3.5);
                                if(master.getIsMirrored()) {
                                    transform.rotate(new Quaternion(0, 180f,0, true));
                                    transform.translate(-7, 0, -6);
                                }
                                renderArm(transform, te, buffer, combinedLightIn, combinedOverlayIn, false, machineProgress);
                                break;
                        }
                    transform.pop();

                    switch (rotation) {
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

                    if(master.shouldRenderActive()) {
                        animateItemProgress(transform, machineProgress, master, buffer, combinedLightIn, combinedOverlayIn, item, itemPile);
                    }
                transform.pop();
            }
            transform.pop();
        }
    }

    private void animateItemProgress(MatrixStack transform, float machineProgress, HydroJetCutterTileEntity master, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn, ItemStack item, ItemStack itemPile){
        ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
        Direction rotation = master.getFacing();
        if(machineProgress > 0.6f){
            item = master.processQueue.stream().findFirst().isPresent() ? master.processQueue.stream().findFirst().get().recipe.getItemOutput() : ItemStack.EMPTY;
        }

        transform.push();
        transform.scale(0.5f,0.5f,0.5f);
        transform.rotate(rotation.getRotation());
        transform.translate(1f, 4f, -2.2f);
        float mp = machineProgress;
        boolean changeTrack = false;
        float x = master.getPos().getX();
        float y = master.getPos().getY();
        float z = master.getPos().getZ();

        switch(master.getFacing()){
            case NORTH:
                transform.translate(2f, 1.5f,0f);
                if(master.getIsMirrored()){
                    transform.translate(-4,0, 0);
                    if(master.getWorld() != null && (machineProgress > 0.3f && machineProgress < 0.6f)) {
                        master.getWorld().addParticle(ParticleTypes.SPLASH.getType(), (x - 0.5f) - master.headCurrentPosition, y + 1.3f, (z + 3.3f) - master.currentArmPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                        master.getWorld().addParticle(ParticleTypes.BUBBLE_POP.getType(), (x- 0.5f) - master.headCurrentPosition, y + 1.1, (z + 3.3f) - master.currentArmPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                    }
                } else {
                    if(master.getWorld() != null) { // && (machineProgress > 0.3f && machineProgress < 0.6f)
                        master.getWorld().addParticle(ParticleTypes.SPLASH.getType(), (x + 1.5f) + master.headCurrentPosition, y + 1.3f, (z - 0.3f) + master.currentArmPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                        master.getWorld().addParticle(ParticleTypes.BUBBLE_POP.getType(), (x + 1.5f) + master.headCurrentPosition, y + 1.1, (z - 0.3f) + master.currentArmPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                    }
                }

                break;
            case SOUTH:
                if(master.getIsMirrored()){
                    transform.translate(-4,0, 0);
                    if(master.getWorld() != null && (machineProgress > 0.3f && machineProgress < 0.6f)){
                        master.getWorld().addParticle(ParticleTypes.SPLASH.getType(), (x + 1.5f) - master.headCurrentPosition, y + 1.4f, (z + 1) - master.currentArmPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                        master.getWorld().addParticle(ParticleTypes.BUBBLE_POP.getType(), (x + 1.5f) - master.headCurrentPosition, y + 1.25, (z + 1) - master.currentArmPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                    }
                } else {
                    if(master.getWorld() != null) master.getWorld().addParticle(ParticleTypes.FIREWORK.getType(), x, y, z, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                }
                break;
            case WEST:
                transform.translate(4.5, -4.875f,0f);
                changeTrack = true;
                if(master.getIsMirrored()){
                    transform.translate(0.5,4, 0);
                    if(master.getWorld() != null) master.getWorld().addParticle(ParticleTypes.FIREWORK.getType(), x, y, z, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                } else {
                    if(master.getWorld() != null) master.getWorld().addParticle(ParticleTypes.FIREWORK.getType(), x, y, z, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                }
                break;
            case EAST:
                mp = Math.abs(machineProgress - 1);
                transform.translate(-3.875, -1.75f,0f);
                if(master.getIsMirrored()){
                    transform.translate(4,-0.5, 0);
                    if(master.getWorld() != null && (machineProgress > 0.3f && machineProgress < 0.6f)) {
                        master.getWorld().addParticle(ParticleTypes.SPLASH.getType(), (x+1) - master.currentArmPosition, y + 1.4f, (z - 0.5) - master.headCurrentPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                        master.getWorld().addParticle(ParticleTypes.BUBBLE_POP.getType(), (x+1) - master.currentArmPosition, y + 1.25, (z - 0.5) - master.headCurrentPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                    }
                } else {
                    if(master.getWorld() != null && (machineProgress > 0.3f && machineProgress < 0.6f)) {
                        master.getWorld().addParticle(ParticleTypes.SPLASH.getType(), (x-2) + master.currentArmPosition, y + 1.4f, (z+1.5) + master.headCurrentPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                        master.getWorld().addParticle(ParticleTypes.BUBBLE_POP.getType(), (x-2) + master.currentArmPosition, y + 1.25, (z+1.5) + master.headCurrentPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
                    }
                }
                break;
        }


        float startPoint = 0f;
        float midStart = -2.4f;
        float midEnd = -4.3f;
        float endPoint = -5.4f;

        float[] Points = new float[6];
        float[] ProgressBreaks = new float[6];

        Points[0] = startPoint;
        Points[1] = midStart;
        Points[2] = midStart * 1.2f;
        Points[3] = midEnd * 0.9f;
        Points[4] = midEnd;
        Points[5] = endPoint;

        ProgressBreaks[0] = 0f;
        ProgressBreaks[1] = .3f;
        ProgressBreaks[2] = .4f;
        ProgressBreaks[3] = .6f;
        ProgressBreaks[4] = .7f;
        ProgressBreaks[5] = 1f;

        float itemPosition = piecewiseLerp(ProgressBreaks, Points, mp);
        float pos = piecewiseLerp(ProgressBreaks, Points, master.getFacing().equals(Direction.EAST) ? 0.9f : 0.1f);

        transform.push();
            transform.translate(changeTrack ? pos : -0.1f, !changeTrack ? pos : -0.2f, 0f);
            itemRender.renderItem(itemPile, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, transform, buffer);

            transform.push();
                transform.translate(0.5f,.4f,0f);
                transform.rotate(new Quaternion(0.0f, 0.0f, 0.42f, 1.0f));
                itemRender.renderItem(itemPile, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, transform, buffer);
            transform.pop();

            transform.push();
                transform.translate(-.63f,.2f,0f);
                transform.rotate(new Quaternion(0.0f, 0.42f, 0.0f, 1.0f));
                itemRender.renderItem(itemPile, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, transform, buffer);
            transform.pop();
        transform.pop();

        transform.translate(changeTrack ? itemPosition : 0f, !changeTrack ? itemPosition : 0f, 0f);
        itemRender.renderItem(item, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, transform, buffer);
        transform.pop();
    }



    private void renderArm(MatrixStack matrix, HydroJetCutterTileEntity te, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, boolean isAnimReversed, float machineProgress)
    {
        float currentArmPos = te.currentArmPosition;
        float newArmPosition = te.newArmPosition;
        HydroJetCutterTileEntity.PositionEnum currentState = te.currentState;


        if(te.shouldRenderAsActive()) {
            if (machineProgress < .3) {
                if (currentState != HydroJetCutterTileEntity.PositionEnum.ZERO) {
                    te.currentState = HydroJetCutterTileEntity.PositionEnum.ZERO; // Needed hack because sometimes progress only gets to 98%
                }
            }
            if (te.currentState.component == HydroJetCutterTileEntity.AnimationPart.ARM) {
                if (te.newArmPosition != te.currentState.position)
                    te.newArmPosition = te.currentState.position;

                if (currentArmPos < (newArmPosition - 0.0825)) {
                    te.currentArmPosition = currentArmPos + 0.0825f;

                } else if (currentArmPos > (newArmPosition + 0.0825)) {
                    te.currentArmPosition = currentArmPos - 0.0825f;

                }

                if (machineProgress > currentState.breakPercent) {
                    float offAmount = Math.abs(currentArmPos - currentState.position);
                    if (offAmount > 0.0625) {
                        IGApi.getNewLogger().warn("Animation Position was off by: " + offAmount);
                    }
                }
            }
            matrix.translate(isAnimReversed ? currentArmPos : 1.5f, 0, isAnimReversed ? 1.5f : currentArmPos);
        } else {

            matrix.translate(isAnimReversed ? 1.8125f : 1.5f, 0, isAnimReversed ? 1.5f : 1.8125f);
        }

        List<BakedQuad> quads = ARM.getNullQuads(te.getFacing(), IGMultiblockProvider.hydrojet_cutter.getDefaultState());
        RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.getSolid()), matrix, combinedLightIn, combinedOverlayIn);

            matrix.push();
                renderHead(matrix, te, bufferIn, combinedLightIn, combinedOverlayIn, isAnimReversed, currentState, machineProgress);
            matrix.pop();

        if (machineProgress > currentState.breakPercent) {
            te.machineProgress = currentState.breakPercent;
            te.currentState = currentState.advance();
        }

    }


    private void renderHead(MatrixStack matrix, HydroJetCutterTileEntity te, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, boolean isAnimReversed, HydroJetCutterTileEntity.PositionEnum currentState, float machineProgress) {
        matrix.push();
        List<BakedQuad> quads = HEAD.getNullQuads(te.getFacing(), IGMultiblockProvider.hydrojet_cutter.getDefaultState());

        if(te.shouldRenderAsActive()) {
            if (te.currentState.component == HydroJetCutterTileEntity.AnimationPart.HEAD) {
                if (te.headNewPosition != currentState.position)
                    te.headNewPosition = currentState.position;

                if (te.headCurrentPosition < te.headNewPosition - 0.05) {
                    te.headCurrentPosition += 0.05F;
                } else if (te.headCurrentPosition > te.headNewPosition + 0.05) {
                    te.headCurrentPosition -= 0.05F;
                }

                if (machineProgress > currentState.breakPercent) {
                    te.headCurrentPosition = currentState.position;
                }
            }
            matrix.translate(isAnimReversed ? 0 : te.headCurrentPosition, 0, isAnimReversed ? te.headCurrentPosition : 0);
        }
        RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.getSolid()), matrix, combinedLightIn, combinedOverlayIn);
        matrix.pop();
    }

    public static ResourceLocation modLoc(String name) {
        return new ResourceLocation(IGLib.MODID, name);
    }



}
