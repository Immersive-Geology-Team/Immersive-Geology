package com.igteam.immersive_geology.client.render.multiblock;

import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.igteam.immersive_geology.common.block.tileentity.HydroJetCutterTileEntity;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.lib.IGRippLib;
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



    private PositionEnum currentState = PositionEnum.ZERO;


    private float currentArmPosition = 2.125f;
    private float OldArmPosition = 2.125f;
    private float NewArmPosition = 2.125f;

    private float stageProgress = 0f;
    private void renderArm(MatrixStack matrix, HydroJetCutterTileEntity te, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        matrix.push();
        matrix.rotate(new Quaternion(0F, 180F, 0F, true));

        float machineProgress = te.progress;

        if(currentState.component == AnimationPart.ARM) {
            if(NewArmPosition != currentState.position)
                NewArmPosition = currentState.position;

            IGApi.getNewLogger().warn("Data: " + String.valueOf(currentArmPosition) + " | " + NewArmPosition  + " | " + String.valueOf(OldArmPosition) + " stage progress: " + stageProgress);

            currentArmPosition = IGRippLib.lerp(OldArmPosition, NewArmPosition, stageProgress);

            if (machineProgress > currentState.breakPercent) {
                OldArmPosition = currentArmPosition;
                currentState = currentState.advance();
                stageProgress = 0;
            } else {
                stageProgress += 0.1; //Need better way to calculate this... Not sure how we should go about it, this is a very important variable.
            }
        }


        matrix.translate(1.5, 0, currentArmPosition);
        List<BakedQuad> quads = ARM.getNullQuads(te.getFacing(), IGMultiblockProvider.hydrojet_cutter.getDefaultState());
        RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.getSolid()), matrix, combinedLightIn, combinedOverlayIn);

            matrix.push();
                renderHead(matrix, te, bufferIn, combinedLightIn, combinedOverlayIn);
            matrix.pop();

        matrix.pop();
    }

    private float headProgress = -0.03125f;
    private void renderHead(MatrixStack matrix, HydroJetCutterTileEntity te, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrix.push();
        List<BakedQuad> quads = HEAD.getNullQuads(te.getFacing(), IGMultiblockProvider.hydrojet_cutter.getDefaultState());

        float machineProgress = te.progress;
        if(currentState.component == AnimationPart.HEAD) {
            headProgress = currentState.position;

            if(machineProgress > currentState.breakPercent){
                currentState = currentState.advance();
            }
        }

        matrix.translate(headProgress, 0, 0);
        RenderUtils.renderModelTESRFast(quads, bufferIn.getBuffer(RenderType.getSolid()), matrix, combinedLightIn, combinedOverlayIn);
        matrix.pop();
    }

    public static ResourceLocation modLoc(String name) {
        return new ResourceLocation(IGLib.MODID, name);
    }

    private enum PositionEnum {
        ZERO(0f, 2.125f, AnimationPart.ARM) {
            @Override
            public PositionEnum advance() {
                return ONE;
            }

            @Override
            public PositionEnum previous() {
                return ZERO; // Screws with stage percent calculations if it loops
            }
        },
        ONE(0.16f, 1.8125f, AnimationPart.ARM){
            @Override
            public PositionEnum advance() {
                return TWO;
            }
            @Override
            public PositionEnum previous() {
                return ZERO;
            }
        },
        TWO(0.32f, -0.1875f, AnimationPart.HEAD){
            @Override
            public PositionEnum advance() {
                return THREE;
            }
            @Override
            public PositionEnum previous() {
                return ONE;
            }
        },
        THREE(0.48f, 0.125f, AnimationPart.HEAD){
            @Override
            public PositionEnum advance() {
                return FOUR;
            }
            @Override
            public PositionEnum previous() {
                return TWO;
            }
        },
        FOUR(0.64f, -0.03125f, AnimationPart.HEAD){
            @Override
            public PositionEnum advance() {
                return FIVE;
            }
            @Override
            public PositionEnum previous() {
                return THREE;
            }
        },
        FIVE(0.64f, -0.1875f, AnimationPart.HEAD){
            @Override
            public PositionEnum advance() {
                return SIX;
            }

            @Override
            public PositionEnum previous() {
                return FOUR;
            }
        },
        SIX(0.80f, 1.5f, AnimationPart.ARM){
            @Override
            public PositionEnum advance() {
                return SEVEN;
            }
            @Override
            public PositionEnum previous() {
                return FIVE;
            }
        },
        SEVEN(1f, 2.125f, AnimationPart.ARM){
            @Override
            public PositionEnum advance() {
                return ZERO;
            }

            @Override
            public PositionEnum previous() {
                return SIX;
            }
        };

        final float breakPercent;
        final float position;

        final AnimationPart component;

        PositionEnum(float bp, float position, AnimationPart component){
            this.breakPercent = bp;
            this.position = position;
            this.component = component;
        }

        public abstract PositionEnum advance();

        public abstract PositionEnum previous();
    }

    private enum AnimationPart {
        HEAD,
        ARM;
    }

}
