package igteam.immersive_geology.client.render.multiblock;

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
import io.netty.util.internal.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
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

    ItemStack item = ItemStack.EMPTY;
    ItemStack itemPile = ItemStack.EMPTY;

    @Override
    public void render(HydroJetCutterTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        //this is where we can put in special rendering things!
        if(te.formed && !te.isDummy()){
            HydroJetCutterTileEntity master = te.master();
            if(master != null) {
                if(master.processQueue.stream().findFirst().isPresent() && master.shouldRenderAsActive()) {
                    PoweredMultiblockTileEntity.MultiblockProcess<HydrojetRecipe> wrapper = master.processQueue.stream().findFirst().get();
                    machineProgress = ((float) wrapper.processTick / (float) wrapper.maxTicks);
                    item = wrapper.recipe.getItemInput().getRandomizedExampleStack(0);
                    itemPile = item.copy();
                    itemPile.setCount(master.processQueue.size());
                }
            }
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
                                renderArm(transform, te, buffer, combinedLightIn, combinedOverlayIn, true);
                                break;
                            case NORTH:
                                if(master.getIsMirrored()) {
                                    transform.rotate(new Quaternion(0, 180f, 0, true));
                                    transform.translate(-1f,0f,-3f);
                                }
                                renderArm(transform, te, buffer, combinedLightIn, combinedOverlayIn, false);
                                break;
                            case EAST:
                                transform.translate(-3.5f, 0, 0f);
                                if(master.getIsMirrored()) {
                                    transform.rotate(new Quaternion(0, 180f, 0, true));
                                    transform.translate(-6, 0, -1);
                                }
                                renderArm(transform, te, buffer, combinedLightIn, combinedOverlayIn, true);
                                break;
                            case SOUTH:
                                transform.translate(-3, 0, -3.5);
                                if(master.getIsMirrored()){
                                    transform.rotate(new Quaternion(0, 180f,0, true));
                                    transform.translate(-7, 0, -6);
                                }
                                renderArm(transform, te, buffer, combinedLightIn, combinedOverlayIn, false);
                                break;
                        }
                    transform.pop();

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
                    if(master.shouldRenderAsActive()) {
                        animateItemProgress(transform, machineProgress, master, buffer, combinedLightIn, combinedOverlayIn);
                    }
                transform.pop();
            }
            transform.pop();
        }
    }

    private void animateItemProgress(MatrixStack transform, float machineProgress, HydroJetCutterTileEntity master, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn){
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
        switch(master.getFacing()){
            case NORTH:
                transform.translate(2f, 1.5f,0f);
                if(master.getIsMirrored()){
                    transform.translate(-4,0, 0);
                }
                break;
            case SOUTH:
                if(master.getIsMirrored()){
                    transform.translate(-4,0, 0);
                }
                break;
            case WEST:
                transform.translate(4.5, -4.875f,0f);
                changeTrack = true;
                if(master.getIsMirrored()){
                    transform.translate(0.5,4, 0);
                }
                break;
            case EAST:
                mp = Math.abs(machineProgress - 1);
                transform.translate(-3.875, -1.75f,0f);
                if(master.getIsMirrored()){
                    transform.translate(4,-0.5, 0);
                }
                break;
        }


        float startPoint = 0f;
        float midStart = -2.4f;
        float midEnd = -4.3f;
        float endPoint = -6f;

        float[] Points = new float[4];
        float[] ProgressBreaks = new float[4];

        Points[0] = startPoint;
        Points[1] = midStart;
        Points[2] = midEnd;
        Points[3] = endPoint;

        ProgressBreaks[0] = 0f;
        ProgressBreaks[1] = .4f;
        ProgressBreaks[2] = .8f;
        ProgressBreaks[3] = 1f;

        float itemPosition = piecewiseLerp(ProgressBreaks, Points, mp);
        transform.translate(changeTrack ? itemPosition : 0f, !changeTrack ? itemPosition : 0f, 0f);
        itemRender.renderItem(item, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, transform, buffer);

        transform.pop();
    }


    private PositionEnum currentState = PositionEnum.ONE;

    float machineProgress = 0f;
    private float currentArmPosition = 2.125f;
    private float newArmPosition = 2.125f;
    private void renderArm(MatrixStack matrix, HydroJetCutterTileEntity te, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, boolean isAnimReversed)
    {
        if(te.shouldRenderAsActive()) {
            //IGApi.getNewLogger().warn("I'm Fucking rendering! " + currentArmPosition + " | " + machineProgress + " | " + currentState.name());
            if (machineProgress < .3) {
                if (currentState != PositionEnum.ZERO) {
                    currentState = PositionEnum.ZERO; // Needed hack because sometimes progress only gets to 98%
                }
            }
            if (currentState.component == AnimationPart.ARM) {
                if (newArmPosition != currentState.position)
                    newArmPosition = currentState.position;

                if (currentArmPosition < (newArmPosition - 0.0825)) {
                    currentArmPosition += 0.0825;
                } else if (currentArmPosition > (newArmPosition + 0.0825)) {
                    currentArmPosition -= 0.0825;
                }

                if (machineProgress > currentState.breakPercent) {
                    float offAmount = Math.abs(currentArmPosition - currentState.position);
                    if (offAmount > 0.0625) {
                        //IGApi.getNewLogger().warn("Animation Position was off by: " + offAmount);
                    }
                }
            }
            matrix.translate(isAnimReversed ? currentArmPosition : 1.5f, 0, isAnimReversed ? 1.5f : currentArmPosition);
        } else {
            // Needed to ensure that the machines arm doesn't look really odd when not active
            // This is setup this way as it prevents the strange global behaviour of the arm movements.
            matrix.translate(isAnimReversed ? 1.8125f : 1.5f, 0, isAnimReversed ? 1.5f : 1.8125f);
        }

        List<BakedQuad> quads = ARM.getNullQuads(te.getFacing(), IGMultiblockProvider.hydrojet_cutter.getDefaultState());
        RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.getSolid()), matrix, combinedLightIn, combinedOverlayIn);

            matrix.push();
                renderHead(matrix, te, bufferIn, combinedLightIn, combinedOverlayIn, isAnimReversed);
            matrix.pop();

        if (machineProgress > currentState.breakPercent) {
            machineProgress = currentState.breakPercent;
            PositionEnum nextState = currentState.advance();
            currentState = nextState;
        }
    }

    private float headCurrentPosition = -0.03125f;
    private float headNewPosition = -0.03125f;
    private void renderHead(MatrixStack matrix, HydroJetCutterTileEntity te, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, boolean isAnimReversed) {
        matrix.push();
        List<BakedQuad> quads = HEAD.getNullQuads(te.getFacing(), IGMultiblockProvider.hydrojet_cutter.getDefaultState());

        if(te.shouldRenderAsActive()) {
            if (currentState.component == AnimationPart.HEAD) {
                if (headNewPosition != currentState.position)
                    headNewPosition = currentState.position;

                if (headCurrentPosition < headNewPosition - 0.05) {
                    headCurrentPosition += 0.05;
                } else if (headCurrentPosition > headNewPosition + 0.05) {
                    headCurrentPosition -= 0.05;
                }

                if (machineProgress > currentState.breakPercent) {
                    headCurrentPosition = currentState.position;
                }
            }
            matrix.translate(isAnimReversed ? 0 : headCurrentPosition, 0, isAnimReversed ? headCurrentPosition : 0);
        }
        RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.getSolid()), matrix, combinedLightIn, combinedOverlayIn);
        matrix.pop();
    }

    public static ResourceLocation modLoc(String name) {
        return new ResourceLocation(IGLib.MODID, name);
    }

    //This system works but is not very good? Still fixing issues with it.
    private enum PositionEnum {
        ZERO(0.30f, 2.125f, AnimationPart.ARM){ //16% of the progress is spent keeping arm in starting position when looping
            @Override
            public PositionEnum advance() {
                return ONE;
            }
        },
        ONE(0.52f, 1.8125f, AnimationPart.ARM){ //16% of progress is spent moving arm to this position
            @Override
            public PositionEnum advance() {
                return TWO;
            }
        },
        TWO(0.68f, -0.1875f, AnimationPart.HEAD){ //16% of progress is spent moving head to position
            @Override
            public PositionEnum advance() {
                return THREE;
            }
        },
        THREE(0.74f, 0.125f, AnimationPart.HEAD){ //16% of progress is spent moving head to position
            @Override
            public PositionEnum advance() {
                return FOUR;
            }
        },
        FOUR(0.80f, -0.03125f, AnimationPart.HEAD){ //16% of progress is spent moving head to position
            @Override
            public PositionEnum advance() {
                return FIVE;
            }
        },
        FIVE(0.9f, 1.5f, AnimationPart.ARM){ // 20% of progress is spent moving head to position (this should be the starting position of the head)
            @Override
            public PositionEnum advance() {
                return SIX;
            }
        },
        SIX(1f, 2.125f, AnimationPart.ARM){
            @Override
            public PositionEnum advance() {
                return ZERO;
            }
        };

        public final float breakPercent;
        public final float position;

        final AnimationPart component;

        PositionEnum(float bp, float position, AnimationPart component){
            this.breakPercent = bp;
            this.position = position;
            this.component = component;
        }

        public abstract PositionEnum advance();
    }

    private enum AnimationPart {
        HEAD,
        ARM;
    }

}
