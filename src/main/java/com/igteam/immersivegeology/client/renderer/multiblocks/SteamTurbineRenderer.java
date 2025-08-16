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
import com.igteam.immersivegeology.common.block.multiblocks.IGSteamTurbineMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SteamTurbineLogic;
import com.igteam.immersivegeology.common.block.multiblocks.part.SteamTurbinePart;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGSteamTurbineSkins;
import com.igteam.immersivegeology.common.config.IGClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

@LinkedMultiSkin(multiblock = IGSteamTurbineMultiblock.class)
public class SteamTurbineRenderer extends IGBlockEntityRenderer<MultiblockBlockEntityMaster<SteamTurbineLogic.State>>
{
    public static final String ROTOR_NAME = "steam_turbine_rotor";
    public static IGDynamicModel ROTOR;

    // Cache quads per skin name
    private final Map<String, List<BakedQuad>> quadCache = new HashMap<>();

    @Override
    public void render(MultiblockBlockEntityMaster<SteamTurbineLogic.State> tile, float pPartialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay)
    {
        boolean doRendering = IGClientConfig.doSpecialRenderSteamTurbine.get();
        if(!doRendering) return;

        IMultiblockBEHelperMaster<SteamTurbineLogic.State> helper = tile.getHelper();
        IMultiblockContext<SteamTurbineLogic.State> context = helper.getContext();

        final MultiblockOrientation orientation = context.getLevel().getOrientation();
        final SteamTurbineLogic.State state = context.getState();
        final IGSteamTurbineSkins skin = tile.getBlockState().getValue(SteamTurbinePart.STEAM_TURBINE);
        float rot = state.getRotation();

        if(state.getTargetRotation() == 0) pPartialTick = 0;
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        Direction dir = orientation.front();
        boolean isMirror = orientation.mirrored();

        poseStack.pushPose();
        rotateForFacing(poseStack, dir);
        poseStack.pushPose();
        {
            poseStack.translate(0.5f,0.5f,1.5f);
            poseStack.pushPose();
            {
                poseStack.mulPose(new Quaternionf().rotateAxis((rot + pPartialTick)*Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
                renderDynamicModel(ROTOR, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay, skin);
                poseStack.popPose();
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}

