package com.igteam.immersivegeology.common;

import com.igteam.immersivegeology.api.materials.MaterialRegistry;
import com.igteam.immersivegeology.common.materials.MaterialMetalCopper;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public class IGMaterials
{
	public static void addMetals()
	{
		MaterialRegistry.addMaterial(new MaterialMetalCopper());
	}

	public static void addMinerals()
	{

	}

	public static void addStones()
	{

	}
}
