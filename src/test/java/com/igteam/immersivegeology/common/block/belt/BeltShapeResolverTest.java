package com.igteam.immersivegeology.common.block.belt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.igteam.immersivegeology.common.block.belt.helper.*;
import com.igteam.immersivegeology.common.block.belt.network.BeltNode;
import com.igteam.immersivegeology.common.block.belt.shape.*;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapeResolver.Resolution;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeltShapeResolverTest
{
	private static final String EXAMPLE_LAYOUT = "/helper/example_layout.json";
	private static final double END_PLANE_TOLERANCE = 0.002;
	private static final Path MODELS = Path.of("src/main/resources/assets/immersivegeology/models/block/belt/steel");
	private static final Path COVERED_MODELS = MODELS.resolve("covered");

	private static final class Grid implements BeltView
	{
		final Map<BlockPos, BeltNode> nodes = new HashMap<>();
		final Set<BlockPos> solid = new HashSet<>();

		Grid belt(int x, int y, int z, Direction facing)
		{
			nodes.put(new BlockPos(x, y, z), BeltNode.basic(facing, false));
			return this;
		}

		Grid covered(int x, int y, int z, Direction facing)
		{
			nodes.put(new BlockPos(x, y, z), BeltNode.basic(facing, true));
			return this;
		}

		Grid riser(int x, int y, int z, Direction facing)
		{
			nodes.put(new BlockPos(x, y, z), BeltNode.riser(facing, false));
			return this;
		}

		Grid coveredRiser(int x, int y, int z, Direction facing)
		{
			nodes.put(new BlockPos(x, y, z), BeltNode.riser(facing, true));
			return this;
		}

		RiserShape riserShape(int x, int y, int z)
		{
			return BeltShapeResolver.resolveRiser(this, new BlockPos(x, y, z)).shape();
		}

		Grid solid(int x, int y, int z)
		{
			solid.add(new BlockPos(x, y, z));
			return this;
		}

		@Override
		public BeltNode at(BlockPos pos)
		{
			return nodes.get(pos);
		}

		@Override
		public boolean isOpen(BlockPos pos)
		{
			return !nodes.containsKey(pos)&&!solid.contains(pos);
		}

		Resolution at(int x, int y, int z)
		{
			return BeltShapeResolver.resolve(this, new BlockPos(x, y, z));
		}

		String piece(int x, int y, int z)
		{
			BlockPos pos = new BlockPos(x, y, z);
			BeltNode node = nodes.get(pos);
			if(node.vertical())
			{
				BeltShapeResolver.RiserResolution riser = BeltShapeResolver.resolveRiser(this, pos);
				return BeltModels.riserPiece(node.joinsCoveredRun()?BeltStyle.STEEL_COVERED: BeltStyle.STEEL, riser.shape(), riser.run(), riser.outlet());
			}
			Resolution r = BeltShapeResolver.resolve(this, pos);
			return BeltModels.piece(nodes.get(pos).joinsCoveredRun()?BeltStyle.STEEL_COVERED: BeltStyle.STEEL, r.shape(), r.run(), r.capped());
		}
	}

	@Test
	void loneBeltIsStraight()
	{
		Grid g = new Grid().belt(0, 0, 0, Direction.NORTH);
		assertEquals(new Resolution(BeltShape.STRAIGHT, BeltRun.SINGLE, false, false), g.at(0, 0, 0));
	}

	@Test
	void straightLineStaysStraight()
	{
		Grid g = new Grid().belt(0, 0, 2, Direction.NORTH).belt(0, 0, 1, Direction.NORTH).belt(0, 0, 0, Direction.NORTH);
		for(int z = 0; z < 3; z++)
			assertEquals(BeltShape.STRAIGHT, g.at(0, 0, z).shape());
	}

	@Test
	void turnFromLeftIsNotASideLoad()
	{
		Grid g = new Grid().belt(-1, 0, 0, Direction.EAST).belt(0, 0, 0, Direction.NORTH);
		assertEquals(new Resolution(BeltShape.FROM_LEFT, BeltRun.SINGLE, false, false), g.at(0, 0, 0));
	}

	@Test
	void turnFromRight()
	{
		Grid g = new Grid().belt(1, 0, 0, Direction.WEST).belt(0, 0, 0, Direction.NORTH);
		assertEquals(BeltShape.FROM_RIGHT, g.at(0, 0, 0).shape());
	}

	@Test
	void sideInputWithBackInputIsASideLoad()
	{
		Grid g = new Grid().belt(-1, 0, 0, Direction.EAST).belt(0, 0, 1, Direction.NORTH).belt(0, 0, 0, Direction.NORTH);
		assertEquals(new Resolution(BeltShape.FROM_LEFT, BeltRun.SINGLE, true, false), g.at(0, 0, 0));
	}

	@Test
	void bothSides()
	{
		Grid g = new Grid().belt(-1, 0, 0, Direction.EAST).belt(1, 0, 0, Direction.WEST).belt(0, 0, 0, Direction.NORTH);
		assertEquals(BeltShape.FROM_BOTH, g.at(0, 0, 0).shape());
	}

	@Test
	void beltPointingAwayIsNotAnInput()
	{
		Grid g = new Grid().belt(-1, 0, 0, Direction.WEST).belt(0, 0, 0, Direction.NORTH);
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, 0).shape());
	}

	@Test
	void shortestRampRisesOneBlock()
	{
		Grid g = new Grid()
				.belt(0, 0, 1, Direction.NORTH)
				.belt(0, 0, 0, Direction.NORTH)
				.belt(0, 1, -1, Direction.NORTH);
		assertEquals(BeltShape.RAMP_START, g.at(0, 0, 1).shape());
		assertEquals(BeltShape.RAMP_END, g.at(0, 0, 0).shape());
		assertEquals(BeltShape.STRAIGHT, g.at(0, 1, -1).shape());
		assertEquals(new BlockPos(0, 1, -1), BeltShapeResolver.outputTarget(g, new BlockPos(0, 0, 0)));
	}

	@Test
	void longerRampsChainRampPieces()
	{
		for(int k = 1; k <= 3; k++)
		{
			Grid g = new Grid().belt(0, 0, 1, Direction.NORTH);
			for(int i = 0; i <= k; i++)
				g.belt(0, i, -i, Direction.NORTH);
			g.belt(0, k+1, -k-1, Direction.NORTH);
			assertEquals(BeltShape.RAMP_START, g.at(0, 0, 1).shape());
			for(int i = 0; i < k; i++)
				assertEquals(BeltShape.RAMP, g.at(0, i, -i).shape(), "ramp length "+k+" piece "+i);
			assertEquals(BeltShape.RAMP_END, g.at(0, k, -k).shape());
			assertEquals(BeltShape.STRAIGHT, g.at(0, k+1, -k-1).shape());
		}
	}

	@Test
	void downhillMirrorsUphill()
	{
		Grid g = new Grid()
				.belt(0, 2, -3, Direction.SOUTH)
				.belt(0, 1, -2, Direction.SOUTH)
				.belt(0, 0, -1, Direction.SOUTH)
				.belt(0, 0, 0, Direction.SOUTH)
				.belt(0, 0, 1, Direction.SOUTH);
		assertEquals(BeltShape.STRAIGHT, g.at(0, 2, -3).shape());
		assertEquals(BeltShape.RAMP_END_DOWN, g.at(0, 1, -2).shape());
		assertEquals(BeltShape.RAMP_DOWN, g.at(0, 0, -1).shape());
		assertEquals(BeltShape.RAMP_START_DOWN, g.at(0, 0, 0).shape());
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, 1).shape());
		assertEquals(new BlockPos(0, 1, -2), BeltShapeResolver.outputTarget(g, new BlockPos(0, 2, -3)));
	}

	@Test
	void blockedOverhangKeepsTheBeltFlat()
	{
		Grid g = new Grid()
				.belt(0, 0, 1, Direction.NORTH)
				.belt(0, 0, 0, Direction.NORTH)
				.belt(0, 1, -1, Direction.NORTH)
				.solid(0, 1, 0);
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, 1).shape());
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, 0).shape());
	}

	@Test
	void beltAheadOnTheSameLevelPreventsARamp()
	{
		Grid g = new Grid()
				.belt(0, 0, 0, Direction.NORTH)
				.belt(0, 0, -1, Direction.EAST)
				.belt(0, 1, -1, Direction.NORTH);
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, 0).shape());
		assertEquals(BeltShape.FROM_RIGHT, g.at(0, 0, -1).shape());
	}

	@Test
	void coveredRunPositions()
	{
		assertEquals("covered/straight", new Grid().covered(0, 0, 0, Direction.NORTH).piece(0, 0, 0));

		Grid two = new Grid().covered(0, 0, 1, Direction.NORTH).covered(0, 0, 0, Direction.NORTH);
		assertEquals("covered/straight_back", two.piece(0, 0, 1));
		assertEquals("covered/straight_front", two.piece(0, 0, 0));

		Grid three = new Grid().covered(0, 0, 2, Direction.NORTH).covered(0, 0, 1, Direction.NORTH).covered(0, 0, 0, Direction.NORTH);
		assertEquals("covered/straight_back", three.piece(0, 0, 2));
		assertEquals("covered/straight_mid", three.piece(0, 0, 1));
		assertEquals("covered/straight_front", three.piece(0, 0, 0));
	}

	@Test
	void coveredRunIgnoresUncoveredNeighbours()
	{
		Grid g = new Grid().belt(0, 0, 2, Direction.NORTH).covered(0, 0, 1, Direction.NORTH).belt(0, 0, 0, Direction.NORTH);
		assertEquals("covered/straight", g.piece(0, 0, 1));
	}

	@Test
	void coveredRampStartIsEmptyOnlyWhenACoveredBeltLeadsIn()
	{
		Grid capped = new Grid()
				.covered(0, 0, 1, Direction.NORTH)
				.covered(0, 0, 0, Direction.NORTH)
				.covered(0, 1, -1, Direction.NORTH);
		assertEquals("covered/ramp_start", capped.piece(0, 0, 1));

		Grid fed = new Grid()
				.covered(0, 0, 2, Direction.NORTH)
				.covered(0, 0, 1, Direction.NORTH)
				.covered(0, 0, 0, Direction.NORTH)
				.covered(0, 1, -1, Direction.NORTH);
		assertEquals("covered/ramp_start_empty", fed.piece(0, 0, 1));

		Grid uncoveredFeed = new Grid()
				.belt(0, 0, 2, Direction.NORTH)
				.covered(0, 0, 1, Direction.NORTH)
				.covered(0, 0, 0, Direction.NORTH)
				.covered(0, 1, -1, Direction.NORTH);
		assertEquals("covered/ramp_start", uncoveredFeed.piece(0, 0, 1));
	}

	@Test
	void rampWithNothingFeedingItsLowEndIsCapped()
	{
		Grid open = new Grid()
				.belt(0, 0, 0, Direction.NORTH)
				.belt(0, 1, -1, Direction.NORTH)
				.belt(0, 2, -2, Direction.NORTH);
		assertEquals("basic/ramp_capped", open.piece(0, 0, 0));
		assertEquals("basic/ramp_end", open.piece(0, 1, -1));

		Grid fed = new Grid()
				.belt(0, 0, 1, Direction.NORTH)
				.belt(0, 0, 0, Direction.NORTH)
				.belt(0, 1, -1, Direction.NORTH)
				.belt(0, 2, -2, Direction.NORTH);
		assertEquals("basic/ramp", fed.piece(0, 0, 0));
		assertFalse(fed.at(0, 0, 0).capped());
	}

	@Test
	void onlyTheLowestRampOfALongChainIsCapped()
	{
		Grid g = new Grid()
				.belt(0, 0, 0, Direction.NORTH)
				.belt(0, 1, -1, Direction.NORTH)
				.belt(0, 2, -2, Direction.NORTH)
				.belt(0, 3, -3, Direction.NORTH);
		assertTrue(g.at(0, 0, 0).capped());
		assertFalse(g.at(0, 1, -1).capped());
		assertEquals("basic/ramp", g.piece(0, 1, -1));
	}

	@Test
	void coveredOpenRampsUseCappedPieces()
	{
		Grid single = new Grid()
				.covered(0, 0, 0, Direction.NORTH)
				.belt(0, 1, -1, Direction.NORTH)
				.belt(0, 2, -2, Direction.NORTH);
		assertEquals("covered/ramp_capped", single.piece(0, 0, 0));

		Grid run = new Grid()
				.covered(0, 0, 0, Direction.NORTH)
				.covered(0, 1, -1, Direction.NORTH)
				.belt(0, 2, -2, Direction.NORTH);
		assertEquals("covered/ramp_front_capped", run.piece(0, 0, 0));
		assertEquals("covered/ramp_end_front", run.piece(0, 1, -1));
	}

	@Test
	void downhillRampWithNothingBelowIsCapped()
	{
		Grid g = new Grid()
				.belt(0, 2, -3, Direction.SOUTH)
				.belt(0, 1, -2, Direction.SOUTH)
				.belt(0, 0, -1, Direction.SOUTH);
		assertEquals(BeltShape.RAMP_DOWN, g.at(0, 0, -1).shape());
		assertEquals("basic/ramp_capped_reverse", g.piece(0, 0, -1));

		g.belt(0, 0, 0, Direction.SOUTH);
		assertEquals("basic/ramp_reverse", g.piece(0, 0, -1));
	}

	@Test
	void rampValleyBecomesTwoRisingLs()
	{
		Grid g = new Grid()
				.belt(0, 3, 3, Direction.NORTH)
				.belt(0, 2, 2, Direction.NORTH)
				.belt(0, 1, 1, Direction.NORTH)
				.belt(0, 0, 0, Direction.NORTH)
				.belt(0, 0, -1, Direction.NORTH)
				.belt(0, 1, -2, Direction.NORTH)
				.belt(0, 2, -3, Direction.NORTH)
				.belt(0, 3, -4, Direction.NORTH);
		assertEquals(BeltShape.RAMP_DOWN, g.at(0, 1, 1).shape());
		assertEquals(BeltShape.VALLEY_DROP, g.at(0, 0, 0).shape());
		assertEquals(BeltShape.VALLEY_RISE, g.at(0, 0, -1).shape());
		assertEquals(BeltShape.RAMP, g.at(0, 1, -2).shape());
		assertEquals("basic/valley_reverse", g.piece(0, 0, 0));
		assertEquals("basic/valley", g.piece(0, 0, -1));
		assertFalse(g.at(0, 1, 1).capped());
		assertFalse(g.at(0, 1, -2).capped());
	}

	@Test
	void flatValleyUsesTheShortExtension()
	{
		Grid g = new Grid()
				.belt(0, 1, 1, Direction.NORTH)
				.belt(0, 0, 0, Direction.NORTH)
				.belt(0, 0, -1, Direction.NORTH)
				.belt(0, 1, -2, Direction.NORTH);
		assertEquals("basic/valley_end_reverse", g.piece(0, 0, 0));
		assertEquals("basic/valley_end", g.piece(0, 0, -1));
		assertEquals(new BlockPos(0, 0, -1), BeltShapeResolver.outputTarget(g, new BlockPos(0, 0, 0)));
		assertEquals(new BlockPos(0, 1, -2), BeltShapeResolver.outputTarget(g, new BlockPos(0, 0, -1)));
	}

	@Test
	void coveredValleysKeepTheirRamps()
	{
		Grid g = new Grid()
				.covered(0, 1, 1, Direction.NORTH)
				.covered(0, 0, 0, Direction.NORTH)
				.belt(0, 0, -1, Direction.NORTH)
				.belt(0, 1, -2, Direction.NORTH);
		assertEquals(BeltShape.RAMP_END_DOWN, g.at(0, 0, 0).shape());
		assertEquals(BeltShape.RAMP_END, g.at(0, 0, -1).shape());
	}

	@Test
	void loneRiserHasAClosedBase()
	{
		Grid g = new Grid().riser(0, 0, 0, Direction.NORTH);
		assertEquals(RiserShape.BASE, g.riserShape(0, 0, 0));
		assertEquals("rising/base", g.piece(0, 0, 0));
		assertEquals(new BlockPos(0, 1, 0), BeltShapeResolver.outputTarget(g, new BlockPos(0, 0, 0)));
	}

	@Test
	void stackedRisersFormAShaft()
	{
		Grid g = new Grid().riser(0, 0, 0, Direction.NORTH).riser(0, 1, 0, Direction.NORTH).riser(0, 2, 0, Direction.NORTH);
		assertEquals(RiserShape.BASE, g.riserShape(0, 0, 0));
		assertEquals(RiserShape.STRAIGHT, g.riserShape(0, 1, 0));
		assertEquals(RiserShape.STRAIGHT, g.riserShape(0, 2, 0));
	}

	@Test
	void risersFacingDifferentWaysDoNotStack()
	{
		Grid g = new Grid().riser(0, 0, 0, Direction.NORTH).riser(0, 1, 0, Direction.EAST);
		assertEquals(RiserShape.BASE, g.riserShape(0, 1, 0));
	}

	@Test
	void sideFeedsReshapeBothRiserAndFeeder()
	{
		Grid left = new Grid().riser(0, 0, 0, Direction.NORTH).belt(-1, 0, 0, Direction.EAST);
		assertEquals(RiserShape.FROM_LEFT, left.riserShape(0, 0, 0));
		assertEquals(BeltShape.TO_RISER_LEFT, left.at(-1, 0, 0).shape());

		Grid right = new Grid().riser(0, 0, 0, Direction.NORTH).belt(1, 0, 0, Direction.WEST);
		assertEquals(RiserShape.FROM_RIGHT, right.riserShape(0, 0, 0));
		assertEquals(BeltShape.TO_RISER_RIGHT, right.at(1, 0, 0).shape());

		Grid both = new Grid().riser(0, 0, 0, Direction.NORTH).belt(-1, 0, 0, Direction.EAST).belt(1, 0, 0, Direction.WEST);
		assertEquals(RiserShape.FROM_BOTH, both.riserShape(0, 0, 0));
	}

	@Test
	void riserFeederKeepsItsStopperAndOpensTheOtherSide()
	{
		Grid g = new Grid().riser(0, 0, 0, Direction.NORTH).belt(-1, 0, 0, Direction.EAST).belt(-1, 0, 1, Direction.NORTH);
		assertEquals(BeltShape.TO_RISER_LEFT_FROM_RIGHT, g.at(-1, 0, 0).shape());
		assertEquals("basic/to_riser_left_from_right", g.piece(-1, 0, 0));

		Grid mirrored = new Grid().riser(0, 0, 0, Direction.NORTH).belt(1, 0, 0, Direction.WEST).belt(1, 0, 1, Direction.NORTH);
		assertEquals(BeltShape.TO_RISER_RIGHT_FROM_LEFT, mirrored.at(1, 0, 0).shape());

		Grid stopperSide = new Grid().riser(0, 0, 0, Direction.NORTH).belt(-1, 0, 0, Direction.EAST).belt(-1, 0, -1, Direction.SOUTH);
		assertEquals(BeltShape.TO_RISER_LEFT, stopperSide.at(-1, 0, 0).shape());
		assertFalse(stopperSide.at(-1, 0, 0).sideLoad());

		Grid covered = new Grid().riser(0, 0, 0, Direction.NORTH).covered(-1, 0, 0, Direction.EAST).belt(-1, 0, 1, Direction.NORTH);
		assertEquals("covered/from_right", covered.piece(-1, 0, 0));
		assertEquals(BeltShape.FROM_RIGHT, BeltShapeResolver.poweredShape(BeltShape.TO_RISER_LEFT_FROM_RIGHT, BeltShape.STRAIGHT));
	}

	@Test
	void backFeedKeepsTheFeederStraight()
	{
		Grid g = new Grid().riser(0, 0, 0, Direction.NORTH).belt(0, 0, 1, Direction.NORTH);
		assertEquals(RiserShape.CONNECTED, g.riserShape(0, 0, 0));
		assertEquals("rising/connected", g.piece(0, 0, 0));
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, 1).shape());
	}

	@Test
	void shaftCellsAndTheWallRefuseInput()
	{
		Grid shaft = new Grid().riser(0, 0, 0, Direction.NORTH).riser(0, 1, 0, Direction.NORTH).belt(-1, 1, 0, Direction.EAST);
		assertEquals(RiserShape.STRAIGHT, shaft.riserShape(0, 1, 0));
		assertEquals(BeltShape.STRAIGHT, shaft.at(-1, 1, 0).shape());

		Grid wall = new Grid().riser(0, 0, 0, Direction.NORTH).belt(0, 0, -1, Direction.SOUTH);
		assertEquals(BeltShape.STRAIGHT, wall.at(0, 0, -1).shape());
		assertEquals(RiserShape.BASE, wall.riserShape(0, 0, 0));
	}

	@Test
	void beltOnTopTakesTheRiserOutput()
	{
		Grid g = new Grid().riser(0, 0, 0, Direction.NORTH).belt(0, 1, 0, Direction.EAST).belt(0, 1, 1, Direction.NORTH);
		assertEquals(new Resolution(BeltShape.FROM_RIGHT, BeltRun.SINGLE, true, false), g.at(0, 1, 0));
	}

	@Test
	void rampsDoNotClimbIntoRisers()
	{
		Grid g = new Grid().belt(0, 0, 0, Direction.NORTH).riser(0, 1, -1, Direction.NORTH);
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, 0).shape());
	}

	@Test
	void riserTopFeedsABeltContinuingForward()
	{
		Grid g = new Grid().riser(0, 0, 0, Direction.NORTH).riser(0, 1, 0, Direction.NORTH).belt(0, 2, -1, Direction.NORTH);
		assertEquals("rising/straight_outlet", g.piece(0, 1, 0));
		assertEquals("rising/base", g.piece(0, 0, 0));
		assertEquals(new BlockPos(0, 2, -1), BeltShapeResolver.outputTarget(g, new BlockPos(0, 1, 0)));
		assertEquals(BeltShape.STRAIGHT, g.at(0, 2, -1).shape());

		Grid single = new Grid().riser(0, 0, 0, Direction.NORTH).belt(0, 1, -1, Direction.NORTH);
		assertEquals("rising/base_outlet", single.piece(0, 0, 0));
	}

	@Test
	void riserTopFeedsTheSideOfACrossingBelt()
	{
		Grid east = new Grid().riser(0, 0, 0, Direction.NORTH).belt(0, 1, -1, Direction.EAST);
		assertEquals("rising/base_outlet", east.piece(0, 0, 0));
		assertEquals(new BlockPos(0, 1, -1), BeltShapeResolver.outputTarget(east, new BlockPos(0, 0, 0)));
		assertEquals(BeltShape.FROM_RIGHT, east.at(0, 1, -1).shape());

		Grid west = new Grid().riser(0, 0, 0, Direction.NORTH).riser(0, 1, 0, Direction.NORTH).belt(0, 2, -1, Direction.WEST);
		assertEquals("rising/straight_outlet", west.piece(0, 1, 0));
		assertEquals(BeltShape.FROM_LEFT, west.at(0, 2, -1).shape());

		Grid covered = new Grid().riser(0, 0, 0, Direction.NORTH).covered(0, 1, -1, Direction.EAST);
		assertEquals("covered/from_right", covered.piece(0, 1, -1));
	}

	@Test
	void riserNeverFeedsABeltComingBackTowardsIt()
	{
		Grid g = new Grid().riser(0, 0, 0, Direction.NORTH).belt(0, 1, -1, Direction.SOUTH);
		assertFalse(BeltShapeResolver.resolveRiser(g, new BlockPos(0, 0, 0)).outlet());
		assertEquals(BeltShape.STRAIGHT, g.at(0, 1, -1).shape());
	}

	@Test
	void beltDirectlyOnTopWinsOverTheForwardOutlet()
	{
		Grid g = new Grid().riser(0, 0, 0, Direction.NORTH).belt(0, 1, 0, Direction.EAST).belt(0, 1, -1, Direction.NORTH);
		assertFalse(BeltShapeResolver.resolveRiser(g, new BlockPos(0, 0, 0)).outlet());
		assertEquals(new BlockPos(0, 1, 0), BeltShapeResolver.outputTarget(g, new BlockPos(0, 0, 0)));
	}

	@Test
	void forwardOutletNeedsAnOpenTop()
	{
		Grid blocked = new Grid().riser(0, 0, 0, Direction.NORTH).belt(0, 1, -1, Direction.NORTH).solid(0, 1, 0);
		assertFalse(BeltShapeResolver.resolveRiser(blocked, new BlockPos(0, 0, 0)).outlet());
	}

	@Test
	void coveredRisersRunVertically()
	{
		Grid g = new Grid().coveredRiser(0, 0, 0, Direction.NORTH).coveredRiser(0, 1, 0, Direction.NORTH).coveredRiser(0, 2, 0, Direction.NORTH);
		assertEquals("rising_covered/base", g.piece(0, 0, 0));
		assertEquals("rising_covered/straight_mid", g.piece(0, 1, 0));
		assertEquals("rising_covered/straight_front", g.piece(0, 2, 0));

		Grid mixed = new Grid().riser(0, 0, 0, Direction.NORTH).coveredRiser(0, 1, 0, Direction.NORTH).riser(0, 2, 0, Direction.NORTH);
		assertEquals("rising_covered/straight", mixed.piece(0, 1, 0));
	}

	@Test
	void coveredRiserOpensOnlyForACoveredFeeder()
	{
		Grid uncoveredFeed = new Grid().coveredRiser(0, 0, 0, Direction.NORTH).belt(0, 0, 1, Direction.NORTH);
		assertEquals("rising_covered/base", uncoveredFeed.piece(0, 0, 0));

		Grid coveredFeed = new Grid().coveredRiser(0, 0, 0, Direction.NORTH).covered(0, 0, 1, Direction.NORTH);
		assertEquals("rising_covered/connected", coveredFeed.piece(0, 0, 0));

		Grid plainRiser = new Grid().riser(0, 0, 0, Direction.NORTH).covered(0, 0, 1, Direction.NORTH);
		assertEquals("rising/connected", plainRiser.piece(0, 0, 0));
	}

	@Test
	void coveredBeltIntoARiserEndsItsRun()
	{
		Grid g = new Grid().covered(0, 0, 1, Direction.NORTH).coveredRiser(0, 0, 0, Direction.NORTH);
		assertEquals("covered/straight", g.piece(0, 0, 1));
	}

	@Test
	void poweredBeltKeepsTheSideItChoseFirst()
	{
		Grid g = new Grid().belt(0, 0, 0, Direction.NORTH).belt(-1, 0, 0, Direction.EAST).belt(1, 0, 0, Direction.WEST);
		BeltShape resolved = g.at(0, 0, 0).shape();
		assertEquals(BeltShape.FROM_BOTH, resolved);
		assertEquals(BeltShape.FROM_LEFT, BeltShapeResolver.poweredShape(resolved, BeltShape.FROM_LEFT));
		assertEquals(BeltShape.FROM_RIGHT, BeltShapeResolver.poweredShape(resolved, BeltShape.FROM_RIGHT));
		assertEquals(BeltShape.FROM_LEFT, BeltShapeResolver.poweredShape(resolved, BeltShape.STRAIGHT));
		assertEquals(BeltShape.FROM_RIGHT, BeltShapeResolver.poweredShape(BeltShape.FROM_RIGHT, BeltShape.FROM_LEFT));
		assertEquals(BeltShape.STRAIGHT, BeltShapeResolver.poweredShape(BeltShape.TO_RISER_LEFT, BeltShape.STRAIGHT));
		assertEquals(BeltShape.STRAIGHT, BeltShapeResolver.poweredShape(BeltShape.RAMP, BeltShape.STRAIGHT));
	}

	@Test
	void poweredNodesNeverSlope()
	{
		Grid g = new Grid().belt(0, 1, -1, Direction.NORTH);
		g.nodes.put(new BlockPos(0, 0, 0), BeltNode.fixed(Direction.NORTH));
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, 0).shape());
	}

	@Test
	void wirePointRotatesWithTheModel()
	{
		net.minecraft.world.phys.Vec3 canonical = new net.minecraft.world.phys.Vec3(-3.5, 15, 0.5);
		assertEquals(new net.minecraft.world.phys.Vec3(-3.5, 15, 0.5), BeltShapes.turnPoint(canonical, Direction.NORTH));
		assertEquals(new net.minecraft.world.phys.Vec3(-0.5, 15, -3.5), BeltShapes.turnPoint(canonical, Direction.EAST));
		assertEquals(new net.minecraft.world.phys.Vec3(3.5, 15, -0.5), BeltShapes.turnPoint(canonical, Direction.SOUTH));
		assertEquals(new net.minecraft.world.phys.Vec3(0.5, 15, 3.5), BeltShapes.turnPoint(canonical, Direction.WEST));
	}

	@Test
	void extractionTakesNoBeltInputAndFeedsForward()
	{
		Grid g = new Grid().belt(0, 0, 1, Direction.NORTH).belt(-1, 0, 0, Direction.EAST).belt(0, 0, -1, Direction.NORTH);
		g.nodes.put(new BlockPos(0, 0, 0), BeltNode.extraction(Direction.NORTH));
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, 0).shape());
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, -1).shape());
		assertEquals(new BlockPos(0, 0, -1), BeltShapeResolver.outputTarget(g, new BlockPos(0, 0, 0)));
		Grid ramp = new Grid().belt(0, 0, 1, Direction.NORTH).belt(0, 1, 0, Direction.NORTH);
		ramp.nodes.put(new BlockPos(0, 1, 0), BeltNode.extraction(Direction.NORTH));
		assertEquals(BeltShape.STRAIGHT, ramp.at(0, 0, 1).shape());
	}

	@Test
	void splitterHalvesTakeFromBehindAndRefuseSides()
	{
		Grid g = new Grid().belt(0, 0, 1, Direction.NORTH).belt(-1, 0, 0, Direction.EAST).belt(0, 0, -1, Direction.NORTH);
		g.nodes.put(new BlockPos(0, 0, 0), BeltNode.splitter(Direction.NORTH));
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, 0).shape());
		assertEquals(new BlockPos(0, 0, 0), BeltShapeResolver.outputTarget(g, new BlockPos(0, 0, 1)));
		assertEquals(new BlockPos(0, 0, -1), BeltShapeResolver.outputTarget(g, new BlockPos(0, 0, 0)));
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, -1).shape());
	}

	@Test
	void endergateEntryFeedsNothingInFrontAndExitRefusesTheTunnelSide()
	{
		Grid g = new Grid().belt(0, 0, 1, Direction.NORTH).belt(-1, 0, 0, Direction.EAST).belt(0, 0, -1, Direction.EAST);
		g.nodes.put(new BlockPos(0, 0, 0), BeltNode.endergateEntry(Direction.NORTH));
		assertEquals(new BlockPos(0, 0, 0), BeltShapeResolver.outputTarget(g, new BlockPos(0, 0, 0)));
		assertEquals(BeltShape.FROM_LEFT, g.at(0, 0, 0).shape());
		assertEquals(BeltShape.STRAIGHT, g.at(0, 0, -1).shape());

		Grid exit = new Grid().belt(0, 0, 1, Direction.NORTH).belt(-1, 0, 0, Direction.EAST).belt(0, 0, -1, Direction.NORTH);
		exit.nodes.put(new BlockPos(0, 0, 0), BeltNode.endergateExit(Direction.NORTH));
		assertEquals(BeltShape.FROM_LEFT, exit.at(0, 0, 0).shape());
		assertFalse(exit.at(0, 0, 0).sideLoad());
		assertEquals(new BlockPos(0, 0, -1), BeltShapeResolver.outputTarget(exit, new BlockPos(0, 0, 0)));
	}

	@Test
	void sideFeedersIntoEndergatesGetTheTunnelStopper()
	{
		Grid entry = new Grid().belt(-1, 0, 0, Direction.EAST).belt(1, 0, 0, Direction.WEST);
		entry.nodes.put(new BlockPos(0, 0, 0), BeltNode.endergateEntry(Direction.NORTH));
		assertEquals(BeltShape.TO_RISER_LEFT, entry.at(-1, 0, 0).shape());
		assertEquals(BeltShape.TO_RISER_RIGHT, entry.at(1, 0, 0).shape());
		assertEquals(BeltShape.FROM_BOTH, entry.at(0, 0, 0).shape().sideInputs());

		Grid exit = new Grid().belt(-1, 0, 0, Direction.EAST);
		exit.nodes.put(new BlockPos(0, 0, 0), BeltNode.endergateExit(Direction.NORTH));
		assertEquals(BeltShape.TO_RISER_RIGHT, exit.at(-1, 0, 0).shape());
	}

	@Test
	void endergateGuideColours()
	{
		assertEquals(EndergateLinks.GuideStatus.IN_RANGE, EndergateLinks.status(1, 6));
		assertEquals(EndergateLinks.GuideStatus.IN_RANGE, EndergateLinks.status(5, 6));
		assertEquals(EndergateLinks.GuideStatus.AT_MAX, EndergateLinks.status(6, 6));
		assertEquals(EndergateLinks.GuideStatus.TOO_FAR, EndergateLinks.status(7, 6));
	}

	@Test
	void everyMappedPieceHasAnExportedModel()
	{
		for(BeltStyle style : new BeltStyle[]{BeltStyle.STEEL, BeltStyle.STEEL_COVERED})
			for(BeltShape shape : BeltShape.values())
				for(BeltRun run : BeltRun.values())
					for(boolean capped : new boolean[]{false, true})
					{
						String piece = BeltModels.piece(style, shape, run, capped);
						assertTrue(Files.exists(MODELS.resolve(piece+".obj")), piece);
					}
		for(BeltStyle style : new BeltStyle[]{BeltStyle.STEEL, BeltStyle.STEEL_COVERED})
		{
			assertTrue(Files.exists(MODELS.resolve(BeltModels.splitterPiece(style)+".obj")), BeltModels.splitterPiece(style));
			for(EndergateRole role : EndergateRole.values())
				for(BeltShape inputs : new BeltShape[]{BeltShape.STRAIGHT, BeltShape.FROM_LEFT, BeltShape.FROM_RIGHT, BeltShape.FROM_BOTH})
					assertTrue(Files.exists(MODELS.resolve(BeltModels.endergatePiece(style, role, inputs)+".obj")), BeltModels.endergatePiece(style, role, inputs));
			assertTrue(Files.exists(MODELS.resolve(BeltModels.extractionPiece(style)+".obj")), BeltModels.extractionPiece(style));
			for(BeltShape shape : new BeltShape[]{BeltShape.STRAIGHT, BeltShape.FROM_LEFT, BeltShape.FROM_RIGHT, BeltShape.FROM_BOTH})
			{
				String piece = BeltModels.droppingPiece(style, shape);
				assertTrue(Files.exists(MODELS.resolve(piece+".obj")), piece);
			}
		}
		for(BeltStyle style : new BeltStyle[]{BeltStyle.STEEL, BeltStyle.STEEL_COVERED})
			for(BeltShape shape : new BeltShape[]{BeltShape.STRAIGHT, BeltShape.FROM_LEFT, BeltShape.FROM_RIGHT})
			{
				String piece = BeltModels.poweredPiece(style, shape);
				assertTrue(Files.exists(MODELS.resolve(piece+".obj")), piece);
			}
		for(BeltStyle style : new BeltStyle[]{BeltStyle.STEEL, BeltStyle.STEEL_COVERED})
			for(RiserShape shape : RiserShape.values())
				for(BeltRun run : BeltRun.values())
					for(boolean outlet : new boolean[]{false, true})
					{
						String piece = BeltModels.riserPiece(style, shape, run, outlet);
						assertTrue(Files.exists(MODELS.resolve(piece+".obj")), piece);
					}
	}

	@Test
	void reachCoversEveryDependency()
	{
		Random random = new Random(1234);
		Direction[] facings = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
		for(int trial = 0; trial < 300; trial++)
		{
			Grid g = new Grid();
			for(int i = 0; i < 40; i++)
			{
				int x = random.nextInt(7)-3, y = random.nextInt(4), z = random.nextInt(7)-3;
				if(random.nextInt(6)==0)
					g.solid(x, y, z);
				else if(random.nextInt(5)==0)
					g.nodes.put(new BlockPos(x, y, z), BeltNode.riser(facings[random.nextInt(4)], random.nextBoolean()));
				else if(random.nextBoolean())
					g.covered(x, y, z, facings[random.nextInt(4)]);
				else
					g.belt(x, y, z, facings[random.nextInt(4)]);
			}
			g.solid.removeAll(g.nodes.keySet());
			for(BlockPos pos : new ArrayList<>(g.nodes.keySet()))
			{
				Grid local = new Grid();
				int h = BeltShapeResolver.REACH_HORIZONTAL, v = BeltShapeResolver.REACH_VERTICAL;
				g.nodes.forEach((p, n) -> {
					if(within(p, pos, h, v))
						local.nodes.put(p, n);
				});
				g.solid.forEach(p -> {
					if(within(p, pos, h, v))
						local.solid.add(p);
				});
				assertEquals(BeltShapeResolver.resolve(g, pos), BeltShapeResolver.resolve(local, pos), "trial "+trial+" at "+pos);
				assertEquals(BeltShapeResolver.resolveRiser(g, pos), BeltShapeResolver.resolveRiser(local, pos), "riser trial "+trial+" at "+pos);
			}
		}
	}

	private static boolean within(BlockPos p, BlockPos centre, int h, int v)
	{
		return Math.abs(p.getX()-centre.getX()) <= h&&Math.abs(p.getZ()-centre.getZ()) <= h&&Math.abs(p.getY()-centre.getY()) <= v;
	}

	@Test
	void exampleLayoutFromTheArtResolvesToItsPieces() throws IOException
	{
		JsonArray layout;
		try(InputStream stream = BeltShapeResolverTest.class.getResourceAsStream(EXAMPLE_LAYOUT))
		{
			assertNotNull(stream, EXAMPLE_LAYOUT);
			layout = JsonParser.parseString(new String(stream.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonArray();
		}
		Grid g = new Grid();
		Map<BlockPos, String> expected = new HashMap<>();
		for(JsonElement element : layout)
		{
			JsonObject entry = element.getAsJsonObject();
			JsonArray p = entry.getAsJsonArray("pos");
			BlockPos pos = new BlockPos(p.get(0).getAsInt(), p.get(1).getAsInt(), p.get(2).getAsInt());
			Direction facing = Direction.byName(entry.get("facing").getAsString());
			String family = entry.get("family").getAsString();
			String piece = entry.get("piece").getAsString();
			switch(family)
			{
				case "basic" -> {
					g.nodes.put(pos, BeltNode.basic(facing, false));
					expected.put(pos, family+"/"+piece);
				}
				case "covered" -> {
					g.nodes.put(pos, BeltNode.basic(facing, true));
					expected.put(pos, family+"/"+piece);
				}
				case "splitter" -> {
					g.nodes.put(pos, BeltNode.fixed(facing));
					g.nodes.put(pos.relative(facing.getCounterClockWise()), BeltNode.fixed(facing));
				}
				case "rising" -> {
					g.nodes.put(pos, BeltNode.riser(facing, false));
					expected.put(pos, family+"/"+piece);
				}
				default -> g.nodes.put(pos, BeltNode.fixed(facing));
			}
		}
		assertFalse(expected.isEmpty());
		expected.forEach((pos, piece) -> assertEquals(piece, g.piece(pos.getX(), pos.getY(), pos.getZ()), "example piece at "+pos));
	}

	@Test
	void coveredPiecesFrameEveryExposedEnd() throws IOException
	{
		BeltShape[] shapes = {BeltShape.STRAIGHT, BeltShape.RAMP_START, BeltShape.RAMP, BeltShape.RAMP_END,
				BeltShape.RAMP_START_DOWN, BeltShape.RAMP_DOWN, BeltShape.RAMP_END_DOWN};
		for(BeltShape shape : shapes)
			for(BeltRun run : BeltRun.values())
			{
				String piece = BeltModels.piece(BeltStyle.STEEL_COVERED, shape, run, false);
				double[] frames = endFrames(COVERED_MODELS.resolve(piece.substring(piece.indexOf('/')+1)+".obj"));
				boolean outputFramed = frames[0] > 100, inputFramed = frames[1] > 100;
				String context = shape+" "+run+" -> "+piece;
				boolean knownGap = shape.uphill()==BeltShape.RAMP_END&&(shape.isDownhill()?run==BeltRun.END: run==BeltRun.START);
				boolean startPiece = shape.uphill()==BeltShape.RAMP_START;
				boolean outputEndOfStartPieceIsTheRise = startPiece&&!shape.isDownhill();
				boolean inputEndOfStartPieceIsTheRise = startPiece&&shape.isDownhill();
				if(!outputEndOfStartPieceIsTheRise&&!(knownGap&&shape.isDownhill()))
					assertEquals(!run.coveredAhead(), outputFramed, context+" output end");
				if(!inputEndOfStartPieceIsTheRise&&!(knownGap&&!shape.isDownhill()))
					assertEquals(!run.coveredBehind(), inputFramed, context+" input end");
			}
	}

	private static double[] endFrames(Path obj) throws IOException
	{
		assertTrue(Files.exists(obj), obj.toString());
		List<double[]> vertices = new ArrayList<>();
		double north = 0, south = 0;
		for(String line : Files.readAllLines(obj))
		{
			String[] parts = line.trim().split("\\s+");
			if(parts[0].equals("v"))
				vertices.add(new double[]{Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3])});
			else if(parts[0].equals("f"))
			{
				double minX = 1, maxX = 0, minY = 1, maxY = 0, minZ = 1, maxZ = 0;
				for(int i = 1; i < parts.length; i++)
				{
					double[] v = vertices.get(Integer.parseInt(parts[i].split("/")[0])-1);
					minX = Math.min(minX, v[0]);
					maxX = Math.max(maxX, v[0]);
					minY = Math.min(minY, v[1]);
					maxY = Math.max(maxY, v[1]);
					minZ = Math.min(minZ, v[2]);
					maxZ = Math.max(maxZ, v[2]);
				}
				double area = (maxX-minX)*(maxY-minY)*128;
				if(maxZ-minZ < 1e-4&&minZ < END_PLANE_TOLERANCE)
					north += area;
				else if(maxZ-minZ < 1e-4&&minZ > 1-END_PLANE_TOLERANCE)
					south += area;
			}
		}
		return new double[]{north, south};
	}
}
