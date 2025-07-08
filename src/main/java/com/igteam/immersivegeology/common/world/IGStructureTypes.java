/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.world.structure.RuinedFactory;
import com.igteam.immersivegeology.common.world.structure.RuinedMiningOutpost;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class IGStructureTypes
{
	public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, IGLib.MODID);

	public static final RegistryObject<StructureType<RuinedMiningOutpost>> RUINED_MINING_OUTPOST = STRUCTURE_TYPES.register("ruined_mining_outpost", () -> () -> RuinedMiningOutpost.CODEC);
	public static final RegistryObject<StructureType<RuinedFactory>> RUINED_FACTORY = STRUCTURE_TYPES.register("ruined_factory", () -> () -> RuinedFactory.CODEC);

	public static void initialize(IEventBus bus)
	{
		STRUCTURE_TYPES.register(bus);
	}
}
