/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.lib.ResourceUtils;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.io.IOError;
import java.io.IOException;

@EventBusSubscriber(value = Dist.CLIENT, modid = IGLib.MODID, bus = Bus.MOD)
public class IGShaders
{
	private static ShaderInstance shader_blueprint;


	private static AbstractUniform projection_alpha;
	private static AbstractUniform projection_time;
	private static AbstractUniform projection_grid;
	public static void setBlueprintData(float alpha, float time)
	{
		IGShaders.projection_alpha.set(alpha);
		IGShaders.projection_time.set(time);
		IGShaders.projection_grid.set(0.9f);
	}

	@SubscribeEvent
	public static void registerShaders(RegisterShadersEvent event) throws IOException
	{
		ShaderInstance instance = new ShaderInstance(event.getResourceProvider(), ResourceUtils.ig("rendertype_blueprint"), DefaultVertexFormat.POSITION_COLOR_TEX);

		event.registerShader(instance,  s -> {
			IGLib.IG_LOGGER.info("Render Type Blueprint Shader Loaded");
			shader_blueprint = s;

			projection_alpha = shader_blueprint.safeGetUniform("Alpha");
			projection_time = shader_blueprint.safeGetUniform("Time");
			projection_grid = shader_blueprint.safeGetUniform("GridThickness");
		});
	}

	public static ShaderInstance getBlueprintShader()
	{
		return shader_blueprint;
	}
}
