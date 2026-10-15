/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer;

import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.MapDecorationTextureManager;

public class DepositMapRenderer extends MapRenderer
{
	public DepositMapRenderer(TextureManager pTextureManager, MapDecorationTextureManager pDecorationTextureManager)
	{
		super(pTextureManager, pDecorationTextureManager);
	}
}
