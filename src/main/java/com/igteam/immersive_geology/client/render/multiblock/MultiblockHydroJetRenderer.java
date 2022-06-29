package com.igteam.immersive_geology.client.render.multiblock;

import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.igteam.immersive_geology.common.block.tileentity.HydroJetCutterTileEntity;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.main.IGMultiblockProvider;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

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
        return true;
    }

    @Override
    public void render(HydroJetCutterTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        //this is where we can put in special rendering things!
        if(te.formed && !te.isDummy()){
            machineProgress += 0.0025;

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
                renderArm(transform, te, buffer, combinedLightIn, combinedOverlayIn);
                transform.pop();
            }
            transform.pop();
        }
    }



    private PositionEnum currentState = PositionEnum.ONE;

    float machineProgress = 0f;
    private float currentArmPosition = 2.125f;
    private float newArmPosition = 2.125f;
    private void renderArm(MatrixStack matrix, HydroJetCutterTileEntity te, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        matrix.push();
        matrix.rotate(new Quaternion(0F, 180F, 0F, true));

        if(te.shouldRenderAsActive()) {
            if (currentState.component == AnimationPart.ARM) {
                if (newArmPosition != currentState.position)
                    newArmPosition = currentState.position;

                if (currentArmPosition < (newArmPosition - 0.1)) {
                    currentArmPosition += 0.0825;
                } else if (currentArmPosition > (newArmPosition + 0.1)) {
                    currentArmPosition -= 0.0825;
                }

                if(machineProgress > currentState.breakPercent){
                    float offAmount = Math.abs(currentArmPosition - currentState.position);
                    if(offAmount > 0.0625){
                        IGApi.getNewLogger().warn("Animation Position was off by: " + offAmount);
                    }
                }
            }
        }


        matrix.translate(1.5, 0, currentArmPosition);
        List<BakedQuad> quads = ARM.getNullQuads(te.getFacing(), IGMultiblockProvider.hydrojet_cutter.getDefaultState());
        RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.getSolid()), matrix, combinedLightIn, combinedOverlayIn);

            matrix.push();
                renderHead(matrix, te, bufferIn, combinedLightIn, combinedOverlayIn);
            matrix.pop();

        if (machineProgress > currentState.breakPercent) {
            machineProgress = currentState.breakPercent;
            PositionEnum nextState = currentState.advance();
            currentState = nextState;
        }

        matrix.pop();
    }

    private float headCurrentPosition = -0.03125f;
    private float headNewPosition = -0.03125f;
    private void renderHead(MatrixStack matrix, HydroJetCutterTileEntity te, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrix.push();
        List<BakedQuad> quads = HEAD.getNullQuads(te.getFacing(), IGMultiblockProvider.hydrojet_cutter.getDefaultState());

        if(te.shouldRenderAsActive()) {
            if (currentState.component == AnimationPart.HEAD) {
                if (headNewPosition != currentState.position)
                    headNewPosition = currentState.position;

                if (headCurrentPosition < (headNewPosition - 0.05125)) {
                    headCurrentPosition += 0.05;
                } else if (headCurrentPosition > (headNewPosition + 0.05125)) {
                    headCurrentPosition -= 0.05;
                }

                if (machineProgress > currentState.breakPercent) {
                    headCurrentPosition = currentState.position;
                }
            }
        }

        matrix.translate(headCurrentPosition, 0, 0);
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
