/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.gametest;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.gametest.tests.*;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.neoforge.gametest.GameTestHolder;

import java.util.ArrayList;
import java.util.List;

@GameTestHolder(IGLib.MODID)
public class ImmersiveGeology
{
	@GameTestGenerator
	public static List<TestFunction> generateTests()
	{
		List<TestFunction> tests = new ArrayList<>();
		tests.addAll(CommonTests.all());
		tests.addAll(ClientTests.all());
		tests.addAll(ServerTests.all());
		tests.addAll(MultiblockDisassemblyTests.all());
		tests.addAll(CapabilityLifecycleTests.all());

		return tests;
	}
}
