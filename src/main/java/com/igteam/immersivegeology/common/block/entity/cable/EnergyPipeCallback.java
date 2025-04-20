/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.cable;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.IEProperties.VisibilityList;
import blusunrize.immersiveengineering.api.client.ieobj.BlockCallback;
import blusunrize.immersiveengineering.api.shader.ShaderCase;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.common.util.chickenbones.Matrix4;
import com.igteam.immersivegeology.common.block.entity.cable.IGEnergyPipeEntity.ConnectionStyle;
import com.mojang.math.Transformation;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Vector4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class EnergyPipeCallback implements BlockCallback<EnergyPipeCallback.Key>
{
	public static final EnergyPipeCallback INSTANCE = new EnergyPipeCallback();
	private static final EnergyPipeCallback.Key INVALID = new EnergyPipeCallback.Key(Util.make(new EnumMap(Direction.class), (m) -> {
		Direction[] var1 = DirectionUtils.VALUES;
		int var2 = var1.length;

		for(int var3 = 0; var3 < var2; ++var3) {
			Direction d = var1[var3];
			m.put(d, IGEnergyPipeEntity.ConnectionStyle.NO_CONNECTION);
		}

	}), null, null);

	public EnergyPipeCallback() {
	}

	public EnergyPipeCallback.Key extractKey(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, BlockEntity blockEntity) {
		if (!(blockEntity instanceof IGEnergyPipeEntity pipeBE)) {
			return this.getDefaultKey();
		} else {
			EnumMap connections = new EnumMap(Direction.class);
			Direction[] var7 = DirectionUtils.VALUES;
			int var8 = var7.length;

			for(int var9 = 0; var9 < var8; ++var9) {
				Direction face = var7[var9];
				connections.put(face, pipeBE.getConnectionStyle(face));
			}

			return new EnergyPipeCallback.Key(connections, pipeBE.cover == Blocks.AIR ? null : pipeBE.cover, pipeBE.getColor());
		}
	}

	public EnergyPipeCallback.Key getDefaultKey() {
		return INVALID;
	}

	public IEProperties.IEObjState getIEOBJState(EnergyPipeCallback.Key key) {
		List<String> parts = new ArrayList();
		Matrix4 rotationMatrix = new Matrix4();
		rotationMatrix.translate(0.5, 0.5, 0.5);
		Direction[] directions = Direction.values();
		Set<Direction> addedDirections = new HashSet<>();
		boolean hasAddedStraight = false;
		int totalConnections = key.numActiveConnections();
		for(Direction f : directions)
		{
			if(key.hasPluggedConnection(f))
			{
				Direction d = f;
				if(Plane.HORIZONTAL.test(f)) d = f.getOpposite();
				parts.add("elec_" + getDirectionSuffix(d));
			}
			if(key.hasCouplingConnection(f))
			{
				Direction d = f;
				if(Plane.HORIZONTAL.test(f)) d = f.getOpposite();
				parts.add("con_" + getDirectionSuffix(d));
			}
			if(key.hasActiveConnection(f))
			{
				Direction d = f;
				if(Plane.HORIZONTAL.test(f)) d = f.getOpposite();
				if(key.hasActiveConnection(f.getOpposite()) && totalConnections ==2)
				{
					if(addedDirections.contains(f.getOpposite())) continue;
					if(Plane.VERTICAL.test(f) && !hasAddedStraight)
					{
						parts.add("pipe_y");
						hasAddedStraight = true;
					}
					if(Plane.HORIZONTAL.test(f))
					{
						if(f.getAxis().equals(Axis.X) && !hasAddedStraight)
						{
							parts.add("pipe_x");
							hasAddedStraight = true;
						}
						if(f.getAxis().equals(Axis.Z) && !hasAddedStraight)
						{
							parts.add("pipe_z");
							hasAddedStraight = true;
						}
					}
				}
				if(!hasAddedStraight) {
					parts.add("pipe_" + d.getName().toLowerCase());
				}
				addedDirections.add(f);
			}
		}

		if(!hasAddedStraight)
		{
			if(totalConnections <= 1) {
				parts.add("center_frame");
			} else {
				parts.add("center");
			}
		}

		rotationMatrix.translate(-0.5, -0.5, -0.5);

		return new IEProperties.IEObjState(VisibilityList.show(parts), new Transformation(rotationMatrix.toMatrix4f()));
	}

	public List<BakedQuad> modifyQuads(EnergyPipeCallback.Key key, List<BakedQuad> quads) {
		if (key.cover() != null) {
			BlockState state = key.cover().defaultBlockState();
			BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(state);
			Iterator var5 = RenderType.chunkBufferLayers().iterator();

			while(var5.hasNext()) {
				RenderType layer = (RenderType)var5.next();
				Direction[] var7 = Direction.values();
				int var8 = var7.length;

				for(int var9 = 0; var9 < var8; ++var9) {
					Direction direction = var7[var9];
					quads.addAll(model.getQuads(state, direction, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, layer));
				}

				quads.addAll(model.getQuads(state, (Direction)null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, layer));
			}
		}

		return quads;
	}

	// Helper method to convert Direction to the appropriate suffix
	private String getDirectionSuffix(Direction dir) {
		switch (dir) {
			case UP: return "yMax";
			case DOWN: return "yMin";
			case NORTH: return "zMax";
			case SOUTH: return "zMin";
			case EAST: return "xMin";
			case WEST: return "xMax";
			default: return "";
		}
	}

	public Vector4f getRenderColor(EnergyPipeCallback.Key key, String group, String material, ShaderCase shaderCase, Vector4f original) {
		if (key.color() != null) {
			float[] rgb = key.color().getTextureDiffuseColors();
			return new Vector4f(rgb[0], rgb[1], rgb[2], 1.0F).mul(original);
		} else {
			return original;
		}
	}

	public record Key(Map<Direction, IGEnergyPipeEntity.ConnectionStyle> connections, @Nullable Block cover, @Nullable DyeColor color) {

		int numActiveConnections() {
			int count = 0;
			Iterator var2 = this.connections.values().iterator();

			while(var2.hasNext()) {
				IGEnergyPipeEntity.ConnectionStyle c = (IGEnergyPipeEntity.ConnectionStyle)var2.next();
				if (c != IGEnergyPipeEntity.ConnectionStyle.NO_CONNECTION) {
					++count;
				}
			}

			return count;
		}

		public boolean hasActiveConnection(Direction side) {
			return this.connections.get(side) != IGEnergyPipeEntity.ConnectionStyle.NO_CONNECTION;
		}

		public boolean hasCouplingConnection(Direction side) {
			return this.connections.get(side) == IGEnergyPipeEntity.ConnectionStyle.FLANGE;
		}

		public boolean hasPluggedConnection(Direction side) {
			return this.connections.get(side) == ConnectionStyle.PLUGGED;
		}

		public boolean anyPluggedConnection(Direction... sides) {
			Direction[] var2 = sides;
			int var3 = sides.length;

			for(int var4 = 0; var4 < var3; ++var4) {
				Direction side = var2[var4];
				if (this.hasPluggedConnection(side)) {
					return true;
				}
			}

			return false;
		}

		public boolean any(Direction... sides) {
			Direction[] var2 = sides;
			int var3 = sides.length;

			for(int var4 = 0; var4 < var3; ++var4) {
				Direction side = var2[var4];
				if (this.hasActiveConnection(side)) {
					return true;
				}
			}

			return false;
		}

		public boolean all(Direction... sides) {
			Direction[] var2 = sides;
			int var3 = sides.length;

			for(int var4 = 0; var4 < var3; ++var4) {
				Direction side = var2[var4];
				if (!this.hasActiveConnection(side)) {
					return false;
				}
			}

			return true;
		}

		public Map<Direction, IGEnergyPipeEntity.ConnectionStyle> connections() {
			return this.connections;
		}

		@Nullable
		public Block cover() {
			return this.cover;
		}

		@Nullable
		public DyeColor color() {
			return this.color;
		}

		public int numPluggedConnections()
		{
			int i = 0;
			Direction[] v = Direction.values();
			for(Direction d : v) if(this.hasPluggedConnection(d)) i++;
			return i;
		}
	}
}
