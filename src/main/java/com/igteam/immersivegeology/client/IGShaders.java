/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(value = Dist.CLIENT, modid = IGLib.MODID)
public class IGShaders
{
	private static ShaderInstance shader_geothermal_display;
	private static AbstractUniform alpha;

	public static void setGeothermalRenderData(float alpha)
	{
		IGShaders.alpha.set(alpha);
	}

	@SubscribeEvent
	public static void registerShaders(RegisterShadersEvent event) throws IOException
	{
		ShaderInstance instance = new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "rendertype_gui_display"), DefaultVertexFormat.BLOCK);

		event.registerShader(instance, s ->
		{
			shader_geothermal_display = s;
			alpha = shader_geothermal_display.safeGetUniform("Alpha");
		});
	}

	public static ShaderInstance getGeothermalDisplayShader()
	{
		return shader_geothermal_display;
	}
}
