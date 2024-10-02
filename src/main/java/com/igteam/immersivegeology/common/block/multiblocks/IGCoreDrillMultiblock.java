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
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic.State;
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

import static com.igteam.immersivegeology.client.renderer.multiblocks.CoreDrillRenderer.*;

public class IGCoreDrillMultiblock extends IGTemplateMultiblock {

    public static IGCoreDrillMultiblock INSTANCE = new IGCoreDrillMultiblock();

    public IGCoreDrillMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/coredrill"), new BlockPos(7,7,7), new BlockPos(3,1,8), new BlockPos(9,11,9), IGMultiblockProvider.COREDRILL);
    }

    @Override
    public float getManualScale() {
        return 6;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new CoredrillClientData(this, 7.5, 7.5, 7.5));
    }

    public static class CoredrillClientData extends IGClientMultiblockProperties {
        public CoredrillClientData(IGTemplateMultiblock multiblock, double offX, double offY, double offZ){
            super(multiblock, offX, offY, offZ);
        }

        @Override
        public void renderExtras(PoseStack poseStack, MultiBufferSource buffer)
        {
            float drill_height = 0;
            float gear_angle = 1;
            float counter_gear_angle = -1;
            float shake = 0;
            Direction dir = Direction.NORTH;
            poseStack.translate(-.5,0,-.5);
            boolean flipSupportAngle = false;

            poseStack.pushPose();
            poseStack.translate(DRILL_BIT_OFFSETS.get(dir).x(), DRILL_BIT_OFFSETS.get(dir).y() + drill_height, DRILL_BIT_OFFSETS.get(dir).z());
            poseStack.pushPose();
            float angle = 0;
            poseStack.mulPose(new Quaternionf().rotateAxis(angle * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)));
            int pPackedLight = LightTexture.FULL_BRIGHT;
            int pPackedOverlay = OverlayTexture.NO_OVERLAY;

            renderDynamicModel(DRILL_BIT, poseStack, buffer, pPackedLight, pPackedOverlay);
            poseStack.popPose();
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(DRILL_ENGINE_OFFSETS.get(dir).x() + shake, DRILL_ENGINE_OFFSETS.get(dir).y() + drill_height, DRILL_ENGINE_OFFSETS.get(dir).z() + shake);
            renderDynamicModel(DRILL_ENGINE, poseStack, buffer, pPackedLight, pPackedOverlay);

            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotateAxis((flipSupportAngle ? -90 : 90) * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)));
            poseStack.translate(DRILL_SUPPORT_OFFSETS.get(dir).x(), DRILL_SUPPORT_OFFSETS.get(dir).y(), DRILL_SUPPORT_OFFSETS.get(dir).z());
            renderDynamicModel(DRILL_ENGINE_SUPPORT, poseStack, buffer, pPackedLight, pPackedOverlay);
            poseStack.translate(DRILL_GEAR_OFFSETS.get(dir).get(0).x(), DRILL_GEAR_OFFSETS.get(dir).get(0).y(), DRILL_GEAR_OFFSETS.get(dir).get(0).z());

            poseStack.mulPose(new Quaternionf().rotateAxis((flipSupportAngle ? 90 : 0) * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)));
            poseStack.pushPose();
            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotateAxis((gear_angle * (flipSupportAngle ? 1 : -1)) * Mth.DEG_TO_RAD, DRILL_GEAR_ROTATION_OFFSETS.get(dir)));
            renderDynamicModel(DRILL_GEARSET, poseStack, buffer, pPackedLight, pPackedOverlay);
            poseStack.popPose();

            poseStack.translate(DRILL_GEAR_OFFSETS.get(dir).get(1).x(), DRILL_GEAR_OFFSETS.get(dir).get(1).y(), DRILL_GEAR_OFFSETS.get(dir).get(1).z());
            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotateAxis((counter_gear_angle * (flipSupportAngle ? 1 : -1)) * Mth.DEG_TO_RAD, DRILL_GEAR_ROTATION_OFFSETS.get(dir)));
            renderDynamicModel(DRILL_GEARSET, poseStack, buffer, pPackedLight, pPackedOverlay);
            poseStack.popPose();

            poseStack.translate(DRILL_GEAR_OFFSETS.get(dir).get(2).x(), DRILL_GEAR_OFFSETS.get(dir).get(2).y(), DRILL_GEAR_OFFSETS.get(dir).get(2).z());
            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotateAxis((gear_angle * (flipSupportAngle ? 1 : -1)) * Mth.DEG_TO_RAD, DRILL_GEAR_ROTATION_OFFSETS.get(dir)));
            renderDynamicModel(DRILL_GEARSET, poseStack, buffer, pPackedLight, pPackedOverlay);
            poseStack.popPose();

            poseStack.translate(DRILL_GEAR_OFFSETS.get(dir).get(3).x(), DRILL_GEAR_OFFSETS.get(dir).get(3).y(), DRILL_GEAR_OFFSETS.get(dir).get(3).z());
            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotateAxis((counter_gear_angle * (flipSupportAngle ? 1 : -1)) * Mth.DEG_TO_RAD, DRILL_GEAR_ROTATION_OFFSETS.get(dir)));
            renderDynamicModel(DRILL_GEARSET, poseStack, buffer, pPackedLight, pPackedOverlay);
            poseStack.popPose();
            poseStack.popPose();
            poseStack.popPose();
            poseStack.popPose();
        }

        private void renderDynamicModel(IGDynamicModel model, PoseStack matrix, MultiBufferSource buffer, int light, int overlay)
        {
            matrix.pushPose();
            List<BakedQuad> quads = model.get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null);

            // TODO Confirm if we can use a hardcoded value.
            // Overlay only contains a few bits of info (0xA0000) so we need to format this into something that we can use
            // This calculation creates '0xA0A0A0' which is about right for the color we need.
            RenderUtils.renderModelTESRFast(quads, buffer.getBuffer(RenderType.cutoutMipped()), matrix, light, overlay);
            matrix.popPose();
        }
    }
}
