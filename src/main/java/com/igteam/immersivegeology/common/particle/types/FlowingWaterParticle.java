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
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FlowingWaterParticle extends TextureSheetParticle
{
	private final float spiralRadius;
	private final float spiralFrequency;
	private final float verticalSpeed;
	private final float initialYPos;

	// Appearance properties
	private final float startSize;
	private final float endSize;
	private final float startAlpha;
	private final float waveMagnitude;
	private final boolean clockwise;

	// Timing properties
	private final int fadeInTime;
	private final int fadeOutTime;


	public FlowingWaterParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
		super(level, x, y, z, xd, yd, zd);

		// Store initial Y position to calculate descent progress
		this.initialYPos = (float)y;

		// Randomize spiral properties within reasonable bounds
		this.spiralRadius = 0.6F + random.nextFloat() * 0.4F; // 0.6-1.0
		this.spiralFrequency = 0.1F + random.nextFloat() * 0.1F; // 0.1-0.2
		this.verticalSpeed = 0.01F + random.nextFloat() * 0.01F; // 0.01-0.02

		// Visual properties
		this.waveMagnitude = 0.01F + random.nextFloat() * 0.01F; // Subtle wave effect
		this.clockwise = random.nextBoolean(); // Random spiral direction

		// Size properties
		this.startSize = 0.04F + random.nextFloat() * 0.03F;
		this.endSize = this.startSize * (0.4F + random.nextFloat() * 0.3F);
		this.quadSize = this.startSize;

		// Color properties - blueish water tint
		float blueIntensity = 0.7F + random.nextFloat() * 0.3F; // 0.7-1.0
		this.rCol = 0.2F * blueIntensity;
		this.gCol = 0.4F * blueIntensity;
		this.bCol = 0.8F * blueIntensity;
		this.startAlpha = 0.7F + random.nextFloat() * 0.3F;
		this.alpha = 0.0F; // Start invisible for fade-in

		// Timing properties
		this.lifetime = 60 + random.nextInt(40); // 60-100 ticks
		this.fadeInTime = 10;
		this.fadeOutTime = 15;

		// Disable Minecraft's physics to handle movement manually
		this.hasPhysics = false;

		// Initial velocity from parameters
		this.xd = xd;
		this.yd = yd;
		this.zd = zd;
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

		// Calculate progress through lifetime (0.0 - 1.0)
		float lifeProgress = (float)this.age / (float)this.lifetime;

		// Handle fade in/out for alpha
		if (this.age < fadeInTime) {
			this.alpha = (this.age / (float)fadeInTime) * this.startAlpha;
		} else if (this.age > this.lifetime - fadeOutTime) {
			this.alpha = ((this.lifetime - this.age) / (float)fadeOutTime) * this.startAlpha;
		} else {
			this.alpha = this.startAlpha;
		}

		// Adjust size through lifetime
		this.quadSize = this.startSize + (this.endSize - this.startSize) * lifeProgress;

		// Calculate current Y position - constant downward motion
		float totalDescent = 3.5F; // Match your model's spiral height (4.5 - 1.0)
		float targetY = this.initialYPos - (totalDescent * lifeProgress);

		// Calculate the circular motion component based on progress
		// More revolutions at the beginning, slowing down toward end
		float progressFactor = 1.0F - (lifeProgress * 0.5F); // Slow down as we descend
		float twists = 11.0F; // Match your model's spiral count
		float spiralPhase = lifeProgress * twists * 6.28F; // Full revolutions

		if (!clockwise) {
			spiralPhase = -spiralPhase; // Reverse direction
		}

		// Calculate spiral position
		float currentRadius = this.spiralRadius * progressFactor;
		float dx = Mth.sin(spiralPhase) * currentRadius;
		float dz = Mth.cos(spiralPhase) * currentRadius;

		// Add wave motion
		float wavePhase = this.age * 0.1F;
		dx += Math.sin(wavePhase) * this.waveMagnitude;
		dz += Math.cos(wavePhase) * this.waveMagnitude;

		// Set position
		this.x = this.xo + dx * 0.05F;
		this.y = targetY;
		this.z = this.zo + dz * 0.05F;

		// Calculate movement direction for orientation
		float dirX = dx - (float)(this.x - this.xo);
		float dirZ = dz - (float)(this.z - this.zo);

		// Rotate particle to face direction of movement
		if (dirX != 0 || dirZ != 0) {
			this.roll = (float)Math.atan2(dirZ, dirX);
		}

		// Check for collisions with blocks
		BlockPos pos = new BlockPos(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));

		if (!level.isEmptyBlock(pos)) {
			// Simple collision response - redirect along surface
			BlockState blockState = level.getBlockState(pos);

			// If we've hit a block in the spiral structure, adapt movement
			// to follow the spiral contour rather than colliding
			if (lifeProgress < 0.95) { // Ignore collisions near the end of lifetime
				// Adjust position to stay above the collision surface
				this.y = pos.getY() + 1.0 + 0.01F;

				// Optionally create a tiny splash effect
				if (random.nextFloat() < 0.1) {
					level.addParticle(
							ParticleTypes.SPLASH,
							this.x, this.y, this.z,
							(random.nextFloat() - 0.5) * 0.1,
							random.nextFloat() * 0.1,
							(random.nextFloat() - 0.5) * 0.1
					);
				}
			}
		}
	}


	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
}