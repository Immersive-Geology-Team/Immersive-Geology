/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer;

import com.igteam.immersivegeology.client.IGShaders;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

public class IGRenderTypes extends RenderStateShard
{
	public IGRenderTypes(String pName, Runnable pSetupState, Runnable pClearState)
	{
		super(pName, pSetupState, pClearState);
	}

	static final RenderStateShard.DepthTestStateShard DEPTH_ALWAYS = new RenderStateShard.DepthTestStateShard("greater", GL11.GL_LESS);
	public static final RenderType GEOTHERMAL_DISPLAY;

	static final RenderStateShard.ShaderStateShard GEOTHERMAL_DISPLAY_SHADER = new RenderStateShard.ShaderStateShard(IGShaders::getGeothermalDisplayShader);
	static
	{
		GEOTHERMAL_DISPLAY = RenderType.create(
				typeName("rendertype_gui_display"),
				DefaultVertexFormat.BLOCK,
				Mode.QUADS,
				RenderType.BIG_BUFFER_SIZE,
				true,
				true,
				RenderType.CompositeState.builder()
						.setShaderState(GEOTHERMAL_DISPLAY_SHADER)
						.setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
						.setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
						.setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
						.setDepthTestState(DEPTH_ALWAYS)
						.createCompositeState(false)
		);
	}

	private static String typeName(String str){
		return IGLib.MODID + ":" + str;
	}
}
