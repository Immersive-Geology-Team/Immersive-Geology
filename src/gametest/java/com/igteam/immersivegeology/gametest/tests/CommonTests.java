/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.gametest.tests;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.common.register.IEItems.Tools;
import com.igteam.immersivegeology.common.block.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CommonTests
{

	public static List<TestFunction> all()
	{
		List<TestFunction> all = new ArrayList<>();
		all.addAll(multiblockTests());
		return all;
	}

	private static List<TestFunction> multiblockTests()
	{
		List<TestFunction> tests = new ArrayList<>();
		for(IMultiblock multiblock : MultiblockHandler.getMultiblocks())
		{
			if(multiblock instanceof IGTemplateMultiblock igMultiblock)
			{
				tests.add(new TestFunction(
						"multiblock", igMultiblock.getUniqueName().getPath(), igMultiblock.getUniqueName().toString(),
						400, 100, true, helper -> formAndDisassemble(helper, igMultiblock)
				));
			}
		}
		return tests;
	}

	private static void formAndDisassemble(GameTestHelper helper, IGTemplateMultiblock multiblock)
	{
		Player player = helper.makeMockPlayer();
		BlockPos triggerRelative = multiblock.getTriggerOffset().above();
		BlockPos triggerAbsolute = helper.absolutePos(triggerRelative);
		BlockPos testRelative = Util.make(() -> {
			for(StructureBlockInfo block : multiblock.getStructure(helper.getLevel()))
			{
				BlockPos testPos = block.pos().above();
				if(!testPos.equals(triggerRelative)&&!block.state().isAir())
					return testPos;
			}
			throw new GameTestAssertException("Multiblock only consists of trigger block?");
		});

		BlockState triggerState = helper.getBlockState(triggerRelative);
		Block originalTestBlock = helper.getBlockState(testRelative).getBlock();
		assertForm(helper, multiblock, testRelative);
		helper.runAfterDelay(20, () -> {
			helper.setBlock(triggerRelative, Blocks.AIR);
			helper.assertBlockPresent(originalTestBlock, testRelative);
			helper.runAfterDelay(20, () -> {
				helper.setBlock(triggerRelative, triggerState);
				assertForm(helper, multiblock, testRelative);
				helper.succeed();
			});
		});
	}

	private static void assertForm(GameTestHelper helper, IGTemplateMultiblock multiblock, BlockPos testPos)
	{
		formMultiblock(multiblock, helper);
		helper.assertBlockPresent(multiblock.getBlock(), testPos);
	}

	public static void formMultiblock(IMultiblock multiblock, GameTestHelper helper)
	{
		Player player = helper.makeMockPlayer();
		ItemStack ie_hammer = new ItemStack(Tools.HAMMER);
		List<ItemStack> hammerTypes = List.of(ie_hammer,
				MetalEnum.Bronze.getStack(ItemCategoryFlags.HAMMER),
				MetalEnum.StainlessSteel.getStack(ItemCategoryFlags.HAMMER),
				StoneEnum.MCStone.getStack(ItemCategoryFlags.HAMMER));



		InteractionResult finalResult = InteractionResult.FAIL;
		for(ItemStack hammer : hammerTypes)
		{
			player.setItemInHand(InteractionHand.MAIN_HAND, hammer);
			BlockPos triggerRelative = multiblock.getTriggerOffset().above();
			BlockPos triggerAbsolute = helper.absolutePos(triggerRelative);
			BlockHitResult hitResult = new BlockHitResult(Vec3.ZERO, Direction.SOUTH, triggerAbsolute, false);
			InteractionResult result = hammer.onItemUseFirst(
					new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult)
			);
			if(result==InteractionResult.SUCCESS)
			{
				finalResult = result;
				break;
			}
		}

		if(finalResult!=InteractionResult.SUCCESS)
			helper.fail("No Hammer is able to build the multiblock: " + multiblock.getUniqueName().toString() + "\nBuild Result: " + finalResult);
	}
}
