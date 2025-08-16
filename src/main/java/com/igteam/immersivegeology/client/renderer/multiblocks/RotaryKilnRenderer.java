/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer.multiblocks;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.igteam.immersivegeology.client.helper.LinkedMultiSkin;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.client.renderer.IGBlockEntityRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.IGRotaryKilnMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.PelletizerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.common.block.multiblocks.part.RotaryKilnPart;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGRotaryKilnSkins;
import com.igteam.immersivegeology.common.config.IGClientConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

@LinkedMultiSkin(multiblock = IGRotaryKilnMultiblock.class)
public class RotaryKilnRenderer extends IGBlockEntityRenderer<MultiblockBlockEntityMaster<RotaryKilnLogic.State>>
{
    public static final String TUBE_NAME = "rotarykiln_tube";
    public static final String LEFT_CONNECTION = "rotarykiln_tube_conl";
    public static final String RIGHT_CONNECTION = "rotarykiln_tube_conr";

    public static IGDynamicModel TUBE, LEFT_CON, RIGHT_CON;

    // Cache quads per skin name
    private final Map<String, List<BakedQuad>> quadCache = new HashMap<>();

    @Override
    public void render(MultiblockBlockEntityMaster<RotaryKilnLogic.State> tile, float pPartialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay)
    {
        boolean doRendering = IGClientConfig.doSpecialRenderRotaryKiln.get();
        if(!doRendering) return;

        IMultiblockBEHelperMaster<RotaryKilnLogic.State> helper = tile.getHelper();
        IMultiblockContext<RotaryKilnLogic.State> context = helper.getContext();

        final MultiblockOrientation orientation = context.getLevel().getOrientation();
        final RotaryKilnLogic.State state = context.getState();
        final IGRotaryKilnSkins skin = tile.getBlockState().getValue(RotaryKilnPart.ROTARYKILN);
        float rot = state.getRotation();
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        Direction dir = orientation.front();
        boolean isMirror = orientation.mirrored();

        poseStack.pushPose();
        rotateForFacing(poseStack, dir);
        poseStack.pushPose();
        poseStack.translate((isMirror ? -0.59375 : 1.4375), 0.8, .5);
		poseStack.mulPose(new Quaternionf().rotateAxis((5f * (isMirror ? 1 : -1)) * Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotateAxis(rot * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)));
        poseStack.scale(1.015625f,1.015625f,1.015625f);
        renderDynamicModel(TUBE, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay, skin);
        poseStack.popPose();
        poseStack.popPose();
        poseStack.popPose();
    }
}

