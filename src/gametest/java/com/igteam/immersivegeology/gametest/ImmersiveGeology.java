/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.gametest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.gametest.tests.ClientTests;
import com.igteam.immersivegeology.gametest.tests.BeltTests;
import com.igteam.immersivegeology.gametest.tests.CapabilityLifecycleTests;
import com.igteam.immersivegeology.gametest.tests.CommonTests;
import com.igteam.immersivegeology.gametest.tests.HammerTests;
import com.igteam.immersivegeology.gametest.tests.MultiblockDisassemblyTests;
import com.igteam.immersivegeology.gametest.tests.ServerTests;
import net.minecraft.gametest.framework.*;
import net.minecraftforge.gametest.GameTestHolder;

import java.util.ArrayList;
import java.util.List;

@GameTestHolder(IGLib.MODID)
public class ImmersiveGeology
{
	private static final String SUITES = "immersivegeology.gametests";

	@GameTestGenerator
	public static List<TestFunction> generateTests()
	{
		Set<String> wanted = Arrays.stream(System.getProperty(SUITES, "").split(","))
				.map(String::trim).filter(name -> !name.isEmpty()).collect(Collectors.toSet());
		Map<String, Supplier<List<TestFunction>>> suites = new LinkedHashMap<>();
		suites.put("common", CommonTests::all);
		suites.put("client", ClientTests::all);
		suites.put("server", ServerTests::all);
		suites.put("disassembly", MultiblockDisassemblyTests::all);
		suites.put("capability", CapabilityLifecycleTests::all);
		suites.put("belt", BeltTests::all);
		suites.put("hammer", HammerTests::all);
		List<TestFunction> tests = new ArrayList<>();
		suites.forEach((name, suite) -> {
			if(wanted.isEmpty()||wanted.contains(name))
				tests.addAll(suite.get());
		});
		return tests;
	}
}
