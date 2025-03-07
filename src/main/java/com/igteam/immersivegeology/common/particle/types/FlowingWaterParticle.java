/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.particle.types;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowingWaterParticle extends TextureSheetParticle
{
	private static final float GRAVITY = 0.04F;
	private static final float FRICTION = 0.97F;        // Slightly more viscous
	private static final float DAMPENING = 0.5F;        // Less bouncy (was 0.8F)
	private static final float SENSOR_DISTANCE = 0.05F; // Distance for collision sensors
	private static final float AVOID_FORCE = 0.03F;     // Force to apply when avoiding obstacles
	private static final float VISCOSITY = 0.97F;       // More viscous than current FRICTION

	public FlowingWaterParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
		super(level, x, y, z, 0, 0, 0);

		// Initial velocity - slight randomness
		this.xd = xd;
		this.yd = yd;
		this.zd = zd;

		// Visual properties
		this.setSize(0.01F, 0.01F);
		this.quadSize = random.nextFloat() * 0.05f;
		this.lifetime = 20 + random.nextInt(20);
		this.gravity = GRAVITY;

		// Water-like blue color with some variation
		float blueIntensity = 0.7F + random.nextFloat() * 0.3F;
		this.rCol = 0.2F * blueIntensity;
		this.gCol = 0.4F * blueIntensity;
		this.bCol = 0.8F * blueIntensity;
		this.alpha = 0.7F + random.nextFloat() * 0.3F;
		this.hasPhysics = false;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
			return;
		}

		this.yd -= this.gravity;

		double nextX = this.x + this.xd;
		double nextY = this.y + this.yd;
		double nextZ = this.z + this.zd;

		if (wouldCollide(nextX, nextY, nextZ)) {
			// Handle collision: determine direction of collision
			boolean xCollision = wouldCollide(nextX, this.y, this.z);
			boolean yCollision = wouldCollide(this.x, nextY, this.z);
			boolean zCollision = wouldCollide(this.x, this.y, nextZ);

			// Apply dampening on collision (inertia reduces with dampening factor)
			if (xCollision) this.xd *= -1 * DAMPENING;
			if (yCollision) this.yd *= -0.3 * DAMPENING; // More dampening on vertical motion
			if (zCollision) this.zd *= -1 * DAMPENING;

			// Add some random horizontal movement after collision to simulate flow
			if (yCollision) {
				this.xd += (random.nextFloat() - 0.5F) * 0.02F;
				this.zd += (random.nextFloat() - 0.5F) * 0.02F;
			}

			// Move the particle with adjusted velocity after collision
			this.x += this.xd;
			this.y += this.yd;
			this.z += this.zd;
		} else {
			// No collision, continue moving with inertia (velocity maintains direction)
			this.x = nextX;
			this.y = nextY;
			this.z = nextZ;
		}

		this.xd *= FRICTION;
		this.yd *= FRICTION * VISCOSITY; // Apply more friction to vertical movement
		this.zd *= FRICTION;

		updateColor();
		updateRoll();

		if (isInsideBlock()) {
			this.y += 0.01F;  // Prevent the particle from staying inside the block
			this.yd = Math.abs(this.yd) * 0.5F; // Apply some vertical velocity to push it upward
		}
	}

	private void updateColor() {
		// Base blue color
		float blueIntensity = 0.7F + random.nextFloat() * 0.3F;
		float baseRed = 0.2F * blueIntensity;
		float baseGreen = 0.4F * blueIntensity;
		float baseBlue = 0.8F * blueIntensity;

		// Calculate speed (magnitude of velocity vector)
		float speed = (float) Math.sqrt(xd * xd + yd * yd + zd * zd);

		// Make particle whiter when moving upward (foamy)
		if (yd > 0.02F) {
			// Add whiteness based on upward velocity
			float whiteFactor = (float) Math.min(1.0F, yd * 5.0F);
			this.rCol = Mth.lerp(whiteFactor, baseRed, 0.9F);
			this.gCol = Mth.lerp(whiteFactor, baseGreen, 0.9F);
			this.bCol = Mth.lerp(whiteFactor, baseBlue, 1.0F);
		} else if (speed > 0.1F) {
			// Add slight whiteness for fast-moving particles
			float whiteFactor = Math.min(0.5F, speed);
			this.rCol = Mth.lerp(whiteFactor, baseRed, 0.6F);
			this.gCol = Mth.lerp(whiteFactor, baseGreen, 0.7F);
			this.bCol = Mth.lerp(whiteFactor, baseBlue, 0.9F);
		} else {
			// Normal color for slow-moving particles
			this.rCol = baseRed;
			this.gCol = baseGreen;
			this.bCol = baseBlue;
		}

		// Fade out near end of lifetime
		if (this.age > this.lifetime * 0.8F) {
			this.alpha = 1.0F - ((float)this.age - this.lifetime * 0.8F) / (this.lifetime * 0.2F);
		}
	}

	private boolean isInsideBlock() {
		BlockPos pos = new BlockPos(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
		BlockState blockState = level.getBlockState(pos);
		VoxelShape shape = blockState.getCollisionShape(level, pos);

		if (shape.isEmpty()) {
			return false;
		}

		// Check if the particle is inside any of the block's AABBs
		AABB particleAABB = new AABB(
				this.x - 0.01, this.y - 0.01, this.z - 0.01,
				this.x + 0.01, this.y + 0.01, this.z + 0.01
		);

		for (AABB aabb : shape.toAabbs()) {
			AABB worldAABB = aabb.move(pos.getX(), pos.getY(), pos.getZ());
			if (particleAABB.intersects(worldAABB)) {
				return true;
			}
		}

		return false;
	}


	private boolean wouldCollide(double x, double y, double z) {
		// Get block at new position
		BlockPos blockPos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
		BlockState blockState = level.getBlockState(blockPos);

		// Get collision shape
		VoxelShape collisionShape = blockState.getCollisionShape(level, blockPos);

		if (collisionShape.isEmpty()) {
			return false;
		}

		// Create particle AABB at the new position
		AABB particleAABB = new AABB(
				x - 0.01, y - 0.01, z - 0.01,
				x + 0.01, y + 0.01, z + 0.01
		);

		// Check against all AABBs in the shape
		for (AABB aabb : collisionShape.toAabbs()) {
			// Adjust AABB to world coordinates
			AABB worldAABB = aabb.move(blockPos.getX(), blockPos.getY(), blockPos.getZ());

			if (particleAABB.intersects(worldAABB)) {
				return true;
			}
		}

		return false;
	}

	private void updateRoll() {
		// Calculate movement direction for orientation
		float dx = (float)(this.x - this.xo);
		float dz = (float)(this.z - this.zo);

		// Rotate particle to face direction of movement
		if (dx != 0 || dz != 0) {
			this.roll = (float)Math.atan2(dz, dx);
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
}