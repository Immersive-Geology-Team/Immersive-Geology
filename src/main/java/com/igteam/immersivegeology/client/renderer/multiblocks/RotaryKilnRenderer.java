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
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.client.renderer.IGBlockEntityRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.IGRotaryKilnMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.PelletizerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.common.block.multiblocks.part.RotaryKilnPart;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGRotaryKilnSkins;
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
        boolean isActive = false;

        poseStack.pushPose();
        rotateForFacing(poseStack, dir);
        poseStack.pushPose();
        poseStack.translate(1.4375, 1.8, .5);
        float angleDrum = 0;//isActive ? (rot) - pPartialTick : rot;
        poseStack.mulPose(new Quaternionf().rotateAxis((5f * (isMirror ? 1 : -1)) * Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotateAxis(angleDrum * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)));
        poseStack.scale(1.015625f,1.015625f,1.015625f);
        renderDynamicModel(TUBE, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay, skin);
        poseStack.popPose();
        poseStack.popPose();
        poseStack.popPose();
    }

    private void renderDynamicModel(IGDynamicModel model, PoseStack matrix, MultiBufferSource buffer, Direction facing, Level level, BlockPos pos, int light, int overlay, IGRotaryKilnSkins skin)
    {
        matrix.pushPose();

        final String skinKey = skin.getSerializedName();

        List<BakedQuad> outQuads = quadCache.computeIfAbsent(skinKey, key -> {
            BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
            BlockState state = IGRotaryKilnMultiblock.INSTANCE.getBlock().defaultBlockState().setValue(RotaryKilnPart.ROTARYKILN, skin);

            BakedModel baseModel = brd.getBlockModel(state);
            baseModel.getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, RenderType.cutout());
            TextureAtlasSprite newSprite = baseModel.getParticleIcon(ModelData.EMPTY);

            List<BakedQuad> baseQuads = model.get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null);

            List<BakedQuad> remapped = new ArrayList<>(baseQuads.size());
            for (BakedQuad q : baseQuads) {
                TextureAtlasSprite oldSprite = q.getSprite();
                remapped.add(remapQuad(q, oldSprite, newSprite));
            }
            return remapped;
        });

        rotateForFacing(matrix, facing);
        RenderUtils.renderModelTESRFancy(outQuads, buffer.getBuffer(RenderType.cutout()), matrix, level, pos, false, 0xffffff, light);
        matrix.popPose();
    }

    private static BakedQuad remapQuad(BakedQuad quad, TextureAtlasSprite oldSprite, TextureAtlasSprite newSprite) {
        int[] vertices = Arrays.copyOf(quad.getVertices(), quad.getVertices().length);

        float oldU0 = oldSprite.getU0(), oldU1 = oldSprite.getU1();
        float oldV0 = oldSprite.getV0(), oldV1 = oldSprite.getV1();

        float newU0 = newSprite.getU0(), newU1 = newSprite.getU1();
        float newV0 = newSprite.getV0(), newV1 = newSprite.getV1();

        for (int i = 0; i < vertices.length; i += 8) {
            float u = Float.intBitsToFloat(vertices[i + 4]);
            float v = Float.intBitsToFloat(vertices[i + 5]);

            float normU = (u - oldU0) / (oldU1 - oldU0);
            float normV = (v - oldV0) / (oldV1 - oldV0);

            float mappedU = newU0 + normU * (newU1 - newU0);
            float mappedV = newV0 + normV * (newV1 - newV0);

            vertices[i + 4] = Float.floatToRawIntBits(mappedU);
            vertices[i + 5] = Float.floatToRawIntBits(mappedV);
        }

        return new BakedQuad(
                vertices,
                quad.getTintIndex(),
                quad.getDirection(),
                newSprite,
                quad.isShade(),
                quad.hasAmbientOcclusion()
        );
    }
}

