/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BallmillLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BallmillLogic.State;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

import static com.igteam.immersivegeology.client.renderer.multiblocks.BallmillRenderer.AXLE;
import static com.igteam.immersivegeology.client.renderer.multiblocks.BallmillRenderer.DRUM;

public class IGBallmillMultiblock extends IGTemplateMultiblock {

    public static final IGBallmillMultiblock INSTANCE = new IGBallmillMultiblock();

    public IGBallmillMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/ballmill"), new BlockPos(2,0,1), new BlockPos(4,1,3), new BlockPos(5,3, 4), IGMultiblockProvider.BALLMILL);
    }

    @Override
    public boolean canFormWithDefaultHammer()
    {
        return true;
    }

    @Override
    public float getManualScale() {
        return 12;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new IGClientMultiblockProperties(this, 2.5, 0.5, 1.5)
        {
            @Override
            public void renderExtras(PoseStack poseStack, MultiBufferSource buffer)
            {
                poseStack.translate(-0.5,-0.5,-0.5);
                poseStack.pushPose();
                    poseStack.pushPose();
                    poseStack.translate(0.905,2.125,0.5);

                    int pPackedLight = LightTexture.FULL_BRIGHT;
                    int pPackedOverlay = OverlayTexture.NO_OVERLAY;

                    float angleDrum = 0;
                    float angleAxle = 0;
                    poseStack.mulPose(new Quaternionf().rotateAxis(angleDrum * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)));
                    renderDynamicModel(DRUM, poseStack, buffer, pPackedLight, pPackedOverlay);
                    poseStack.popPose();

                    poseStack.pushPose();
                    poseStack.translate(1.34375,0.775,0.9375);
                    poseStack.mulPose(new Quaternionf().rotateAxis(-angleAxle * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)));

                    renderDynamicModel(AXLE, poseStack, buffer, pPackedLight, pPackedOverlay);
                    poseStack.popPose();
                poseStack.popPose();
            }

            private void renderDynamicModel(IGDynamicModel model, PoseStack matrix, MultiBufferSource buffer, int light, int overlay)
            {
                matrix.pushPose();
                List<BakedQuad> quads = model.get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null);
                RenderUtils.renderModelTESRFast(quads, buffer.getBuffer(RenderType.cutoutMipped()), matrix, light, overlay);
                matrix.popPose();
            }
        });
    }

    @Override
    public String getName()
    {
        return "Ballmill";
    }


    @Override
    public int getDefaultBatchInput()
    {
        return 4;
    }

    @Override
    public int getDefaultBatchOutput()
    {
        return 4;
    };

    @Override
    public int getDefaultTime()
    {
        return 800;
    };

    @Override
    public int getDefaultEnergy()
    {
        return 64000;
    };
}
