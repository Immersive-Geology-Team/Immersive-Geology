/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.particle.providers;

import com.igteam.immersivegeology.common.particle.types.FlowingWaterParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

import javax.annotation.Nullable;

public class FlowingWaterParticleProvider implements ParticleProvider<SimpleParticleType>
{
	private final SpriteSet spriteSet;

    public FlowingWaterParticleProvider(SpriteSet spriteSet) {
		this.spriteSet = spriteSet;
	}

	@Nullable
	@Override
	public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
		FlowingWaterParticle particle = new FlowingWaterParticle(level, x, y, z, xd, yd, zd);
		particle.pickSprite(spriteSet);
		return particle;
	}
}
