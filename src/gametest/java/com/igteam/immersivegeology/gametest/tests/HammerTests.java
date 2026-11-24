/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.gametest.tests;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import com.igteam.immersivegeology.common.block.multiblocks.IGBloomeryMultiblock;
import com.igteam.immersivegeology.common.recipe.IGRepairItemRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class HammerTests
{
	private static final String TEST_AREA = IGLib.MODID+":test_area";
	private static final int DAMAGE = 10;

	public static List<TestFunction> all()
	{
		List<TestFunction> tests = new ArrayList<>();
		add(tests, "stone_hammer_forms_only_its_tier", HammerTests::stoneHammerFormsOnlyItsTier);
		add(tests, "repairing_hammers_leaves_nothing_behind", HammerTests::repairingHammersLeavesNothingBehind);
		return tests;
	}

	private static void add(List<TestFunction> tests, String name, Consumer<GameTestHelper> body)
	{
		tests.add(new TestFunction("hammer", "hammer."+name, TEST_AREA, 100, 0, true, body));
	}

	private static void stoneHammerFormsOnlyItsTier(GameTestHelper helper)
	{
		helper.assertFalse(StoneEnum.MCStone.canFormMB(IEMultiblocks.COKE_OVEN), "the stone hammer must not form the coke oven");
		helper.assertFalse(StoneEnum.MCStone.canFormMB(IEMultiblocks.BLAST_FURNACE), "the stone hammer must not form the crude blast furnace");
		helper.assertTrue(StoneEnum.MCStone.canFormMB(IEMultiblocks.ALLOY_SMELTER), "the stone hammer forms the alloy kiln");
		helper.assertTrue(StoneEnum.MCStone.canFormMB(IGBloomeryMultiblock.INSTANCE), "the stone hammer forms the crude bloomery");
		helper.assertTrue(MetalEnum.Bronze.canFormMB(IEMultiblocks.COKE_OVEN), "the bronze hammer forms the coke oven");
		helper.assertTrue(MetalEnum.Bronze.canFormMB(IEMultiblocks.BLAST_FURNACE), "the bronze hammer forms the crude blast furnace");
		helper.succeed();
	}

	private static void repairingHammersLeavesNothingBehind(GameTestHelper helper)
	{
		CraftingContainer grid = new TransientCraftingContainer(new AbstractContainerMenu(null, -1)
		{
			@Override
			public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index)
			{
				return ItemStack.EMPTY;
			}

			@Override
			public boolean stillValid(@NotNull Player player)
			{
				return true;
			}
		}, 3, 3);
		ItemStack first = StoneEnum.MCStone.getStack(ItemCategoryFlags.HAMMER);
		ItemStack second = StoneEnum.MCStone.getStack(ItemCategoryFlags.HAMMER);
		first.setDamageValue(DAMAGE);
		second.setDamageValue(DAMAGE);
		grid.setItem(0, first);
		grid.setItem(1, second);
		helper.assertFalse(first.isRepairable(), "hammers must not match the vanilla repair recipe");
		Optional<CraftingRecipe> recipe = helper.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel());
		helper.assertTrue(recipe.isPresent()&&recipe.get() instanceof IGRepairItemRecipe, "two damaged hammers should use the IG repair recipe, got "+recipe.map(Object::getClass).orElse(null));
		ItemStack result = recipe.get().assemble(grid, helper.getLevel().registryAccess());
		helper.assertTrue(result.getItem()==first.getItem()&&result.getDamageValue() < DAMAGE, "the repaired hammer should come out with less damage");
		NonNullList<ItemStack> remaining = recipe.get().getRemainingItems(grid);
		helper.assertTrue(remaining.stream().allMatch(ItemStack::isEmpty), "repairing must not leave hammers in the grid");
		helper.succeed();
	}
}
