/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.gametest.tests;

import com.igteam.immersivegeology.common.block.belt.helper.BeltRun;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShape;
import com.igteam.immersivegeology.common.block.belt.helper.BeltStyle;
import com.igteam.immersivegeology.common.block.belt.IGBeltBlock;
import com.igteam.immersivegeology.common.block.belt.IGRisingBeltBlock;
import com.igteam.immersivegeology.common.block.belt.IGPoweredBeltBlock;
import com.igteam.immersivegeology.common.block.belt.IGDroppingBeltBlock;
import com.igteam.immersivegeology.common.block.belt.IGExtractionBeltBlock;
import com.igteam.immersivegeology.common.block.belt.IGSplitterBlock;
import com.igteam.immersivegeology.common.block.belt.IGEndergateBlock;
import com.igteam.immersivegeology.common.block.belt.entity.IGBeltBlockEntity;
import com.igteam.immersivegeology.common.block.belt.entity.IGEndergateBlockEntity;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import com.igteam.immersivegeology.common.block.belt.helper.BeltCoverHandler;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapes;
import com.igteam.immersivegeology.common.block.belt.network.BeltLines;
import com.igteam.immersivegeology.common.block.belt.network.LanePath;
import blusunrize.immersiveengineering.common.blocks.metal.MetalScaffoldingType;
import blusunrize.immersiveengineering.common.register.IEBlocks.MetalDecoration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import com.igteam.immersivegeology.common.block.belt.helper.EndergateLinks;
import com.igteam.immersivegeology.common.block.belt.helper.EndergateRole;
import com.igteam.immersivegeology.common.block.belt.entity.IGSplitterBlockEntity;
import com.igteam.immersivegeology.common.block.belt.helper.SplitterHalf;
import com.igteam.immersivegeology.common.block.belt.helper.SplitterPriority;
import com.igteam.immersivegeology.common.menu.IGSplitterMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import com.igteam.immersivegeology.common.block.belt.shape.RiserShape;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Block;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BeltTests
{
	private static final String TEST_AREA = IGLib.MODID+":test_area";

	private static final BlockPos START = new BlockPos(4, 1, 8);
	private static final BlockPos FIRST = new BlockPos(4, 1, 7);
	private static final BlockPos SECOND = new BlockPos(4, 2, 6);
	private static final BlockPos TOP = new BlockPos(4, 3, 5);

	public static List<TestFunction> all()
	{
		List<TestFunction> tests = new ArrayList<>();
		add(tests, "ramp_forward_order", BeltTests::rampForwardOrder);
		add(tests, "ramp_reverse_order", BeltTests::rampReverseOrder);
		add(tests, "ramp_collapses_when_top_removed", BeltTests::rampCollapsesWhenTopRemoved);
		add(tests, "ramp_flattens_under_block", BeltTests::rampFlattensUnderBlock);
		add(tests, "covered_run_any_order", BeltTests::coveredRunAnyOrder);
		add(tests, "turn_and_side_load", BeltTests::turnAndSideLoad);
		add(tests, "ramp_low_end_caps_until_fed", BeltTests::rampLowEndCapsUntilFed);
		add(tests, "valley_forms_two_rising_ls", BeltTests::valleyFormsTwoRisingLs);
		add(tests, "riser_column_and_side_feed", BeltTests::riserColumnAndSideFeed);
		add(tests, "riser_outlet_feeds_forward", BeltTests::riserOutletFeedsForward);
		add(tests, "powered_side_is_sticky", BeltTests::poweredSideIsSticky);
		add(tests, "powered_by_redstone", BeltTests::poweredByRedstone);
		add(tests, "levers_attach_to_closed_sides", BeltTests::leversAttachToClosedSides);
		add(tests, "wired_belts_stop_together", BeltTests::wiredBeltsStopTogether);
		add(tests, "covered_belts_are_lit_from_their_own_block", BeltTests::coveredBeltsAreLitFromTheirOwnBlock);
		add(tests, "dropping_belt_shapes_and_power", BeltTests::droppingBeltShapesAndPower);
		add(tests, "extraction_faces_away_from_container", BeltTests::extractionFacesAwayFromContainer);
		add(tests, "splitter_places_and_breaks_as_a_pair", BeltTests::splitterPlacesAndBreaksAsAPair);
		add(tests, "splitter_settings_and_filter", BeltTests::splitterSettingsAndFilter);
		add(tests, "splitter_creative_break_drops_nothing", BeltTests::splitterCreativeBreakDropsNothing);
		add(tests, "endergate_pairs_within_range", BeltTests::endergatePairsWithinRange);
		add(tests, "endergate_link_lock_and_sneak", BeltTests::endergateLinkLockAndSneak);
		add(tests, "belts_extend_like_scaffolding", BeltTests::beltsExtendLikeScaffolding);
		add(tests, "items_travel_and_fall_off_the_end", BeltTests::itemsTravelAndFallOffTheEnd);
		add(tests, "belt_end_fills_a_chest", BeltTests::beltEndFillsAChest);
		add(tests, "powered_belt_freezes_items", BeltTests::poweredBeltFreezesItems);
		add(tests, "hopper_extraction_and_dropping", BeltTests::hopperExtractionAndDropping);
		add(tests, "side_load_uses_the_near_lane", BeltTests::sideLoadUsesTheNearLane);
		add(tests, "belts_pick_up_dropped_items", BeltTests::beltsPickUpDroppedItems);
		add(tests, "splitter_splits_stacks_evenly", BeltTests::splitterSplitsStacksEvenly);
		add(tests, "splitter_priority_and_filter", BeltTests::splitterPriorityAndFilter);
		add(tests, "splitter_falls_back_when_one_side_is_blocked", BeltTests::splitterFallsBackWhenOneSideIsBlocked);
		add(tests, "ramp_collision_climbs_in_small_steps", BeltTests::rampCollisionClimbsInSmallSteps);
		add(tests, "broken_belt_drops_its_items", BeltTests::brokenBeltDropsItsItems);
		add(tests, "endergate_carries_items_underground", BeltTests::endergateCarriesItemsUnderground);
		add(tests, "endergate_transit_drops_when_broken", BeltTests::endergateTransitDropsWhenBroken);
		add(tests, "splitter_moves_filtered_items_mid_body", BeltTests::splitterMovesFilteredItemsMidBody);
		add(tests, "riser_drops_above_its_top", BeltTests::riserDropsAboveItsTop);
		add(tests, "endergate_side_feed_filters_lanes", BeltTests::endergateSideFeedFiltersLanes);
		add(tests, "splitter_priority_overflows_when_backed_up", BeltTests::splitterPriorityOverflowsWhenBackedUp);
		add(tests, "stalled_dropping_belt_drops_when_room_appears", BeltTests::stalledDroppingBeltDropsWhenRoomAppears);
		add(tests, "right_click_takes_a_stack_off_the_belt", BeltTests::rightClickTakesAStackOffTheBelt);
		add(tests, "scaffolding_covers_and_uncovers_belts", BeltTests::scaffoldingCoversAndUncoversBelts);
		add(tests, "scaffolding_covers_both_splitter_halves", BeltTests::scaffoldingCoversBothSplitterHalves);
		add(tests, "line_placement_fills_the_gap", BeltTests::linePlacementFillsTheGap);
		add(tests, "line_placement_needs_enough_belts", BeltTests::linePlacementNeedsEnoughBelts);
		add(tests, "line_placement_climbs_and_descends", BeltTests::linePlacementClimbsAndDescends);
		add(tests, "covered_ramps_collide_along_the_roof", BeltTests::coveredRampsCollideAlongTheRoof);
		add(tests, "line_placement_covers_from_offhand_scaffolding", BeltTests::linePlacementCoversFromOffhandScaffolding);
		add(tests, "covered_endergates_pair_and_carry", BeltTests::coveredEndergatesPairAndCarry);
		add(tests, "offhand_scaffolding_covers_every_belt_kind", BeltTests::offhandScaffoldingCoversEveryBeltKind);
		add(tests, "risers_stack_like_scaffolding", BeltTests::risersStackLikeScaffolding);
		return tests;
	}

	private static void add(List<TestFunction> tests, String name, Consumer<GameTestHelper> body)
	{
		tests.add(new TestFunction("belt", "belt."+name, TEST_AREA, 400, 0, true, body));
	}

	private static Block belt(BeltStyle style)
	{
		return IGRegistrationHolder.getBlock.apply(style.registryKey(""));
	}

	private static void place(GameTestHelper helper, BlockPos pos, BeltStyle style, Direction facing)
	{
		helper.setBlock(pos, belt(style).defaultBlockState().setValue(IGBeltBlock.FACING, facing));
	}

	private static void expect(GameTestHelper helper, BlockPos pos, BeltShape shape, BeltRun run)
	{
		BlockState state = helper.getBlockState(pos);
		helper.assertTrue(state.getBlock() instanceof IGBeltBlock, "no belt at "+pos);
		helper.assertTrue(state.getValue(IGBeltBlock.SHAPE)==shape, "expected "+shape+" at "+pos+" but was "+state.getValue(IGBeltBlock.SHAPE));
		helper.assertTrue(state.getValue(IGBeltBlock.RUN)==run, "expected "+run+" at "+pos+" but was "+state.getValue(IGBeltBlock.RUN));
	}

	private static void expectRamp(GameTestHelper helper)
	{
		expect(helper, START, BeltShape.RAMP_START, BeltRun.SINGLE);
		expect(helper, FIRST, BeltShape.RAMP, BeltRun.SINGLE);
		expect(helper, SECOND, BeltShape.RAMP_END, BeltRun.SINGLE);
		expect(helper, TOP, BeltShape.STRAIGHT, BeltRun.SINGLE);
	}

	private static void rampForwardOrder(GameTestHelper helper)
	{
		for(BlockPos pos : List.of(START, FIRST, SECOND, TOP))
			place(helper, pos, BeltStyle.STEEL, Direction.NORTH);
		expectRamp(helper);
		helper.succeed();
	}

	private static void rampReverseOrder(GameTestHelper helper)
	{
		for(BlockPos pos : List.of(TOP, SECOND, FIRST, START))
			place(helper, pos, BeltStyle.STEEL, Direction.NORTH);
		expectRamp(helper);
		helper.succeed();
	}

	private static void rampCollapsesWhenTopRemoved(GameTestHelper helper)
	{
		for(BlockPos pos : List.of(SECOND, START, TOP, FIRST))
			place(helper, pos, BeltStyle.STEEL, Direction.NORTH);
		expectRamp(helper);
		helper.setBlock(TOP, Blocks.AIR);
		expect(helper, START, BeltShape.RAMP_START, BeltRun.SINGLE);
		expect(helper, FIRST, BeltShape.RAMP_END, BeltRun.SINGLE);
		expect(helper, SECOND, BeltShape.STRAIGHT, BeltRun.SINGLE);
		helper.setBlock(SECOND, Blocks.AIR);
		expect(helper, START, BeltShape.STRAIGHT, BeltRun.SINGLE);
		expect(helper, FIRST, BeltShape.STRAIGHT, BeltRun.SINGLE);
		helper.succeed();
	}

	private static void rampFlattensUnderBlock(GameTestHelper helper)
	{
		for(BlockPos pos : List.of(START, FIRST, SECOND, TOP))
			place(helper, pos, BeltStyle.STEEL, Direction.NORTH);
		helper.setBlock(FIRST.above(), Blocks.STONE);
		expect(helper, START, BeltShape.STRAIGHT, BeltRun.SINGLE);
		expect(helper, FIRST, BeltShape.STRAIGHT, BeltRun.SINGLE);
		expect(helper, SECOND, BeltShape.RAMP_END, BeltRun.SINGLE);
		helper.setBlock(FIRST.above(), Blocks.AIR);
		expectRamp(helper);
		helper.succeed();
	}

	private static void coveredRunAnyOrder(GameTestHelper helper)
	{
		BlockPos back = new BlockPos(8, 1, 8), mid = new BlockPos(8, 1, 7), front = new BlockPos(8, 1, 6);
		for(BlockPos pos : List.of(mid, front, back))
			place(helper, pos, BeltStyle.STEEL_COVERED, Direction.NORTH);
		expect(helper, back, BeltShape.STRAIGHT, BeltRun.START);
		expect(helper, mid, BeltShape.STRAIGHT, BeltRun.MIDDLE);
		expect(helper, front, BeltShape.STRAIGHT, BeltRun.END);
		helper.setBlock(mid, Blocks.AIR);
		expect(helper, back, BeltShape.STRAIGHT, BeltRun.SINGLE);
		expect(helper, front, BeltShape.STRAIGHT, BeltRun.SINGLE);
		helper.succeed();
	}

	private static void turnAndSideLoad(GameTestHelper helper)
	{
		BlockPos centre = new BlockPos(11, 1, 8);
		place(helper, centre, BeltStyle.STEEL, Direction.NORTH);
		place(helper, centre.west(), BeltStyle.STEEL, Direction.EAST);
		expect(helper, centre, BeltShape.FROM_LEFT, BeltRun.SINGLE);
		place(helper, centre.east(), BeltStyle.STEEL, Direction.WEST);
		expect(helper, centre, BeltShape.FROM_BOTH, BeltRun.SINGLE);
		place(helper, centre.west(), BeltStyle.STEEL, Direction.WEST);
		expect(helper, centre, BeltShape.FROM_RIGHT, BeltRun.SINGLE);
		helper.succeed();
	}

	private static void rampLowEndCapsUntilFed(GameTestHelper helper)
	{
		for(BlockPos pos : List.of(FIRST, SECOND, TOP))
			place(helper, pos, BeltStyle.STEEL_COVERED, Direction.NORTH);
		helper.assertTrue(helper.getBlockState(FIRST).getValue(IGBeltBlock.CAPPED), "lowest ramp should be capped with nothing feeding it");
		place(helper, START, BeltStyle.STEEL, Direction.NORTH);
		helper.assertFalse(helper.getBlockState(FIRST).getValue(IGBeltBlock.CAPPED), "ramp should open once a ramp start feeds it");
		helper.setBlock(START, Blocks.AIR);
		helper.assertTrue(helper.getBlockState(FIRST).getValue(IGBeltBlock.CAPPED), "ramp should cap again when its feeder is removed");
		helper.succeed();
	}

	private static void valleyFormsTwoRisingLs(GameTestHelper helper)
	{
		BlockPos in = new BlockPos(10, 2, 11), drop = new BlockPos(10, 1, 10), rise = new BlockPos(10, 1, 9), out = new BlockPos(10, 2, 8);
		for(BlockPos pos : List.of(out, drop, in, rise))
			place(helper, pos, BeltStyle.STEEL, Direction.NORTH);
		expect(helper, drop, BeltShape.VALLEY_DROP_END, BeltRun.SINGLE);
		expect(helper, rise, BeltShape.VALLEY_RISE_END, BeltRun.SINGLE);
		helper.setBlock(in, Blocks.AIR);
		expect(helper, drop, BeltShape.RAMP_START, BeltRun.SINGLE);
		expect(helper, rise, BeltShape.RAMP_END, BeltRun.SINGLE);
		helper.succeed();
	}

	private static void placeRiser(GameTestHelper helper, BlockPos pos, Direction facing)
	{
		Block riser = IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL.registryKey("rising"));
		helper.setBlock(pos, riser.defaultBlockState().setValue(IGBeltBlock.FACING, facing));
	}

	private static void expectRiser(GameTestHelper helper, BlockPos pos, RiserShape shape)
	{
		BlockState state = helper.getBlockState(pos);
		helper.assertTrue(state.getBlock() instanceof IGRisingBeltBlock, "no riser at "+pos);
		helper.assertTrue(state.getValue(IGRisingBeltBlock.RISER_SHAPE)==shape, "expected "+shape+" at "+pos+" but was "+state.getValue(IGRisingBeltBlock.RISER_SHAPE));
	}

	private static void riserColumnAndSideFeed(GameTestHelper helper)
	{
		BlockPos bottom = new BlockPos(13, 1, 12), middle = bottom.above(), top = middle.above(), feeder = bottom.west();
		placeRiser(helper, middle, Direction.NORTH);
		placeRiser(helper, top, Direction.NORTH);
		placeRiser(helper, bottom, Direction.NORTH);
		expectRiser(helper, bottom, RiserShape.BASE);
		expectRiser(helper, middle, RiserShape.STRAIGHT);
		expectRiser(helper, top, RiserShape.STRAIGHT);
		place(helper, feeder, BeltStyle.STEEL, Direction.EAST);
		expectRiser(helper, bottom, RiserShape.FROM_LEFT);
		expect(helper, feeder, BeltShape.TO_RISER_LEFT, BeltRun.SINGLE);
		helper.setBlock(bottom, Blocks.AIR);
		expectRiser(helper, middle, RiserShape.BASE);
		place(helper, middle.south(), BeltStyle.STEEL, Direction.NORTH);
		expectRiser(helper, middle, RiserShape.CONNECTED);
		expect(helper, feeder, BeltShape.STRAIGHT, BeltRun.SINGLE);
		helper.succeed();
	}

	private static void riserOutletFeedsForward(GameTestHelper helper)
	{
		BlockPos riser = new BlockPos(2, 1, 12), out = riser.north().above();
		placeRiser(helper, riser, Direction.NORTH);
		helper.assertFalse(helper.getBlockState(riser).getValue(IGRisingBeltBlock.OUTLET), "riser should not have an outlet yet");
		place(helper, out, BeltStyle.STEEL, Direction.NORTH);
		helper.assertTrue(helper.getBlockState(riser).getValue(IGRisingBeltBlock.OUTLET), "riser should turn into the forward belt");
		place(helper, out, BeltStyle.STEEL, Direction.EAST);
		helper.assertTrue(helper.getBlockState(riser).getValue(IGRisingBeltBlock.OUTLET), "riser should also feed a crossing belt");
		expect(helper, out, BeltShape.FROM_RIGHT, BeltRun.SINGLE);
		place(helper, riser.above(), BeltStyle.STEEL, Direction.EAST);
		helper.assertFalse(helper.getBlockState(riser).getValue(IGRisingBeltBlock.OUTLET), "a belt on top takes priority");
		helper.succeed();
	}

	private static void placePowered(GameTestHelper helper, BlockPos pos, Direction facing)
	{
		Block powered = IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL.registryKey("powered"));
		helper.setBlock(pos, powered.defaultBlockState().setValue(IGBeltBlock.FACING, facing));
	}

	private static void expectPowered(GameTestHelper helper, BlockPos pos, BeltShape shape)
	{
		BeltShape actual = helper.getBlockState(pos).getValue(IGPoweredBeltBlock.POWERED_SHAPE);
		helper.assertTrue(actual==shape, "expected "+shape+" at "+pos+" but was "+actual);
	}

	private static void poweredSideIsSticky(GameTestHelper helper)
	{
		BlockPos belt = new BlockPos(6, 1, 13);
		placePowered(helper, belt, Direction.NORTH);
		place(helper, belt.west(), BeltStyle.STEEL, Direction.EAST);
		expectPowered(helper, belt, BeltShape.FROM_LEFT);
		place(helper, belt.east(), BeltStyle.STEEL, Direction.WEST);
		expectPowered(helper, belt, BeltShape.FROM_LEFT);
		helper.setBlock(belt.west(), Blocks.AIR);
		expectPowered(helper, belt, BeltShape.FROM_RIGHT);
		helper.succeed();
	}

	private static void poweredByRedstone(GameTestHelper helper)
	{
		BlockPos belt = new BlockPos(9, 1, 13);
		placePowered(helper, belt, Direction.NORTH);
		helper.assertFalse(helper.getBlockState(belt).getValue(IGPoweredBeltBlock.POWERED), "belt should start running");
		helper.setBlock(belt.south(), Blocks.REDSTONE_BLOCK);
		helper.assertTrue(helper.getBlockState(belt).getValue(IGPoweredBeltBlock.POWERED), "redstone should stop the belt");
		helper.setBlock(belt.south(), Blocks.AIR);
		helper.assertFalse(helper.getBlockState(belt).getValue(IGPoweredBeltBlock.POWERED), "belt should run again");
		helper.succeed();
	}

	private static void leversAttachToClosedSides(GameTestHelper helper)
	{
		BlockPos belt = new BlockPos(12, 1, 3), feeder = belt.west(), lever = belt.east();
		placePowered(helper, belt, Direction.NORTH);
		place(helper, feeder, BeltStyle.STEEL, Direction.EAST);
		expectPowered(helper, belt, BeltShape.FROM_LEFT);
		BlockState leverState = Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACE, AttachFace.WALL).setValue(LeverBlock.FACING, Direction.EAST);
		helper.assertTrue(leverState.canSurvive(helper.getLevel(), helper.absolutePos(lever)), "lever should fit on the switch side");
		BlockState beltState = helper.getBlockState(belt);
		helper.assertTrue(beltState.isFaceSturdy(helper.getLevel(), helper.absolutePos(belt), Direction.EAST), "closed side should hold a lever");
		helper.assertFalse(beltState.isFaceSturdy(helper.getLevel(), helper.absolutePos(belt), Direction.WEST), "the input side must not hold a lever");
		helper.assertFalse(beltState.isFaceSturdy(helper.getLevel(), helper.absolutePos(belt), Direction.UP), "the top must not hold a lever");
		helper.setBlock(lever, leverState);
		helper.setBlock(feeder, Blocks.AIR);
		expectPowered(helper, belt, BeltShape.STRAIGHT);
		BlockState straight = helper.getBlockState(belt);
		helper.assertTrue(straight.isFaceSturdy(helper.getLevel(), helper.absolutePos(belt), Direction.EAST)&&straight.isFaceSturdy(helper.getLevel(), helper.absolutePos(belt), Direction.WEST), "a straight powered belt holds levers on both sides");
		helper.assertBlockPresent(Blocks.LEVER, lever);
		BlockPos side = new BlockPos(12, 1, 8), sideFeeder = side.east(), sideLever = side.west();
		placePowered(helper, side, Direction.NORTH);
		helper.setBlock(sideLever, leverState.setValue(LeverBlock.FACING, Direction.WEST));
		place(helper, sideFeeder, BeltStyle.STEEL, Direction.WEST);
		expectPowered(helper, side, BeltShape.FROM_RIGHT);
		helper.assertBlockPresent(Blocks.LEVER, sideLever);
		helper.succeed();
	}

	private static void wiredBeltsStopTogether(GameTestHelper helper)
	{
		BlockPos a = new BlockPos(2, 1, 2), b = new BlockPos(6, 1, 2);
		placePowered(helper, a, Direction.NORTH);
		placePowered(helper, b, Direction.NORTH);
		helper.runAfterDelay(5, () -> {
			GlobalWireNetwork net = GlobalWireNetwork.getNetwork(helper.getLevel());
			net.addConnection(new Connection(WireType.REDSTONE, new ConnectionPoint(helper.absolutePos(a), 0), new ConnectionPoint(helper.absolutePos(b), 0), net));
			helper.setBlock(a.south(), Blocks.REDSTONE_BLOCK);
			helper.runAfterDelay(10, () -> {
				helper.assertTrue(helper.getBlockState(a).getValue(IGPoweredBeltBlock.POWERED), "the powered belt should stop");
				helper.assertTrue(helper.getBlockState(b).getValue(IGPoweredBeltBlock.POWERED), "the wired belt should stop with it");
				helper.setBlock(a.south(), Blocks.AIR);
				helper.runAfterDelay(10, () -> {
					helper.assertFalse(helper.getBlockState(a).getValue(IGPoweredBeltBlock.POWERED), "the belt should run again");
					helper.assertFalse(helper.getBlockState(b).getValue(IGPoweredBeltBlock.POWERED), "the wired belt should run again");
					helper.succeed();
				});
			});
		});
	}

	private static void coveredBeltsAreLitFromTheirOwnBlock(GameTestHelper helper)
	{
		BlockPos pos = new BlockPos(14, 1, 14);
		for(String key : IGRegistrationHolder.BELT_KEYS)
		{
			Block block = IGRegistrationHolder.getBlock.apply(key);
			for(BlockState state : block.getStateDefinition().getPossibleStates())
			{
				helper.setBlock(pos, state);
				BlockState placed = helper.getBlockState(pos);
				helper.assertFalse(placed.isCollisionShapeFullBlock(helper.getLevel(), helper.absolutePos(pos)), key+" "+placed+" reports a full block, which makes Minecraft light its faces from the neighbours");
			}
		}
		helper.succeed();
	}

	private static void droppingBeltShapesAndPower(GameTestHelper helper)
	{
		BlockPos belt = new BlockPos(3, 1, 8);
		Block dropping = IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL.registryKey("dropping"));
		helper.setBlock(belt, dropping.defaultBlockState().setValue(IGBeltBlock.FACING, Direction.NORTH));
		place(helper, belt.west(), BeltStyle.STEEL, Direction.EAST);
		place(helper, belt.east(), BeltStyle.STEEL, Direction.WEST);
		BlockState state = helper.getBlockState(belt);
		helper.assertTrue(state.getValue(IGDroppingBeltBlock.DROPPING_SHAPE)==BeltShape.FROM_BOTH, "dropping belt should take both sides, was "+state.getValue(IGDroppingBeltBlock.DROPPING_SHAPE));
		helper.assertTrue(IGDroppingBeltBlock.dropsItems(state), "dropping belt should drop while unpowered");
		helper.setBlock(belt.south(), Blocks.REDSTONE_BLOCK);
		helper.assertFalse(IGDroppingBeltBlock.dropsItems(helper.getBlockState(belt)), "power should stop the dropping");
		helper.setBlock(belt.south(), Blocks.AIR);
		helper.assertTrue(IGDroppingBeltBlock.dropsItems(helper.getBlockState(belt)), "dropping should resume");
		helper.succeed();
	}

	private static void extractionFacesAwayFromContainer(GameTestHelper helper)
	{
		BlockPos chest = new BlockPos(8, 1, 8), extraction = chest.east();
		helper.setBlock(chest, Blocks.CHEST);
		Block block = IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL.registryKey("extraction"));
		Player player = helper.makeMockPlayer();
		BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(helper.absolutePos(chest)), Direction.EAST, helper.absolutePos(chest), false);
		BlockState placed = block.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, new ItemStack(block), hit));
		helper.assertTrue(placed!=null&&placed.getValue(IGBeltBlock.FACING)==Direction.EAST, "extraction should face away from the clicked container");
		helper.setBlock(extraction, placed);
		helper.assertTrue(IGExtractionBeltBlock.sourcePos(helper.getBlockState(extraction), helper.absolutePos(extraction)).equals(helper.absolutePos(chest)), "extraction should pull from the chest behind it");
		place(helper, extraction.east(), BeltStyle.STEEL, Direction.EAST);
		place(helper, extraction.north(), BeltStyle.STEEL, Direction.SOUTH);
		expect(helper, extraction.east(), BeltShape.STRAIGHT, BeltRun.SINGLE);
		expect(helper, extraction.north(), BeltShape.STRAIGHT, BeltRun.SINGLE);
		helper.succeed();
	}

	private static BlockState placeSplitter(GameTestHelper helper, BlockPos pos, Direction facing)
	{
		Block block = IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL.registryKey("splitter"));
		Player player = helper.makeMockPlayer();
		player.setYRot(facing.toYRot());
		BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(helper.absolutePos(pos)), Direction.UP, helper.absolutePos(pos), false);
		ItemStack stack = new ItemStack(block);
		BlockState state = block.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, hit));
		if(state==null)
			return null;
		helper.setBlock(pos, state);
		block.setPlacedBy(helper.getLevel(), helper.absolutePos(pos), state, player, stack);
		return state;
	}

	private static void splitterPlacesAndBreaksAsAPair(GameTestHelper helper)
	{
		BlockPos right = new BlockPos(4, 1, 4), left = right.west();
		BlockState placed = placeSplitter(helper, right, Direction.NORTH);
		helper.assertTrue(placed!=null&&placed.getValue(IGSplitterBlock.HALF)==SplitterHalf.RIGHT, "clicked block should become the right half");
		helper.assertTrue(helper.getBlockState(left).getBlock() instanceof IGSplitterBlock&&helper.getBlockState(left).getValue(IGSplitterBlock.HALF)==SplitterHalf.LEFT, "left half should be placed to the west");
		helper.setBlock(left, Blocks.AIR);
		helper.assertBlockNotPresent(placed.getBlock(), right);
		helper.assertTrue(helper.getEntities(net.minecraft.world.entity.EntityType.ITEM, right, 2).stream().mapToInt(e -> ((ItemEntity)e).getItem().getCount()).sum()==1, "breaking one half should drop exactly one splitter");

		BlockPos blocked = new BlockPos(10, 1, 4);
		helper.setBlock(blocked.west(), Blocks.STONE);
		BlockState flipped = placeSplitter(helper, blocked, Direction.NORTH);
		helper.assertTrue(flipped!=null&&flipped.getValue(IGSplitterBlock.HALF)==SplitterHalf.LEFT, "with the left side blocked the clicked block becomes the left half, got "+flipped);
		helper.assertTrue(helper.getBlockState(blocked.east()).getBlock() instanceof IGSplitterBlock, "right half should go east");

		BlockPos boxed = new BlockPos(13, 1, 10);
		helper.setBlock(boxed.west(), Blocks.STONE);
		helper.setBlock(boxed.east(), Blocks.STONE);
		helper.assertTrue(placeSplitter(helper, boxed, Direction.NORTH)==null, "a splitter must not place without room for both halves");
		helper.succeed();
	}

	private static void splitterSettingsAndFilter(GameTestHelper helper)
	{
		BlockPos right = new BlockPos(4, 1, 10);
		placeSplitter(helper, right, Direction.NORTH);
		IGSplitterBlockEntity splitter = (IGSplitterBlockEntity)helper.getBlockEntity(right);
		Player player = helper.makeMockPlayer();
		IGSplitterMenu menu = new IGSplitterMenu(0, player.getInventory(), splitter);
		menu.clickMenuButton(player, IGSplitterMenu.BUTTON_INPUT_BASE+SplitterPriority.LEFT.ordinal());
		menu.clickMenuButton(player, IGSplitterMenu.BUTTON_OUTPUT_BASE+SplitterPriority.RIGHT.ordinal());
		helper.assertTrue(splitter.inputPriority()==SplitterPriority.LEFT&&splitter.outputPriority()==SplitterPriority.RIGHT, "buttons should set the priorities");
		ItemStack held = new ItemStack(Items.IRON_INGOT, 5);
		menu.setCarried(held);
		menu.clicked(0, 0, ClickType.PICKUP, player);
		helper.assertTrue(splitter.filter().is(Items.IRON_INGOT)&&splitter.filter().getCount()==1, "filter should copy the held item");
		helper.assertTrue(menu.getCarried().getCount()==5, "setting the filter must not use up the held item");
		CompoundTag saved = splitter.saveWithoutMetadata();
		IGSplitterBlockEntity reloaded = new IGSplitterBlockEntity(splitter.getBlockPos(), splitter.getBlockState());
		reloaded.load(saved);
		helper.assertTrue(reloaded.inputPriority()==SplitterPriority.LEFT&&reloaded.outputPriority()==SplitterPriority.RIGHT&&reloaded.filter().is(Items.IRON_INGOT), "settings should survive saving");
		menu.setCarried(ItemStack.EMPTY);
		menu.clicked(0, 0, ClickType.PICKUP, player);
		helper.assertFalse(splitter.hasFilter(), "clicking with an empty hand clears the filter");
		helper.assertTrue(IGSplitterBlock.masterPos(helper.getBlockState(right.west()), helper.absolutePos(right.west())).equals(helper.absolutePos(right)), "left half should point at the right half for its settings");
		helper.succeed();
	}

	private static void splitterCreativeBreakDropsNothing(GameTestHelper helper)
	{
		for(boolean breakLeft : new boolean[]{true, false})
		{
			BlockPos right = new BlockPos(breakLeft?4: 10, 1, 13), left = right.west();
			placeSplitter(helper, right, Direction.NORTH);
			Player creative = helper.makeMockPlayer();
			helper.assertTrue(creative.isCreative(), "mock player should be in creative");
			BlockPos broken = breakLeft?left: right;
			BlockState state = helper.getBlockState(broken);
			state.getBlock().playerWillDestroy(helper.getLevel(), helper.absolutePos(broken), state, creative);
			helper.getLevel().removeBlock(helper.absolutePos(broken), false);
			helper.assertBlockNotPresent(state.getBlock(), left);
			helper.assertBlockNotPresent(state.getBlock(), right);
			helper.assertTrue(helper.getEntities(net.minecraft.world.entity.EntityType.ITEM, right, 3).isEmpty(), "creative break of the "+(breakLeft?"left": "right")+" half must not drop anything");
		}
		helper.succeed();
	}

	private static BlockState placeEndergate(GameTestHelper helper, BlockPos pos, Direction facing)
	{
		Block block = IGRegistrationHolder.getBlock.apply(IGRegistrationHolder.ENDERGATE_KEY);
		Player player = helper.makeMockPlayer();
		player.setYRot(facing.toYRot());
		BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(helper.absolutePos(pos)), Direction.UP, helper.absolutePos(pos), false);
		BlockState state = block.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, new ItemStack(block), hit));
		helper.setBlock(pos, state);
		return state;
	}

	private static void endergatePairsWithinRange(GameTestHelper helper)
	{
		IGEndergateBlock block = (IGEndergateBlock)IGRegistrationHolder.getBlock.apply(IGRegistrationHolder.ENDERGATE_KEY);
		int range = block.range();
		BlockPos entry = new BlockPos(2, 1, 14);
		helper.assertTrue(placeEndergate(helper, entry, Direction.NORTH).getValue(IGEndergateBlock.ROLE)==EndergateRole.ENTRY, "first endergate is an entry");

		BlockPos tooFar = entry.north(range+1);
		var farGuide = EndergateLinks.guide(helper.getLevel(), helper.absolutePos(tooFar), Direction.NORTH, range);
		helper.assertTrue(farGuide.isPresent()&&farGuide.get().status()==EndergateLinks.GuideStatus.TOO_FAR, "guide beyond range should be red");
		var maxGuide = EndergateLinks.guide(helper.getLevel(), helper.absolutePos(entry.north(range)), Direction.NORTH, range);
		helper.assertTrue(maxGuide.isPresent()&&maxGuide.get().status()==EndergateLinks.GuideStatus.AT_MAX, "guide at range should be green");
		var nearGuide = EndergateLinks.guide(helper.getLevel(), helper.absolutePos(entry.north(3)), Direction.NORTH, range);
		helper.assertTrue(nearGuide.isPresent()&&nearGuide.get().status()==EndergateLinks.GuideStatus.IN_RANGE&&nearGuide.get().distance()==3, "guide inside range should be yellow");
		helper.assertTrue(EndergateLinks.guide(helper.getLevel(), helper.absolutePos(entry.north(3)), Direction.EAST, range).isEmpty(), "no guide when facing a different way");

		helper.assertTrue(placeEndergate(helper, tooFar, Direction.NORTH).getValue(IGEndergateBlock.ROLE)==EndergateRole.ENTRY, "one past the range starts a new entry");
		helper.setBlock(tooFar, Blocks.AIR);

		BlockPos exit = entry.north(range);
		helper.assertTrue(placeEndergate(helper, exit, Direction.NORTH).getValue(IGEndergateBlock.ROLE)==EndergateRole.EXIT, "at the range it becomes an exit");
		BlockState entryState = helper.getBlockState(entry);
		helper.assertTrue(block.partner(helper.getLevel(), helper.absolutePos(entry), entryState).map(p -> p.equals(helper.absolutePos(exit))).orElse(false), "entry should pair with the exit");
		helper.assertTrue(block.partner(helper.getLevel(), helper.absolutePos(exit), helper.getBlockState(exit)).map(p -> p.equals(helper.absolutePos(entry))).orElse(false), "exit should pair back");
		helper.assertTrue(EndergateLinks.guide(helper.getLevel(), helper.absolutePos(exit.north()), Direction.NORTH, range).isEmpty(), "no guide behind a paired exit");
		helper.succeed();
	}

	private static BlockState placeEndergateWithItem(GameTestHelper helper, ItemStack stack, BlockPos pos, Direction facing, boolean sneaking)
	{
		Player player = helper.makeMockPlayer();
		player.setYRot(facing.toYRot());
		player.setShiftKeyDown(sneaking);
		player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(helper.absolutePos(pos)), Direction.UP, helper.absolutePos(pos), false);
		((net.minecraft.world.item.BlockItem)stack.getItem()).place(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, hit));
		return helper.getBlockState(pos);
	}

	private static void endergateLinkLockAndSneak(GameTestHelper helper)
	{
		Block block = IGRegistrationHolder.getBlock.apply(IGRegistrationHolder.ENDERGATE_KEY);
		ItemStack stack = new ItemStack(block, 8);
		BlockPos entry = new BlockPos(6, 1, 14);
		BlockState placedEntry = placeEndergateWithItem(helper, stack, entry, Direction.NORTH, false);
		helper.assertTrue(placedEntry.getValue(IGEndergateBlock.ROLE)==EndergateRole.ENTRY, "first placement is an entry");
		helper.assertTrue(stack.hasTag()&&stack.getTag().contains(EndergateLinks.LINK_TAG), "the stack should remember the entry");

		BlockState sneaked = placeEndergateWithItem(helper, stack, entry.north(2), Direction.EAST, true);
		helper.assertTrue(sneaked.getValue(IGEndergateBlock.ROLE)==EndergateRole.ENTRY&&sneaked.getValue(IGEndergateBlock.FACING)==Direction.EAST, "sneaking ignores the lock, got "+sneaked);
		helper.setBlock(entry.north(2), Blocks.AIR);
		EndergateLinks.remember(stack, helper.getLevel(), helper.absolutePos(entry), Direction.NORTH);

		BlockState exit = placeEndergateWithItem(helper, stack, entry.north(4), Direction.EAST, false);
		helper.assertTrue(exit.getValue(IGEndergateBlock.ROLE)==EndergateRole.EXIT&&exit.getValue(IGEndergateBlock.FACING)==Direction.NORTH, "facing sideways should still place the linked exit, got "+exit);
		helper.assertFalse(stack.hasTag()&&stack.getTag().contains(EndergateLinks.LINK_TAG), "placing the exit should clear the lock");
		helper.succeed();
	}

	private static void clickWithBelt(GameTestHelper helper, String key, BlockPos clicked, Direction face, Direction looking, boolean sneaking)
	{
		Block block = IGRegistrationHolder.getBlock.apply(key);
		ItemStack stack = new ItemStack(block, 16);
		Player player = helper.makeMockPlayer();
		player.setYRot(looking.toYRot());
		player.setShiftKeyDown(sneaking);
		player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(helper.absolutePos(clicked)), face, helper.absolutePos(clicked), false);
		((net.minecraft.world.item.BlockItem)stack.getItem()).place(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, hit));
	}

	private static void beltsExtendLikeScaffolding(GameTestHelper helper)
	{
		String belt = BeltStyle.STEEL.registryKey("");
		BlockPos a = new BlockPos(3, 1, 12), b = a.north();
		place(helper, a, BeltStyle.STEEL, Direction.NORTH);
		place(helper, b, BeltStyle.STEEL, Direction.NORTH);
		clickWithBelt(helper, belt, a, Direction.UP, Direction.EAST, false);
		BlockState extended = helper.getBlockState(b.north());
		helper.assertTrue(extended.getBlock() instanceof IGBeltBlock&&extended.getValue(IGBeltBlock.FACING)==Direction.NORTH, "clicking the line should extend it forward facing the same way, got "+extended);
		helper.assertTrue(helper.getBlockState(a.above()).isAir(), "nothing should be placed on top");

		clickWithBelt(helper, BeltStyle.STEEL.registryKey("powered"), a, Direction.UP, Direction.EAST, false);
		helper.assertTrue(helper.getBlockState(b.north(2)).getBlock() instanceof IGPoweredBeltBlock, "other line belts extend the line too");

		clickWithBelt(helper, belt, a, Direction.UP, Direction.EAST, true);
		BlockState sneaked = helper.getBlockState(a.above());
		helper.assertTrue(sneaked.getBlock() instanceof IGBeltBlock&&sneaked.getValue(IGBeltBlock.FACING)==Direction.EAST, "sneaking places normally, got "+sneaked);

		BlockPos blocked = new BlockPos(10, 1, 12);
		place(helper, blocked, BeltStyle.STEEL, Direction.NORTH);
		helper.setBlock(blocked.north(), Blocks.STONE);
		clickWithBelt(helper, belt, blocked, Direction.UP, Direction.EAST, false);
		helper.assertTrue(helper.getBlockState(blocked.above()).getBlock() instanceof IGBeltBlock, "a blocked line falls back to normal placement");

		BlockPos riser = new BlockPos(13, 1, 4);
		placeRiser(helper, riser, Direction.NORTH);
		clickWithBelt(helper, belt, riser, Direction.UP, Direction.EAST, false);
		helper.assertTrue(helper.getBlockState(riser.north()).isAir(), "risers are not extended like a line");
		helper.succeed();
	}

	private static IGBeltBlockEntity beltAt(GameTestHelper helper, BlockPos pos)
	{
		return (IGBeltBlockEntity)helper.getBlockEntity(pos);
	}

	private static int count(IGBeltBlockEntity belt, Lane lane)
	{
		return belt.lane(lane).stream().mapToInt(item -> item.stack().getCount()).sum();
	}

	private static void putOnBelt(GameTestHelper helper, BlockPos pos, Lane lane, ItemStack stack)
	{
		IGBeltBlockEntity belt = beltAt(helper, pos);
		IGBeltBlock block = (IGBeltBlock)helper.getBlockState(pos).getBlock();
		belt.insert(lane, 0, stack, block.lanePath(helper.getBlockState(pos), lane, belt.isSideLoad()), false);
	}

	private static void itemsTravelAndFallOffTheEnd(GameTestHelper helper)
	{
		BlockPos first = new BlockPos(2, 1, 12), second = first.north(), third = second.north();
		for(BlockPos pos : List.of(first, second, third))
			place(helper, pos, BeltStyle.STEEL, Direction.NORTH);
		putOnBelt(helper, first, Lane.RIGHT, new ItemStack(Items.IRON_INGOT, 64));
		helper.runAfterDelay(20, () -> helper.assertTrue(count(beltAt(helper, second), Lane.RIGHT)==64, "the stack should be on the second belt's right lane"));
		helper.succeedWhen(() -> {
			helper.assertItemEntityPresent(Items.IRON_INGOT, third.north(), 2);
			helper.assertTrue(beltAt(helper, third).isEmpty(), "the last belt should have dropped the stack");
		});
	}

	private static void beltEndFillsAChest(GameTestHelper helper)
	{
		BlockPos belt = new BlockPos(6, 1, 12), chest = belt.north();
		place(helper, belt, BeltStyle.STEEL, Direction.NORTH);
		helper.setBlock(chest, Blocks.CHEST);
		putOnBelt(helper, belt, Lane.LEFT, new ItemStack(Items.COPPER_INGOT, 40));
		helper.succeedWhen(() -> {
			ChestBlockEntity box = (ChestBlockEntity)helper.getBlockEntity(chest);
			helper.assertTrue(box.countItem(Items.COPPER_INGOT)==40, "the chest should receive the whole stack");
		});
	}

	private static void poweredBeltFreezesItems(GameTestHelper helper)
	{
		BlockPos belt = new BlockPos(10, 1, 12);
		placePowered(helper, belt, Direction.NORTH);
		helper.setBlock(belt.east(), Blocks.REDSTONE_BLOCK);
		putOnBelt(helper, belt, Lane.LEFT, new ItemStack(Items.GOLD_INGOT, 8));
		helper.runAfterDelay(40, () -> {
			float progress = beltAt(helper, belt).lane(Lane.LEFT).get(0).progress();
			helper.assertTrue(progress==0, "a powered belt must not move items, progress was "+progress);
			helper.setBlock(belt.east(), Blocks.AIR);
			helper.succeedWhen(() -> helper.assertItemEntityPresent(Items.GOLD_INGOT, belt.north(), 2));
		});
	}

	private static void hopperExtractionAndDropping(GameTestHelper helper)
	{
		BlockPos hopperBelt = new BlockPos(2, 2, 4);
		place(helper, hopperBelt, BeltStyle.STEEL, Direction.EAST);
		helper.setBlock(hopperBelt.above(), Blocks.HOPPER);
		((HopperBlockEntity)helper.getBlockEntity(hopperBelt.above())).setItem(0, new ItemStack(Items.REDSTONE, 5));
		Block dropping = IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL.registryKey("dropping"));
		BlockPos drop = hopperBelt.east();
		helper.setBlock(drop, dropping.defaultBlockState().setValue(IGBeltBlock.FACING, Direction.EAST));
		helper.setBlock(drop.below(), Blocks.CHEST);

		BlockPos source = new BlockPos(8, 1, 4), extraction = source.east();
		helper.setBlock(source, Blocks.CHEST);
		((ChestBlockEntity)helper.getBlockEntity(source)).setItem(0, new ItemStack(Items.EMERALD, 12));
		Block extractor = IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL.registryKey("extraction"));
		helper.setBlock(extraction, extractor.defaultBlockState().setValue(IGBeltBlock.FACING, Direction.EAST));
		helper.setBlock(extraction.east(), Blocks.CHEST);

		helper.succeedWhen(() -> {
			helper.assertTrue(((ChestBlockEntity)helper.getBlockEntity(drop.below())).countItem(Items.REDSTONE)==5, "hopper items should ride the belt and drop into the chest below");
			helper.assertTrue(((ChestBlockEntity)helper.getBlockEntity(extraction.east())).countItem(Items.EMERALD)==12, "the extraction belt should move the chest's contents along");
		});
	}

	private static void sideLoadUsesTheNearLane(GameTestHelper helper)
	{
		BlockPos main = new BlockPos(12, 1, 6), back = main.south(), side = main.west();
		place(helper, back, BeltStyle.STEEL, Direction.NORTH);
		place(helper, main, BeltStyle.STEEL, Direction.NORTH);
		place(helper, main.north(), BeltStyle.STEEL, Direction.NORTH);
		place(helper, side, BeltStyle.STEEL, Direction.EAST);
		putOnBelt(helper, side, Lane.RIGHT, new ItemStack(Items.DIAMOND, 3));
		helper.succeedWhen(() -> helper.assertTrue(count(beltAt(helper, main.north()), Lane.LEFT)==3, "side-loaded items should end up on the near (left) lane"));
	}

	private static void beltsPickUpDroppedItems(GameTestHelper helper)
	{
		BlockPos belt = new BlockPos(14, 1, 12);
		place(helper, belt, BeltStyle.STEEL, Direction.NORTH);
		helper.spawnItem(Items.APPLE, belt.getX()+.5f, belt.getY()+.5f, belt.getZ()+.5f);
		helper.succeedWhen(() -> helper.assertTrue(beltAt(helper, belt).lane(Lane.LEFT).size()+beltAt(helper, belt).lane(Lane.RIGHT).size() > 0||helper.getEntities(net.minecraft.world.entity.EntityType.ITEM, belt.north(), 1).size() > 0, "the dropped item should be picked up onto the belt"));
	}

	private record SplitterRig(BlockPos left, BlockPos right, BlockPos leftChest, BlockPos rightChest)
	{
	}

	private static SplitterRig splitterRig(GameTestHelper helper, BlockPos right)
	{
		placeSplitter(helper, right, Direction.NORTH);
		BlockPos left = right.west();
		for(BlockPos half : List.of(left, right))
		{
			place(helper, half.north(), BeltStyle.STEEL, Direction.NORTH);
			helper.setBlock(half.north(2), Blocks.CHEST);
		}
		return new SplitterRig(left, right, left.north(2), right.north(2));
	}

	private static int chestCount(GameTestHelper helper, BlockPos chest, net.minecraft.world.item.Item item)
	{
		return ((ChestBlockEntity)helper.getBlockEntity(chest)).countItem(item);
	}

	private static void splitterSplitsStacksEvenly(GameTestHelper helper)
	{
		SplitterRig even = splitterRig(helper, new BlockPos(3, 1, 13));
		putOnBelt(helper, even.right(), Lane.RIGHT, new ItemStack(Items.IRON_INGOT, 64));
		SplitterRig odd = splitterRig(helper, new BlockPos(8, 1, 13));
		putOnBelt(helper, odd.left(), Lane.LEFT, new ItemStack(Items.GOLD_INGOT, 5));
		helper.succeedWhen(() -> {
			helper.assertTrue(chestCount(helper, even.leftChest(), Items.IRON_INGOT)==32&&chestCount(helper, even.rightChest(), Items.IRON_INGOT)==32, "64 should split 32/32");
			int left = chestCount(helper, odd.leftChest(), Items.GOLD_INGOT), right = chestCount(helper, odd.rightChest(), Items.GOLD_INGOT);
			helper.assertTrue(left+right==5&&Math.abs(left-right)==1, "5 should split 2/3, got "+left+"/"+right);
		});
	}

	private static void splitterPriorityAndFilter(GameTestHelper helper)
	{
		SplitterRig priority = splitterRig(helper, new BlockPos(3, 1, 13));
		((IGSplitterBlockEntity)helper.getBlockEntity(priority.right())).setOutputPriority(SplitterPriority.RIGHT);
		putOnBelt(helper, priority.left(), Lane.LEFT, new ItemStack(Items.IRON_INGOT, 64));

		SplitterRig filtered = splitterRig(helper, new BlockPos(8, 1, 13));
		IGSplitterBlockEntity filter = (IGSplitterBlockEntity)helper.getBlockEntity(filtered.right());
		filter.setOutputPriority(SplitterPriority.LEFT);
		filter.setFilter(new ItemStack(Items.DIAMOND));
		putOnBelt(helper, filtered.right(), Lane.LEFT, new ItemStack(Items.DIAMOND, 10));
		putOnBelt(helper, filtered.right(), Lane.RIGHT, new ItemStack(Items.COAL, 10));
		helper.succeedWhen(() -> {
			helper.assertTrue(chestCount(helper, priority.rightChest(), Items.IRON_INGOT)==64&&chestCount(helper, priority.leftChest(), Items.IRON_INGOT)==0, "output priority should send the whole stack right");
			helper.assertTrue(chestCount(helper, filtered.leftChest(), Items.DIAMOND)==10&&chestCount(helper, filtered.rightChest(), Items.COAL)==10, "filtered items go to the filter side, the rest to the other");
			helper.assertTrue(chestCount(helper, filtered.leftChest(), Items.COAL)==0&&chestCount(helper, filtered.rightChest(), Items.DIAMOND)==0, "nothing should cross over");
		});
	}

	private static void splitterFallsBackWhenOneSideIsBlocked(GameTestHelper helper)
	{
		BlockPos right = new BlockPos(5, 1, 13);
		placeSplitter(helper, right, Direction.NORTH);
		BlockPos left = right.west();
		place(helper, left.north(), BeltStyle.STEEL, Direction.NORTH);
		helper.setBlock(left.north(2), Blocks.CHEST);
		helper.setBlock(right.north(), Blocks.STONE);
		putOnBelt(helper, right, Lane.RIGHT, new ItemStack(Items.IRON_INGOT, 64));
		helper.succeedWhen(() -> helper.assertTrue(chestCount(helper, left.north(2), Items.IRON_INGOT)==64, "with the right output blocked everything should go left"));
	}

	private static void rampCollisionClimbsInSmallSteps(GameTestHelper helper)
	{
		for(BlockPos pos : List.of(START, FIRST, SECOND, TOP))
			place(helper, pos, BeltStyle.STEEL, Direction.NORTH);
		expectRamp(helper);
		double previous = -1;
		for(BlockPos pos : List.of(START, FIRST, SECOND, TOP))
		{
			var shape = helper.getBlockState(pos).getCollisionShape(helper.getLevel(), helper.absolutePos(pos));
			for(int band = 15; band >= 0; band--)
			{
				double z = (band+0.5)/16;
				double top = -1;
				for(var box : shape.toAabbs())
					if(z >= box.minZ&&z <= box.maxZ&&box.maxX > 0.5&&box.minX < 0.5)
						top = Math.max(top, box.maxY);
				if(top < 0)
					continue;
				double height = (pos.getY()+top)*16;
				if(previous >= 0)
					helper.assertTrue(height-previous <= 2.01, "step of "+(height-previous)+"px at "+pos+" band "+band);
				previous = Math.max(previous, height);
			}
		}
		helper.succeed();
	}

	private static void brokenBeltDropsItsItems(GameTestHelper helper)
	{
		BlockPos belt = new BlockPos(12, 1, 2);
		placePowered(helper, belt, Direction.NORTH);
		helper.setBlock(belt.east(), Blocks.REDSTONE_BLOCK);
		putOnBelt(helper, belt, Lane.LEFT, new ItemStack(Items.EMERALD, 7));
		helper.setBlock(belt, Blocks.AIR);
		helper.assertItemEntityCountIs(Items.EMERALD, belt, 1.5, 7);
		helper.succeed();
	}

	private static void endergateCarriesItemsUnderground(GameTestHelper helper)
	{
		BlockPos entry = new BlockPos(2, 1, 13), exit = entry.north(4), chest = exit.north();
		placeEndergate(helper, entry, Direction.NORTH);
		placeEndergate(helper, exit, Direction.NORTH);
		helper.setBlock(entry.north(2), Blocks.STONE);
		helper.setBlock(chest, Blocks.CHEST);
		putOnBelt(helper, entry, Lane.LEFT, new ItemStack(Items.IRON_INGOT, 64));
		putOnBelt(helper, entry, Lane.RIGHT, new ItemStack(Items.GOLD_INGOT, 16));

		BlockPos lonely = new BlockPos(10, 1, 13);
		placeEndergate(helper, lonely, Direction.NORTH);
		putOnBelt(helper, lonely, Lane.LEFT, new ItemStack(Items.COAL, 3));
		helper.succeedWhen(() -> {
			helper.assertTrue(chestCount(helper, chest, Items.IRON_INGOT)==64&&chestCount(helper, chest, Items.GOLD_INGOT)==16, "both lanes should arrive through the tunnel");
			helper.assertTrue(count(beltAt(helper, lonely), Lane.LEFT)==3, "an unpaired entry should hold its items");
		});
	}

	private static void endergateTransitDropsWhenBroken(GameTestHelper helper)
	{
		BlockPos entry = new BlockPos(6, 1, 13), exit = entry.north(6);
		placeEndergate(helper, entry, Direction.NORTH);
		placeEndergate(helper, exit, Direction.NORTH);
		putOnBelt(helper, entry, Lane.LEFT, new ItemStack(Items.EMERALD, 9));
		helper.succeedWhen(() -> {
			IGEndergateBlockEntity gate = (IGEndergateBlockEntity)helper.getBlockEntity(entry);
			helper.assertTrue(gate.inTransit(Lane.LEFT)==1, "the stack should be underground");
			helper.setBlock(entry, Blocks.AIR);
			helper.assertItemEntityCountIs(Items.EMERALD, entry, 1.5, 9);
		});
	}

	private static void splitterMovesFilteredItemsMidBody(GameTestHelper helper)
	{
		SplitterRig rig = splitterRig(helper, new BlockPos(3, 1, 13));
		IGSplitterBlockEntity master = (IGSplitterBlockEntity)helper.getBlockEntity(rig.right());
		master.setOutputPriority(SplitterPriority.LEFT);
		master.setFilter(new ItemStack(Items.DIAMOND));
		putOnBelt(helper, rig.left(), Lane.RIGHT, new ItemStack(Items.COAL, 4));
		helper.succeedWhen(() -> {
			var rightLane = beltAt(helper, rig.right()).lane(Lane.RIGHT);
			helper.assertTrue(!rightLane.isEmpty()&&rightLane.get(0).stack().is(Items.COAL)&&rightLane.get(0).progress() < 1, "the coal should be on the right half's lane inside the splitter");
			helper.assertTrue(beltAt(helper, rig.left()).lane(Lane.RIGHT).isEmpty(), "and no longer on the filter half");
		});
	}

	private static void riserDropsAboveItsTop(GameTestHelper helper)
	{
		BlockPos riser = new BlockPos(12, 1, 6);
		placeRiser(helper, riser, Direction.NORTH);
		putOnBelt(helper, riser, Lane.LEFT, new ItemStack(Items.REDSTONE, 6));
		helper.succeedWhen(() -> {
			var dropped = helper.getEntities(net.minecraft.world.entity.EntityType.ITEM, riser.above(), 2);
			helper.assertTrue(!dropped.isEmpty(), "the riser should drop its stack");
			double y = dropped.get(0).getY()-helper.absolutePos(riser).getY();
			helper.assertTrue(y > 0.9, "the stack should leave over the top of the riser, not inside its wall, y offset "+y);
		});
	}

	private static void endergateSideFeedFiltersLanes(GameTestHelper helper)
	{
		BlockPos entry = new BlockPos(4, 1, 13), exit = entry.north(3), chest = exit.north(), feeder = entry.west();
		placeEndergate(helper, entry, Direction.NORTH);
		placeEndergate(helper, exit, Direction.NORTH);
		helper.setBlock(chest, Blocks.CHEST);
		place(helper, feeder, BeltStyle.STEEL, Direction.EAST);
		expect(helper, feeder, BeltShape.TO_RISER_LEFT, BeltRun.SINGLE);
		helper.assertTrue(helper.getBlockState(entry).getValue(IGEndergateBlock.INPUTS)==BeltShape.FROM_LEFT, "the entry should show its left input");
		putOnBelt(helper, feeder, Lane.RIGHT, new ItemStack(Items.IRON_INGOT, 8));
		putOnBelt(helper, feeder, Lane.LEFT, new ItemStack(Items.COAL, 8));
		helper.succeedWhen(() -> {
			helper.assertTrue(chestCount(helper, chest, Items.IRON_INGOT)==8, "the lane beside the belt half should go through");
			helper.assertTrue(chestCount(helper, chest, Items.COAL)==0&&count(beltAt(helper, feeder), Lane.LEFT)==8, "the lane beside the tunnel should be held back");
		});
	}

	private static void splitterPriorityOverflowsWhenBackedUp(GameTestHelper helper)
	{
		BlockPos right = new BlockPos(10, 1, 13), left = right.west();
		placeSplitter(helper, right, Direction.NORTH);
		((IGSplitterBlockEntity)helper.getBlockEntity(right)).setOutputPriority(SplitterPriority.LEFT);
		helper.setBlock(left.north(), Blocks.STONE);
		place(helper, right.north(), BeltStyle.STEEL, Direction.NORTH);
		helper.setBlock(right.north(2), Blocks.CHEST);
		putOnBelt(helper, left, Lane.LEFT, new ItemStack(Items.IRON_INGOT, 64));
		putOnBelt(helper, right, Lane.RIGHT, new ItemStack(Items.GOLD_INGOT, 64));
		helper.succeedWhen(() -> helper.assertTrue(chestCount(helper, right.north(2), Items.IRON_INGOT)==64&&chestCount(helper, right.north(2), Items.GOLD_INGOT)==64, "with the priority side blocked everything should come out on the right"));
	}

	private static void fillChest(GameTestHelper helper, BlockPos pos)
	{
		ChestBlockEntity chest = (ChestBlockEntity)helper.getBlockEntity(pos);
		for(int slot = 0; slot < chest.getContainerSize(); slot++)
			chest.setItem(slot, new ItemStack(Items.DIRT, 64));
	}

	private static void stalledDroppingBeltDropsWhenRoomAppears(GameTestHelper helper)
	{
		BlockPos belt = new BlockPos(6, 2, 6), end = belt.east(), below = belt.below();
		Block dropping = IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL.registryKey("dropping"));
		helper.setBlock(belt, dropping.defaultBlockState().setValue(IGBeltBlock.FACING, Direction.EAST));
		helper.setBlock(end, Blocks.CHEST);
		helper.setBlock(below, Blocks.CHEST);
		fillChest(helper, end);
		fillChest(helper, below);
		putOnBelt(helper, belt, Lane.LEFT, new ItemStack(Items.IRON_INGOT, 16));
		helper.runAfterDelay(40, () -> {
			helper.assertTrue(count(beltAt(helper, belt), Lane.LEFT)==16, "with both chests full the stack should be stalled on the belt");
			((ChestBlockEntity)helper.getBlockEntity(below)).setItem(0, ItemStack.EMPTY);
			helper.succeedWhen(() -> helper.assertTrue(chestCount(helper, below, Items.IRON_INGOT)==16, "the stalled stack should drop once the chest below has room"));
		});
	}

	private static void rightClickTakesAStackOffTheBelt(GameTestHelper helper)
	{
		BlockPos pos = new BlockPos(6, 1, 6);
		place(helper, pos, BeltStyle.STEEL, Direction.EAST);
		putOnBelt(helper, pos, Lane.LEFT, new ItemStack(Items.IRON_INGOT, 20));
		putOnBelt(helper, pos, Lane.RIGHT, new ItemStack(Items.GOLD_INGOT, 5));
		BlockState state = helper.getBlockState(pos);
		IGBeltBlock block = (IGBeltBlock)state.getBlock();
		IGBeltBlockEntity belt = beltAt(helper, pos);
		LanePath path = block.lanePath(state, Lane.LEFT, belt.isSideLoad());
		Vec3 local = BeltShapes.turnPoint(path.point(belt.lane(Lane.LEFT).get(0).progress()), Direction.EAST);
		BlockPos abs = helper.absolutePos(pos);
		Vec3 item = new Vec3(abs.getX()+(local.x+8)/16, abs.getY()+local.y/16, abs.getZ()+(local.z+8)/16);
		Player player = helper.makeMockPlayer();
		player.setPos(item.x, item.y+1-player.getEyeHeight(), item.z);
		player.setXRot(90);
		BlockHitResult hit = new BlockHitResult(item, Direction.UP, abs, false);
		block.use(state, helper.getLevel(), abs, player, InteractionHand.MAIN_HAND, hit);
		helper.assertTrue(belt.lane(Lane.LEFT).isEmpty(), "the clicked stack should leave the belt");
		helper.assertTrue(count(belt, Lane.RIGHT)==5, "the other lane should be untouched");
		helper.assertTrue(BeltLines.count(player.getInventory(), Items.IRON_INGOT)==20, "the whole stack should go to the player");
		helper.succeed();
	}

	private static Player sneakingWith(GameTestHelper helper, ItemStack stack)
	{
		Player player = helper.makeMockPlayer();
		player.setShiftKeyDown(true);
		player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		return player;
	}

	private static void cover(GameTestHelper helper, Player player, BlockPos pos)
	{
		BlockPos abs = helper.absolutePos(pos);
		BeltCoverHandler.onRightClickBlock(new PlayerInteractEvent.RightClickBlock(player, InteractionHand.MAIN_HAND, abs, new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false)));
	}

	private static void uncover(GameTestHelper helper, Player player, BlockPos pos)
	{
		BlockPos abs = helper.absolutePos(pos);
		BlockState state = helper.getBlockState(pos);
		state.use(helper.getLevel(), player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false));
	}

	private static void scaffoldingCoversAndUncoversBelts(GameTestHelper helper)
	{
		BlockPos pos = new BlockPos(6, 1, 6);
		Block covered = belt(BeltStyle.STEEL_COVERED);
		place(helper, pos, BeltStyle.STEEL, Direction.WEST);
		putOnBelt(helper, pos, Lane.LEFT, new ItemStack(Items.IRON_INGOT, 12));
		ItemStack scaffolding = new ItemStack(MetalDecoration.STEEL_SCAFFOLDING.get(MetalScaffoldingType.STANDARD), 4);
		Player player = sneakingWith(helper, scaffolding);
		cover(helper, player, pos);
		BlockState state = helper.getBlockState(pos);
		helper.assertTrue(state.is(covered)&&state.getValue(IGBeltBlock.FACING)==Direction.WEST, "sneak clicking with scaffolding should cover the belt, got "+state);
		helper.assertTrue(scaffolding.getCount()==3, "covering should use one scaffolding");
		helper.assertTrue(count(beltAt(helper, pos), Lane.LEFT)==12, "items on the belt should survive covering");
		helper.assertTrue(helper.getEntities(net.minecraft.world.entity.EntityType.ITEM, pos, 2).isEmpty(), "covering should not drop anything");

		Player empty = sneakingWith(helper, ItemStack.EMPTY);
		uncover(helper, empty, pos);
		state = helper.getBlockState(pos);
		helper.assertTrue(state.is(belt(BeltStyle.STEEL))&&state.getValue(IGBeltBlock.FACING)==Direction.WEST, "sneak clicking with an empty hand should remove the cover, got "+state);
		helper.assertTrue(BeltLines.count(empty.getInventory(), scaffolding.getItem())==1, "removing the cover should give the scaffolding back");
		helper.assertTrue(count(beltAt(helper, pos), Lane.LEFT)==12, "items on the belt should survive uncovering");

		BlockPos powered = new BlockPos(10, 1, 6);
		placePowered(helper, powered, Direction.NORTH);
		cover(helper, player, powered);
		helper.assertTrue(helper.getBlockState(powered).is(IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL_COVERED.registryKey("powered"))), "other belt kinds take a cover too");
		helper.succeed();
	}

	private static void scaffoldingCoversBothSplitterHalves(GameTestHelper helper)
	{
		BlockPos right = new BlockPos(6, 1, 6), left = right.west();
		placeSplitter(helper, right, Direction.NORTH);
		ItemStack scaffolding = new ItemStack(MetalDecoration.STEEL_SCAFFOLDING.get(MetalScaffoldingType.STANDARD), 4);
		cover(helper, sneakingWith(helper, scaffolding), left);
		Block covered = IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL_COVERED.registryKey("splitter"));
		helper.runAfterDelay(2, () -> {
			helper.assertTrue(helper.getBlockState(right).is(covered)&&helper.getBlockState(right).getValue(IGSplitterBlock.HALF)==SplitterHalf.RIGHT, "right half should be covered");
			helper.assertTrue(helper.getBlockState(left).is(covered)&&helper.getBlockState(left).getValue(IGSplitterBlock.HALF)==SplitterHalf.LEFT, "left half should be covered");
			helper.assertTrue(scaffolding.getCount()==3, "a splitter takes a single scaffolding");
			helper.succeed();
		});
	}

	private static Player beltPlacer(GameTestHelper helper, int belts)
	{
		Player player = helper.makeMockPlayer();
		player.setYRot(Direction.NORTH.toYRot());
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(belt(BeltStyle.STEEL), belts));
		return player;
	}

	private static void placeFromHand(GameTestHelper helper, Player player, BlockPos pos)
	{
		BlockPos target = helper.absolutePos(pos);
		ItemStack stack = player.getMainHandItem();
		((net.minecraft.world.item.BlockItem)stack.getItem()).place(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, new BlockHitResult(Vec3.atCenterOf(target), Direction.UP, target, false)));
	}

	private static void linePlacementFillsTheGap(GameTestHelper helper)
	{
		BlockPos start = new BlockPos(3, 1, 12);
		Player player = beltPlacer(helper, 8);
		placeFromHand(helper, player, start);
		placeFromHand(helper, player, start.north(5));
		for(int step = 1; step <= 5; step++)
		{
			BlockState state = helper.getBlockState(start.north(step));
			helper.assertTrue(state.is(belt(BeltStyle.STEEL))&&state.getValue(IGBeltBlock.FACING)==Direction.NORTH, "cell "+step+" of the line should be a belt facing north, got "+state);
		}
		helper.assertTrue(player.getMainHandItem().getCount()==2, "six belts should have been used, left "+player.getMainHandItem().getCount());

		placeFromHand(helper, player, start.north(7));
		helper.assertTrue(helper.getBlockState(start.north(6)).is(belt(BeltStyle.STEEL))&&helper.getBlockState(start.north(7)).is(belt(BeltStyle.STEEL)), "the line continues from the last placed belt");
		helper.assertTrue(player.getMainHandItem().isEmpty(), "the last two belts should be used");

		BlockPos side = new BlockPos(9, 1, 12);
		Player turner = beltPlacer(helper, 8);
		placeFromHand(helper, turner, side);
		turner.setYRot(Direction.SOUTH.toYRot());
		placeFromHand(helper, turner, side.north(3));
		expectLine(helper, side, new int[]{0, 0, 0, 0}, Direction.NORTH);

		BlockPos single = new BlockPos(13, 1, 12);
		Player sneaker = beltPlacer(helper, 8);
		placeFromHand(helper, sneaker, single);
		sneaker.setYRot(Direction.EAST.toYRot());
		sneaker.setShiftKeyDown(true);
		placeFromHand(helper, sneaker, single.north(3));
		helper.assertTrue(helper.getBlockState(single.north()).isAir()&&helper.getBlockState(single.north(3)).getValue(IGBeltBlock.FACING)==Direction.EAST, "sneaking places a single belt facing the player");
		helper.succeed();
	}

	private static void linePlacementNeedsEnoughBelts(GameTestHelper helper)
	{
		BlockPos start = new BlockPos(3, 1, 12);
		Player player = beltPlacer(helper, 3);
		placeFromHand(helper, player, start);
		placeFromHand(helper, player, start.north(5));
		helper.assertTrue(helper.getBlockState(start.north(5)).isAir()&&helper.getBlockState(start.north()).isAir(), "a line longer than the belts carried must not place");
		helper.assertTrue(player.getMainHandItem().getCount()==2, "nothing should be used by a refused line");

		BlockPos blocked = new BlockPos(9, 1, 12);
		Player other = beltPlacer(helper, 16);
		placeFromHand(helper, other, blocked);
		helper.setBlock(blocked.north(2), Blocks.STONE);
		placeFromHand(helper, other, blocked.north(4));
		helper.assertTrue(helper.getBlockState(blocked.north(4)).isAir(), "a blocked line must not place");

		other.setShiftKeyDown(true);
		placeFromHand(helper, other, blocked.north(4));
		helper.assertTrue(helper.getBlockState(blocked.north(4)).is(belt(BeltStyle.STEEL))&&helper.getBlockState(blocked.north()).isAir(), "sneaking places a single belt");
		helper.succeed();
	}

	private static void expectLine(GameTestHelper helper, BlockPos start, int[] heights, Direction facing)
	{
		for(int step = 1; step < heights.length; step++)
		{
			BlockPos cell = start.relative(facing, step).above(heights[step]);
			BlockState state = helper.getBlockState(cell);
			helper.assertTrue(state.is(belt(BeltStyle.STEEL))&&state.getValue(IGBeltBlock.FACING)==facing, "cell "+step+" should be a belt at height "+heights[step]+", got "+state);
		}
	}

	private static void linePlacementClimbsAndDescends(GameTestHelper helper)
	{
		BlockPos up = new BlockPos(3, 1, 12);
		Player climber = beltPlacer(helper, 8);
		placeFromHand(helper, climber, up);
		placeFromHand(helper, climber, up.north(5).above(2));
		expectLine(helper, up, new int[]{0, 0, 0, 0, 1, 2}, Direction.NORTH);
		expect(helper, up.north(2), BeltShape.RAMP_START, BeltRun.SINGLE);
		expect(helper, up.north(3), BeltShape.RAMP, BeltRun.SINGLE);
		expect(helper, up.north(4).above(), BeltShape.RAMP_END, BeltRun.SINGLE);
		helper.assertTrue(climber.getMainHandItem().getCount()==2, "a climbing line costs one belt per cell");

		BlockPos down = new BlockPos(9, 3, 12);
		Player descender = beltPlacer(helper, 8);
		placeFromHand(helper, descender, down);
		placeFromHand(helper, descender, down.north(4).below(2));
		expectLine(helper, down, new int[]{0, -1, -2, -2, -2}, Direction.NORTH);
		helper.assertTrue(helper.getBlockState(down.north()).isAir(), "a descending line drops straight away");

		BlockPos blocked = new BlockPos(14, 1, 12);
		Player stopped = beltPlacer(helper, 8);
		placeFromHand(helper, stopped, blocked);
		helper.setBlock(blocked.north(3).above(), Blocks.STONE);
		placeFromHand(helper, stopped, blocked.north(4).above());
		helper.assertTrue(helper.getBlockState(blocked.north(4).above()).isAir()&&helper.getBlockState(blocked.north()).isAir(), "a slope without room for its ramp must not place");

		BlockPos careful = new BlockPos(3, 1, 4);
		Player stepper = beltPlacer(helper, 8);
		placeFromHand(helper, stepper, careful);
		place(helper, careful.north(), BeltStyle.STEEL, Direction.EAST);
		placeFromHand(helper, stepper, careful.north(3));
		helper.assertTrue(helper.getBlockState(careful.north(2)).isAir(), "a belt right ahead switches the line off");
		BlockPos crossed = new BlockPos(9, 1, 4);
		Player crosser = beltPlacer(helper, 8);
		placeFromHand(helper, crosser, crossed);
		placeRiser(helper, crossed.north(2), Direction.EAST);
		placeFromHand(helper, crosser, crossed.north(4));
		helper.assertTrue(helper.getBlockState(crossed.north()).isAir()&&helper.getBlockState(crossed.north(3)).isAir(), "a line through any belt block stays off");

		BlockPos ledge = new BlockPos(6, 2, 7);
		for(int step = 0; step <= 2; step++)
			helper.setBlock(ledge.north(step).below(), Blocks.STONE);
		Player walker = beltPlacer(helper, 8);
		placeFromHand(helper, walker, ledge);
		placeFromHand(helper, walker, ledge.north(5).below());
		expectLine(helper, ledge, new int[]{0, 0, 0, -1, -1, -1}, Direction.NORTH);
		helper.assertTrue(walker.getMainHandItem().getCount()==2, "a flat run into a slope costs one belt per cell");

		BlockPos steep = new BlockPos(14, 1, 5);
		Player tooSteep = beltPlacer(helper, 8);
		placeFromHand(helper, tooSteep, steep);
		placeFromHand(helper, tooSteep, steep.north(2).above(3));
		helper.assertTrue(helper.getBlockState(steep.north()).isAir(), "a slope steeper than the ramps must not fill");
		helper.succeed();
	}

	private static void coveredRampsCollideAlongTheRoof(GameTestHelper helper)
	{
		Block covered = belt(BeltStyle.STEEL_COVERED);
		BlockPos abs = helper.absolutePos(BlockPos.ZERO);
		BlockState ramp = covered.defaultBlockState().setValue(IGBeltBlock.FACING, Direction.NORTH).setValue(IGBeltBlock.SHAPE, BeltShape.RAMP);
		net.minecraft.world.phys.shapes.VoxelShape shape = ramp.getCollisionShape(helper.getLevel(), abs);
		helper.assertTrue(Math.abs(shape.max(Direction.Axis.Y)-2) < 1e-6, "covered ramps should reach the two block collision limit, got "+shape.max(Direction.Axis.Y));
		double south = shape.toAabbs().stream().filter(box -> box.maxZ > 0.99).mapToDouble(box -> box.maxY).max().orElse(0);
		helper.assertTrue(Math.abs(south-20.47/16) < 1e-3, "the low end of a covered ramp should follow the roof, got "+south*16);
		BlockState start = ramp.setValue(IGBeltBlock.SHAPE, BeltShape.RAMP_START);
		helper.assertTrue(Math.abs(start.getCollisionShape(helper.getLevel(), abs).max(Direction.Axis.Y)-18.47/16) < 1e-3, "a covered ramp start should rise into the roof");
		BlockState down = ramp.setValue(IGBeltBlock.SHAPE, BeltShape.RAMP_DOWN);
		double northDown = down.getCollisionShape(helper.getLevel(), abs).toAabbs().stream().filter(box -> box.minZ < 0.01).mapToDouble(box -> box.maxY).max().orElse(0);
		helper.assertTrue(Math.abs(northDown-20.47/16) < 1e-3, "downhill covered ramps should be low at the front, got "+northDown*16);
		helper.succeed();
	}

	private static void linePlacementCoversFromOffhandScaffolding(GameTestHelper helper)
	{
		Block plain = belt(BeltStyle.STEEL), covered = belt(BeltStyle.STEEL_COVERED);
		BlockPos partial = new BlockPos(3, 1, 12);
		Player short_ = beltPlacer(helper, 8);
		ItemStack few = new ItemStack(MetalDecoration.STEEL_SCAFFOLDING.get(MetalScaffoldingType.STANDARD), 4);
		short_.setItemInHand(InteractionHand.OFF_HAND, few);
		placeFromHand(helper, short_, partial);
		helper.assertTrue(helper.getBlockState(partial).is(covered), "a single placement is covered from the offhand");
		placeFromHand(helper, short_, partial.north(5));
		for(int step = 1; step <= 5; step++)
		{
			BlockState state = helper.getBlockState(partial.north(step));
			Block expected = step <= 3?covered: plain;
			helper.assertTrue(state.is(expected)&&state.getValue(IGBeltBlock.FACING)==Direction.NORTH, "cell "+step+" should be "+expected+", got "+state);
		}
		helper.assertTrue(few.isEmpty(), "all scaffolding should be used");
		helper.assertTrue(short_.getMainHandItem().getCount()==2, "covering does not change the belt cost");

		BlockPos full = new BlockPos(9, 1, 12);
		Player plenty = beltPlacer(helper, 8);
		ItemStack many = new ItemStack(MetalDecoration.STEEL_SCAFFOLDING.get(MetalScaffoldingType.STANDARD), 16);
		plenty.setItemInHand(InteractionHand.OFF_HAND, many);
		placeFromHand(helper, plenty, full);
		placeFromHand(helper, plenty, full.north(4));
		for(int step = 1; step <= 4; step++)
			helper.assertTrue(helper.getBlockState(full.north(step)).is(covered), "cell "+step+" should be covered");
		helper.assertTrue(many.getCount()==11, "one scaffolding per new belt, left "+many.getCount());
		placeFromHand(helper, plenty, full.north(7));
		helper.assertTrue(helper.getBlockState(full.north(6)).is(covered)&&helper.getBlockState(full.north(7)).is(covered), "the line continues from a covered end");
		expect(helper, full.north(4), BeltShape.STRAIGHT, BeltRun.MIDDLE);

		BlockPos splitter = new BlockPos(14, 1, 4);
		Player splitterPlacer = helper.makeMockPlayer();
		splitterPlacer.setYRot(Direction.NORTH.toYRot());
		Block splitterBlock = IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL.registryKey("splitter"));
		splitterPlacer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(splitterBlock));
		ItemStack two = new ItemStack(MetalDecoration.STEEL_SCAFFOLDING.get(MetalScaffoldingType.STANDARD), 3);
		splitterPlacer.setItemInHand(InteractionHand.OFF_HAND, two);
		placeFromHand(helper, splitterPlacer, splitter);
		Block coveredSplitter = IGRegistrationHolder.getBlock.apply(BeltStyle.STEEL_COVERED.registryKey("splitter"));
		helper.assertTrue(helper.getBlockState(splitter).is(coveredSplitter)&&helper.getBlockState(splitter.west()).is(coveredSplitter), "both splitter halves should be covered");
		helper.assertTrue(two.getCount()==2, "a splitter takes a single scaffolding");
		helper.succeed();
	}

	private static void coveredEndergatesPairAndCarry(GameTestHelper helper)
	{
		Block covered = IGRegistrationHolder.getBlock.apply(IGRegistrationHolder.ENDERGATE_COVERED_KEY);
		BlockPos entry = new BlockPos(2, 1, 13), exit = entry.north(4), chest = exit.north();
		placeEndergate(helper, entry, Direction.NORTH);
		placeEndergate(helper, exit, Direction.NORTH);
		ItemStack scaffolding = new ItemStack(MetalDecoration.STEEL_SCAFFOLDING.get(MetalScaffoldingType.STANDARD), 2);
		cover(helper, sneakingWith(helper, scaffolding), entry);
		BlockState coveredEntry = helper.getBlockState(entry);
		helper.assertTrue(coveredEntry.is(covered)&&coveredEntry.getValue(IGEndergateBlock.ROLE)==EndergateRole.ENTRY, "covering keeps the entry role, got "+coveredEntry);
		helper.assertTrue(((IGEndergateBlock)covered).partner(helper.getLevel(), helper.absolutePos(entry), coveredEntry).map(p -> p.equals(helper.absolutePos(exit))).orElse(false), "a covered entry pairs with an uncovered exit");
		helper.assertTrue(Math.abs(coveredEntry.getCollisionShape(helper.getLevel(), helper.absolutePos(entry)).max(Direction.Axis.Y)-15.99/16) < 1e-6, "covered endergates collide as a covered block");
		helper.setBlock(chest, Blocks.CHEST);
		putOnBelt(helper, entry, Lane.LEFT, new ItemStack(Items.IRON_INGOT, 32));
		helper.succeedWhen(() -> helper.assertTrue(chestCount(helper, chest, Items.IRON_INGOT)==32, "items should travel from a covered entry"));
	}

	private static void offhandScaffoldingCoversEveryBeltKind(GameTestHelper helper)
	{
		String[] kinds = {"", "rising", "powered", "dropping", "extraction"};
		int column = 2;
		for(String kind : kinds)
		{
			expectOffhandCover(helper, new BlockPos(column, 1, 12), BeltStyle.STEEL.registryKey(kind), BeltStyle.STEEL_COVERED.registryKey(kind));
			column += 3;
		}
		expectOffhandCover(helper, new BlockPos(2, 1, 6), IGRegistrationHolder.ENDERGATE_KEY, IGRegistrationHolder.ENDERGATE_COVERED_KEY);
		helper.succeed();
	}

	private static void expectOffhandCover(GameTestHelper helper, BlockPos pos, String plainKey, String coveredKey)
	{
		Player player = helper.makeMockPlayer();
		player.setYRot(Direction.NORTH.toYRot());
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(IGRegistrationHolder.getBlock.apply(plainKey), 4));
		ItemStack scaffolding = new ItemStack(MetalDecoration.STEEL_SCAFFOLDING.get(MetalScaffoldingType.STANDARD), 2);
		player.setItemInHand(InteractionHand.OFF_HAND, scaffolding);
		placeFromHand(helper, player, pos);
		BlockState state = helper.getBlockState(pos);
		helper.assertTrue(state.is(IGRegistrationHolder.getBlock.apply(coveredKey)), plainKey+" with scaffolding in the offhand should place "+coveredKey+", got "+state);
		helper.assertTrue(scaffolding.getCount()==1, plainKey+" should use one scaffolding");
	}

	private static void risersStackLikeScaffolding(GameTestHelper helper)
	{
		String riser = BeltStyle.STEEL.registryKey("rising");
		Block riserBlock = IGRegistrationHolder.getBlock.apply(riser);
		BlockPos base = new BlockPos(6, 1, 8);
		placeRiser(helper, base, Direction.NORTH);
		clickWithBelt(helper, riser, base, Direction.EAST, Direction.WEST, false);
		clickWithBelt(helper, riser, base, Direction.UP, Direction.SOUTH, false);
		for(int up = 1; up <= 2; up++)
		{
			BlockState state = helper.getBlockState(base.above(up));
			helper.assertTrue(state.is(riserBlock)&&state.getValue(IGBeltBlock.FACING)==Direction.NORTH, "riser "+up+" should stack on the column facing north, got "+state);
		}
		helper.assertTrue(helper.getBlockState(base.east()).isAir(), "clicking the side of a riser should not place beside it");
		expectRiser(helper, base.above(), RiserShape.STRAIGHT);

		BlockPos capped = new BlockPos(12, 1, 8);
		placeRiser(helper, capped, Direction.NORTH);
		helper.setBlock(capped.above(), Blocks.STONE);
		clickWithBelt(helper, riser, capped, Direction.EAST, Direction.WEST, false);
		helper.assertTrue(helper.getBlockState(capped.east()).is(riserBlock), "a blocked column falls back to normal placement");

		BlockPos sneaked = new BlockPos(3, 1, 3);
		placeRiser(helper, sneaked, Direction.NORTH);
		clickWithBelt(helper, riser, sneaked, Direction.EAST, Direction.WEST, true);
		helper.assertTrue(helper.getBlockState(sneaked.above()).isAir()&&helper.getBlockState(sneaked.east()).is(riserBlock), "sneaking places beside the riser normally");
		helper.succeed();
	}
}
