/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.helper;

import net.minecraft.core.Direction;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Class representing a cube of fluid, which appears in many model types. */
public class FluidCuboid {
	protected static final Map<Direction, FluidFace> DEFAULT_FACES;
	static {
		DEFAULT_FACES = new EnumMap<>(Direction.class);
		for (Direction direction : Direction.values()) {
			DEFAULT_FACES.put(direction, FluidFace.NORMAL);
		}
	}
	FluidCuboid(Vector3f from, Vector3f to, Map<Direction, FluidFace> faces)
	{
		this.from = from;
		this.to = to;
		this.faces = faces;
	}

	protected final Map<Direction, FluidFace> faces;
	protected final Vector3f from;
	protected final Vector3f to;

	/** Cache for scaled from */
	@Nullable
	private Vector3f fromScaled;
	/** Cache for scaled to */
	@Nullable
	private Vector3f toScaled;

	/**
	 * Checks if the fluid has the given face
	 * @param face  Face to check
	 * @return  True if the face is present
	 */
	@Nullable
	public FluidFace getFace(Direction face) {
		return faces.get(face);
	}

	/**
	 * Gets fluid from, scaled for renderer
	 * @return Scaled from
	 */
	public Vector3f getFromScaled() {
		if (fromScaled == null) {
			fromScaled = new Vector3f(from);
			fromScaled.mul(1 / 16f);
		}
		return fromScaled;
	}

	/**
	 * Gets fluid to, scaled for renderer
	 * @return Scaled from
	 */
	public Vector3f getToScaled() {
		if (toScaled == null) {
			toScaled = new Vector3f(to);
			toScaled.mul(1 / 16f);
		}
		return toScaled;
	}

	/** Represents a single fluid face in the model */
	public record FluidFace(boolean isFlowing, int rotation) {
		public static final FluidFace NORMAL = new FluidFace(false, 0);

		public FluidFace {
		}
	}


	/** Creates a new builder instance */
	public static Builder builder() {
		return new Builder();
	}

	/** Builder logic */
	public static class Builder {
		private Vector3f from = new Vector3f(0, 0, 0);
		private Vector3f to = new Vector3f(16, 16, 16);
		private final Map<Direction,FluidFace> faces = new EnumMap<>(Direction.class);

		/** Sets the starting bounds */
		public Builder from(float x, float y, float z) {
			this.from = new Vector3f(x, y, z);
			return this;
		}

		/** Sets the ending bounds */
		public Builder to(float x, float y, float z) {
			this.to = new Vector3f(x, y, z);
			return this;
		}

		/** Adds a face to the builder */
		private Builder face(Direction direction, FluidFace face) {
			FluidFace oldFace = faces.putIfAbsent(direction, face);
			if (oldFace != null) {
				throw new IllegalArgumentException("Duplicate face for " + direction + ", original " + oldFace + ", new " + face);
			}
			return this;
		}

		/** Adds a face to the builder */
		public Builder face(boolean flowing, int rotation, Direction direction, Direction... others) {
			FluidFace face = new FluidFace(flowing, rotation);
			for (Direction other : others) {
				face(other, face);
			}
			return face(direction, face);
		}

		/** Adds a face to the builder */
		public Builder face(Direction direction, Direction... others) {
			for (Direction other : others) {
				face(other, FluidFace.NORMAL);
			}
			return face(direction, FluidFace.NORMAL);
		}

		/** Builds the final instance */
		public FluidCuboid build() {
			return new FluidCuboid(from, to, faces.isEmpty() ? DEFAULT_FACES : faces);
		}
	}
}
