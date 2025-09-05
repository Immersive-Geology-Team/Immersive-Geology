/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.client.helper.LinkedMultiSkin;
import com.igteam.immersivegeology.common.block.multiblocks.IGAlternatorMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.part.SkinableMultiblockPart;
import com.igteam.immersivegeology.common.block.multiblocks.skins.helpers.IIGMultiSkinHelper;
import com.igteam.immersivegeology.common.config.IGClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;

import java.lang.reflect.Field;
import java.util.*;

public abstract class IGBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T>
{
	private static final Map<Direction, Quaternionf> ROTATE_FOR_FACING = Util.make(
			new EnumMap<>(Direction.class), m -> {
				for(Direction facing : DirectionUtils.BY_HORIZONTAL_INDEX)
					m.put(facing, new Quaternionf().rotateY(Mth.DEG_TO_RAD*(180-facing.toYRot())));
			}
	);

	private final Map<String, List<BakedQuad>> quadCache = new HashMap<>();

	protected static void rotateForFacingNoCentering(PoseStack stack, Direction facing)
	{
		stack.mulPose(ROTATE_FOR_FACING.get(facing));
	}

	@Override
	public int getViewDistance()
	{

		double distanceMod = IGClientConfig.multiblockSpecialRenderDistanceModifier.get();
		return (int)(BlockEntityRenderer.super.getViewDistance() * distanceMod);
	}

	protected static void rotateForFacing(PoseStack stack, Direction facing)
	{
		stack.translate(0.5, 0.5, 0.5);
		rotateForFacingNoCentering(stack, facing);
		stack.translate(-0.5, -0.5, -0.5);
	}


	@SuppressWarnings("unchecked")
	private static <T extends Enum<T> & StringRepresentable> EnumProperty<T> castEnumProperty(Property<?> property) {
		return (EnumProperty<T>) property;
	}

	public void renderDynamicModel(IGDynamicModel model, PoseStack matrix, MultiBufferSource buffer, Direction facing, Level level, BlockPos pos, int light, int overlay, IIGMultiSkinHelper skin)
	{
		matrix.pushPose();

		final String skinKey = skin.getSerializedName();

		List<BakedQuad> outQuads = quadCache.computeIfAbsent(skinKey, key -> {
			BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();

			// Default fallback if annotation or INSTANCE not found
			BlockState state = null;

			try {
				Class<?> rendererClass = this.getClass();
				LinkedMultiSkin annotation = rendererClass.getAnnotation(LinkedMultiSkin.class);

				if (annotation != null) {
					Class<?> multiblockClass = annotation.multiblock();
					Field instanceField = multiblockClass.getField("INSTANCE"); // public static final
					Object instance = instanceField.get(null); // Static field, so null target
					if (instance instanceof IGTemplateMultiblock multiblock) {
						Block multipartBlock = multiblock.getBlock();
						if(multipartBlock instanceof SkinableMultiblockPart<?,?> part)
						{
							int ord = skin.instance().ordinal();
							state = multiblock.getBlock().defaultBlockState().setValue(castEnumProperty(part.getSkinProperty()),
									castEnumProperty(part.getSkinProperty()).getPossibleValues().stream().toList().get(ord));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (state == null) {
				state = IGAlternatorMultiblock.INSTANCE.getBlock().defaultBlockState();
			}

			BakedModel baseModel = brd.getBlockModel(state);
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
