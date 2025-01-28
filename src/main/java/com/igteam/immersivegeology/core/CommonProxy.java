/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core;

import com.igteam.immersivegeology.common.tag.IGTags;
import com.igteam.immersivegeology.common.world.IGWorldGen;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGContent;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CommonProxy
{

	public void modConstruction()
	{
		IGLib.IG_LOGGER.info("Registering Multiblocks to Immersive Engineering");
		IGMultiblockProvider.forceClassLoad();
		IGRegistrationHolder.initialize();
		IGTags.initialize();
		IGWorldGen.init();
		IGRecipeTypes.init();
		IGContent.initializeIETweaks();
	}

	public void reinitializeGUI(){}

	public Level getClientWorld()
	{
		return null;
	}

	public Player getClientPlayer()
	{
		return null;
	}
}
