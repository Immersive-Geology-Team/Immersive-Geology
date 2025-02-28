/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.particle;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class IGParticles
{
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, IGLib.MODID);

	public static final RegistryObject<SimpleParticleType> FLOWING_WATER = PARTICLES.register("flowing_water",
			() -> new SimpleParticleType(true));

	public static void register(IEventBus eventBus) {
		PARTICLES.register(eventBus);
	}
}
