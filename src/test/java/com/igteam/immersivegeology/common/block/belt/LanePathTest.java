package com.igteam.immersivegeology.common.block.belt;

import com.igteam.immersivegeology.common.block.belt.shape.BeltPaths;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShape;
import com.igteam.immersivegeology.common.block.belt.network.LanePath;
import com.igteam.immersivegeology.common.block.belt.shape.RiserShape;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LanePathTest
{
	private static final double EPSILON = 1e-3;

	private static void assertPoint(Vec3 expected, Vec3 actual, String context)
	{
		assertTrue(expected.distanceTo(actual) < EPSILON, context+": expected "+expected+" but was "+actual);
	}

	private static Vec3 start(LanePath path)
	{
		return path.point(0);
	}

	private static Vec3 end(LanePath path)
	{
		return path.point(1);
	}

	private static Vec3 nextBlock(Vec3 point, int dy)
	{
		return new Vec3(point.x, point.y-16*dy, point.z+16);
	}

	@Test
	void straightRunsTheLengthOfTheBlockOnItsLane()
	{
		for(Lane lane : Lane.values())
		{
			LanePath path = BeltPaths.forShape(BeltShape.STRAIGHT, lane, false);
			assertEquals(16, path.length(), EPSILON);
			assertPoint(new Vec3(lane.offset(), LanePath.SURFACE, 8), start(path), "start");
			assertPoint(new Vec3(lane.offset(), LanePath.SURFACE, -8), end(path), "end");
		}
	}

	@Test
	void turnsEnterFromTheSideAndLeaveOnTheirLane()
	{
		for(Lane lane : Lane.values())
		{
			LanePath left = BeltPaths.forShape(BeltShape.FROM_LEFT, lane, false);
			assertEquals(-8, start(left).x, EPSILON);
			assertPoint(new Vec3(lane.offset(), LanePath.SURFACE, -8), end(left), "left turn exit");
			LanePath right = BeltPaths.forShape(BeltShape.FROM_RIGHT, lane, false);
			assertEquals(8, start(right).x, EPSILON);
			assertPoint(new Vec3(lane.offset(), LanePath.SURFACE, -8), end(right), "right turn exit");
		}
		assertTrue(BeltPaths.forShape(BeltShape.FROM_LEFT, Lane.LEFT, false).length() < BeltPaths.forShape(BeltShape.FROM_LEFT, Lane.RIGHT, false).length(), "the inner lane of a left turn is shorter");
		assertEquals(16, BeltPaths.forShape(BeltShape.FROM_LEFT, Lane.LEFT, true).length(), EPSILON, "a side-load keeps the straight path");
	}

	@Test
	void rampChainsJoinWithoutJumps()
	{
		Lane lane = Lane.LEFT;
		LanePath start = BeltPaths.forShape(BeltShape.RAMP_START, lane, false);
		LanePath ramp = BeltPaths.forShape(BeltShape.RAMP, lane, false);
		LanePath end = BeltPaths.forShape(BeltShape.RAMP_END, lane, false);
		LanePath flat = BeltPaths.forShape(BeltShape.STRAIGHT, lane, false);
		assertPoint(start(ramp), nextBlock(end(start), 0), "ramp start into ramp");
		assertPoint(start(ramp), nextBlock(end(ramp), 1), "ramp into ramp one block up");
		assertPoint(start(end), nextBlock(end(ramp), 1), "ramp into ramp end one block up");
		assertPoint(start(flat), nextBlock(end(end), 1), "ramp end onto the flat one block up");
	}

	@Test
	void downhillChainsJoinWithoutJumps()
	{
		Lane lane = Lane.RIGHT;
		LanePath flat = BeltPaths.forShape(BeltShape.STRAIGHT, lane, false);
		LanePath top = BeltPaths.forShape(BeltShape.RAMP_END_DOWN, lane, false);
		LanePath ramp = BeltPaths.forShape(BeltShape.RAMP_DOWN, lane, false);
		LanePath bottom = BeltPaths.forShape(BeltShape.RAMP_START_DOWN, lane, false);
		assertPoint(start(top), new Vec3(end(flat).x, end(flat).y+16, end(flat).z+16), "flat onto the downhill top a block lower");
		assertPoint(start(ramp), nextBlock(end(top), -1), "top into ramp a block lower");
		assertPoint(start(bottom), nextBlock(end(ramp), 0), "ramp into the bottom bend");
		assertPoint(start(flat), nextBlock(end(bottom), 0), "bottom bend onto the flat");
	}

	@Test
	void risersClimbTheWallAndOutletsReachTheNextBelt()
	{
		LanePath bottom = BeltPaths.forRiser(RiserShape.CONNECTED, false, Lane.LEFT);
		LanePath shaft = BeltPaths.forRiser(RiserShape.STRAIGHT, false, Lane.LEFT);
		assertPoint(start(shaft), new Vec3(end(bottom).x, end(bottom).y-16, end(bottom).z), "bottom into the shaft above");
		assertTrue(bottom.cornerProgress() > 0&&bottom.cornerProgress() < 1, "side input enters at the foot of the wall");
		LanePath outlet = BeltPaths.forRiser(RiserShape.STRAIGHT, true, Lane.LEFT);
		assertEquals(16+LanePath.SURFACE, end(outlet).y, EPSILON, "an outlet climbs to the next belt's surface");
	}

	@Test
	void valleysMeetTheirNeighbours()
	{
		LanePath drop = BeltPaths.forShape(BeltShape.VALLEY_DROP, Lane.LEFT, false);
		LanePath rise = BeltPaths.forShape(BeltShape.VALLEY_RISE, Lane.LEFT, false);
		LanePath ramp = BeltPaths.forShape(BeltShape.RAMP, Lane.LEFT, false);
		assertPoint(start(rise), nextBlock(end(drop), 0), "drop L into rise L");
		assertEquals(start(ramp).y+16, end(rise).y, EPSILON, "rise L tops out at the next ramp's entry height");
	}
}
