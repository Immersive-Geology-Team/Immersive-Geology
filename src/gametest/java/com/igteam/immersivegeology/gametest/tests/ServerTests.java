/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.gametest.tests;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

public class ServerTests
{
	private static final String TEST_AREA = IGLib.MODID + ":test_area";
	public static List<TestFunction> all()
	{
		List<TestFunction> all = new ArrayList<>();
		all.addAll(basics());
		return all;
	}

	private static List<TestFunction> basics()
	{
		List<TestFunction> tests = new ArrayList<>();
		tests.add(new TestFunction(
				"server", "startup", TEST_AREA,
				200, 0, true, ServerTests::testServerStartup
		));

		tests.add(new TestFunction(
				"server", "connection", TEST_AREA,
				200, 0, true, ServerTests::testNetworkConnectivity
		));
		return tests;
	}

	private static void testServerStartup(GameTestHelper helper) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		helper.assertTrue(server != null, "Server failed to initialize");
		helper.assertTrue(server.isRunning(), "Server is not running");
		helper.succeed();
	}

	private static void testNetworkConnectivity(GameTestHelper helper) {
		helper.runAfterDelay(40, () -> {
			try {
				MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
				helper.assertTrue(server.getPlayerList() != null,
						"Server player list not initialized");
				helper.succeed();
			} catch (Exception e) {
				helper.fail("Network test failed: " + e.getMessage());
			}
		});
	}
}
