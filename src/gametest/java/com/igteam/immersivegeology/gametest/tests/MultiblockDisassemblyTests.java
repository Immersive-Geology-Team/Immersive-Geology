/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.gametest.tests;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import com.igteam.immersivegeology.common.block.multiblocks.IGSmallChemicalReactorMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SmallChemicalReactorLogic;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class MultiblockDisassemblyTests
{
	private static final float GUARANTEED_FAILURE_DAMAGE = 95f;

	public static List<TestFunction> all()
	{
		List<TestFunction> tests = new ArrayList<>();
		tests.add(new TestFunction(
				"multiblock", "damaged_small_chemical_reactor_disassembly",
				IGSmallChemicalReactorMultiblock.INSTANCE.getUniqueName().toString(),
				400, 0, true, MultiblockDisassemblyTests::damagedReactorDisassembly
		));
		return tests;
	}

	private static void damagedReactorDisassembly(GameTestHelper helper)
	{
		IGSmallChemicalReactorMultiblock multiblock = IGSmallChemicalReactorMultiblock.INSTANCE;
		BlockPos trigger = multiblock.getTriggerOffset().above();

		CommonTests.formMultiblock(multiblock, helper);
		helper.assertBlockPresent(multiblock.getBlock(), trigger);

		SmallChemicalReactorLogic.State state = reactorStateAt(helper, trigger);
		helper.assertTrue(state != null, "Formed reactor did not expose a SmallChemicalReactorLogic state");
		state.damage = GUARANTEED_FAILURE_DAMAGE;

		helper.runAfterDelay(20, () -> {
			helper.setBlock(trigger, Blocks.AIR);

			helper.runAfterDelay(5, () -> {
				helper.assertTrue(reactorStateAt(helper, trigger) == null, "Reactor did not disassemble");

				int debris = countDebris(helper, multiblock.getSize(helper.getLevel()));
				helper.assertTrue(debris > 0,
						"Damaged reactor disassembled without leaving failure debris - the effect did not run server-side");
				helper.succeed();
			});
		});
	}

	private static SmallChemicalReactorLogic.State reactorStateAt(GameTestHelper helper, BlockPos relativePos)
	{
		if(helper.getBlockEntity(relativePos) instanceof IMultiblockBE<?> be
				&&be.getHelper().getState() instanceof SmallChemicalReactorLogic.State state)
			return state;
		return null;
	}

	private static int countDebris(GameTestHelper helper, Vec3i size)
	{
		Block debris = MiscEnum.RustyMetal.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK);
		int found = 0;
		for(BlockPos pos : BlockPos.betweenClosed(BlockPos.ZERO, new BlockPos(size).offset(1, 1, 1)))
			if(helper.getBlockState(pos).is(debris))
				found++;
		return found;
	}
}
