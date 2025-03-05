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
		this.lifetime = 40 + random.nextInt(20);
		this.gravity = GRAVITY;

		// Water-like blue color with some variation
		float blueIntensity = 0.7F + random.nextFloat() * 0.3F;
		this.rCol = 0.2F * blueIntensity;
		this.gCol = 0.4F * blueIntensity;
		this.bCol = 0.8F * blueIntensity;
		this.alpha = 0.7F + random.nextFloat() * 0.3F;

		// We'll handle physics ourselves
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

		// Apply gravity
		this.yd -= this.gravity;

		// Check sensors and adjust direction before moving
		Vec3 avoidVector = checkSensorsAndGetAvoidVector();
		if (avoidVector != null) {
			// Apply avoidance force
			this.xd += avoidVector.x;
			this.zd += avoidVector.z;
		}

		// Calculate next position
		double nextX = this.x + this.xd;
		double nextY = this.y + this.yd;
		double nextZ = this.z + this.zd;

		// Detect if we're trying to move uphill
		if (!wouldCollide(this.x, this.y, this.z) && wouldCollide(nextX, this.y, nextZ) &&
				!wouldCollide(nextX, this.y + 0.1, nextZ)) {
			// We're trying to go uphill - slow down based on slope steepness
			double yNeeded = 0;
			double testY = this.y;

			// Find how high the slope is
			while (!wouldCollide(nextX, testY, nextZ) && testY < this.y + 1.0) {
				testY += 0.1;
			}

			yNeeded = testY - this.y;

			// Slow down horizontal movement based on slope steepness
			float slopeFactor = 1.0F - Math.min(1.0F, (float)yNeeded * 2.0F);
			this.xd *= slopeFactor;
			this.zd *= slopeFactor;

			// Add some upward velocity to help flow around obstacles
			if (slopeFactor < 0.5F) {
				this.yd += 0.015F;
			}
		}

		// Check for collision at the next position
		if (wouldCollide(nextX, nextY, nextZ)) {
			// Handle collision
			// First determine which direction has the collision
			boolean xCollision = wouldCollide(nextX, this.y, this.z);
			boolean yCollision = wouldCollide(this.x, nextY, this.z);
			boolean zCollision = wouldCollide(this.x, this.y, nextZ);

			// Apply dampening based on which direction collided
			if (xCollision) {
				this.xd *= -1 * DAMPENING;
			}

			if (yCollision) {
				// Apply stronger dampening for vertical collisions (less bouncy)
				this.yd *= -0.3 * DAMPENING;

				// Horizontal splashing on impact
				if (this.yd < -0.1 && random.nextFloat() < 0.3) {
					float splashForce = random.nextFloat() * 0.05F;
					float splashAngle = random.nextFloat() * (float)Math.PI * 2.0F;

					// Add horizontal velocity for splash
					this.xd += Math.cos(splashAngle) * splashForce;
					this.zd += Math.sin(splashAngle) * splashForce;

					// Create splash particles on impact
					if (random.nextFloat() < 0.4F) {
//						level.addParticle(
//								ParticleTypes.SPLASH,
//								this.x, this.y, this.z,
//								(random.nextFloat() - 0.5) * 0.1,
//								random.nextFloat() * 0.15,
//								(random.nextFloat() - 0.5) * 0.1
//						);
					}
				}
			}

			if (zCollision) {
				this.zd *= -1 * DAMPENING;
			}

			// Add some randomness to horizontal movement to simulate flow
			if (yCollision) {
				this.xd += (random.nextFloat() - 0.5F) * 0.02F;
				this.zd += (random.nextFloat() - 0.5F) * 0.02F;
			}

			// Move with adjusted velocity
			this.x += this.xd;
			this.y += this.yd;
			this.z += this.zd;
		} else {
			// No collision, move normally
			this.x = nextX;
			this.y = nextY;
			this.z = nextZ;
		}

		// Apply friction
		this.xd *= FRICTION;
		this.yd *= FRICTION;
		this.zd *= FRICTION;

		// Apply surface tension - slows down vertical movement more than horizontal
		this.yd *= VISCOSITY;

		// Apply flow alignment - make particles tend to follow terrain
		if (wouldCollide(this.x, this.y - 0.01, this.z)) {
			// If on or near ground, prioritize horizontal movement
			float align = 0.98F;
			this.yd *= align;

			// Add a bit more speed to horizontal movement to simulate water flowing
			if (Math.abs(this.xd) + Math.abs(this.zd) < 0.05) {
				// Find a downhill direction if moving slowly
				double lowestY = Double.MAX_VALUE;
				double bestX = 0, bestZ = 0;

				// Check 8 directions for lowest point
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						if (i == 0 && j == 0) continue;

						// Get block position in this direction
						double checkX = this.x + i * 0.1;
						double checkZ = this.z + j * 0.1;
						BlockPos checkPos = new BlockPos(Mth.floor(checkX), Mth.floor(this.y - 0.2), Mth.floor(checkZ));

						// Find lowest neighboring point
						if (!wouldCollide(checkX, this.y - 0.2, checkZ) && this.y - 0.2 < lowestY) {
							lowestY = this.y - 0.2;
							bestX = i * 0.005;
							bestZ = j * 0.005;
						}
					}
				}

				// Apply force toward the lowest point (simulating water flowing downhill)
				this.xd += bestX;
				this.zd += bestZ;
			}
		}

		// Rotate particle to face direction of movement
		updateRoll();

		// Update color based on motion
		updateColor();

		// Simulate water behavior (pooling, etc)
		simulateWaterBehavior();

		// Simulate merging and splitting
		simulateWaterMergeSplit();

		// Safety check - if we somehow got stuck in a block, push the particle slightly upward
		// This helps prevent particles from vanishing
		if (isInsideBlock()) {
			this.y += 0.01F;
			this.yd = Math.abs(this.yd) * 0.5F; // Push upward
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

	private void simulateWaterBehavior() {
		// If on ground or near ground, reduce bouncing and increase horizontal spread
		if (wouldCollide(this.x, this.y - 0.03, this.z)) {
			// Reduce vertical motion more (pooling effect)
			this.yd *= 0.8F;

			// Increase horizontal spread slightly
			if (random.nextFloat() < 0.1F) {
				float spreadDir = random.nextFloat() * (float)Math.PI * 2.0F;
				float spreadForce = 0.005F;
				this.xd += Math.cos(spreadDir) * spreadForce;
				this.zd += Math.sin(spreadDir) * spreadForce;
			}

			// Reduce alpha slightly for pooled water
			this.alpha = Math.max(0.6F, this.alpha - 0.01F);
		}
	}

	private void simulateWaterMergeSplit() {
		// Purely visual effect - vary the size slightly over time
		if (random.nextFloat() < 0.05F) {
			if (random.nextBoolean() && this.quadSize < 0.08F) {
				// Grow slightly (simulating merging with another droplet)
				this.quadSize += 0.005F;
			} else if (this.quadSize > 0.03F) {
				// Shrink slightly (simulating splitting)
				this.quadSize -= 0.003F;
			}
		}

		// When hitting ground at speed, split into smaller particles
		if (this.yd < -0.15 && wouldCollide(this.x, this.y - 0.01, this.z) && random.nextFloat() < 0.2F) {
			// Reduce size of this particle
			this.quadSize *= 0.8F;

			// Create 1-3 smaller splash particles
			int splashes = random.nextInt(3) + 1;
			for (int i = 0; i < splashes; i++) {
				level.addParticle(
						ParticleTypes.DRIPPING_WATER,
						this.x, this.y, this.z,
						(random.nextFloat() - 0.5) * 0.05,
						random.nextFloat() * 0.05,
						(random.nextFloat() - 0.5) * 0.05
				);
			}
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

	private Vec3 checkSensorsAndGetAvoidVector() {
		// Check 4 sensors around the particle (left, right, front, back)
		boolean leftCollision = checkPoint(this.x - SENSOR_DISTANCE, this.y, this.z);
		boolean rightCollision = checkPoint(this.x + SENSOR_DISTANCE, this.y, this.z);
		boolean frontCollision = checkPoint(this.x, this.y, this.z + SENSOR_DISTANCE);
		boolean backCollision = checkPoint(this.x, this.y, this.z - SENSOR_DISTANCE);

		// If no collisions, return null
		if (!leftCollision && !rightCollision && !frontCollision && !backCollision) {
			return null;
		}

		// Calculate avoidance vector
		double avoidX = 0;
		double avoidZ = 0;

		// Add avoidance forces based on which sensors detected collisions
		if (leftCollision) avoidX += AVOID_FORCE;
		if (rightCollision) avoidX -= AVOID_FORCE;
		if (frontCollision) avoidZ -= AVOID_FORCE;
		if (backCollision) avoidZ += AVOID_FORCE;

		// Add a bit of randomness to avoid getting stuck in symmetrical situations
		avoidX += (random.nextFloat() - 0.5) * 0.01F;
		avoidZ += (random.nextFloat() - 0.5) * 0.01F;

		return new Vec3(avoidX, 0, avoidZ);
	}

	private boolean checkPoint(double x, double y, double z) {
		// Get block at point
		BlockPos blockPos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
		BlockState blockState = level.getBlockState(blockPos);

		// Get collision shape
		VoxelShape collisionShape = blockState.getCollisionShape(level, blockPos);

		if (collisionShape.isEmpty()) {
			return false;
		}

		// Check if point is inside the shape
		return collisionShape.bounds().contains(x - blockPos.getX(), y - blockPos.getY(), z - blockPos.getZ());
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