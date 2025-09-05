/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import com.igteam.immersivegeology.client.helper.LinkedMultiSkin;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.client.renderer.IGBlockEntityRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.IGAlternatorMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.AlternatorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.part.AlternatorPart;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGAlternatorSkins;
import com.igteam.immersivegeology.common.config.IGClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@LinkedMultiSkin(multiblock = IGAlternatorMultiblock.class)
public class AlternatorRenderer extends IGBlockEntityRenderer<MultiblockBlockEntityMaster<AlternatorLogic.State>>
{
    public static final String WHEEL_NAME = "alternator_wheel";
    public static IGDynamicModel WHEEL;

    @Override
    public void render(MultiblockBlockEntityMaster<AlternatorLogic.State> tile, float pPartialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay)
    {
        boolean doRendering = IGClientConfig.doSpecialRenderAlternator.get();
        if(!doRendering) return;

        IMultiblockBEHelperMaster<AlternatorLogic.State> helper = tile.getHelper();
        IMultiblockContext<AlternatorLogic.State> context = helper.getContext();

        final MultiblockOrientation orientation = context.getLevel().getOrientation();
        final AlternatorLogic.State state = context.getState();
        final IGAlternatorSkins skin = tile.getBlockState().getValue(AlternatorPart.ALTERNATOR);
        float rot = state.getRenderRotation();
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        Direction dir = orientation.front();
        boolean isMirror = orientation.mirrored();
        if(state.target_rotation == 0) pPartialTick = 0;
        poseStack.pushPose();
        poseStack.scale(0.975f,0.975f,0.975f);
        rotateForFacing(poseStack, dir);
        poseStack.pushPose();
        {
            poseStack.translate(.5f,1.5f,0);
            poseStack.pushPose();
            {
                poseStack.mulPose(new Quaternionf().rotateAxis((rot + pPartialTick)*Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
                renderDynamicModel(WHEEL, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay, skin);
                poseStack.popPose();
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }

}

