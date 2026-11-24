/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.shape;

import com.igteam.immersivegeology.common.block.belt.helper.BeltRun;
import com.igteam.immersivegeology.common.block.belt.helper.BeltView;
import com.igteam.immersivegeology.common.block.belt.network.BeltNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public final class BeltShapeResolver
{
	public static final int REACH_HORIZONTAL = 2;
	public static final int REACH_VERTICAL = 3;

	private BeltShapeResolver()
	{
	}

	public record Resolution(BeltShape shape, BeltRun run, boolean sideLoad, boolean capped)
	{
	}

	public record RiserResolution(RiserShape shape, BeltRun run, boolean outlet)
	{
	}

	public static RiserResolution resolveRiser(BeltView view, BlockPos pos)
	{
		BeltNode node = view.at(pos);
		if(node==null)
			return new RiserResolution(RiserShape.BASE, BeltRun.SINGLE, false);
		BeltNode below = view.at(pos.below());
		boolean stacked = stacksOn(node, below);
		RiserShape shape;
		if(stacked)
			shape = RiserShape.STRAIGHT;
		else
		{
			boolean left = feeds(view, pos.relative(node.left()), pos);
			boolean right = feeds(view, pos.relative(node.right()), pos);
			BeltNode feeder = straightFeeder(view, pos, node);
			boolean back = feeder!=null&&(!node.joinsCoveredRun()||(feeder.joinsCoveredRun()&&!feeder.vertical()));
			shape = left&&right?RiserShape.FROM_BOTH: left?RiserShape.FROM_LEFT: right?RiserShape.FROM_RIGHT: back?RiserShape.CONNECTED: RiserShape.BASE;
		}
		BeltRun run = BeltRun.SINGLE;
		if(node.joinsCoveredRun())
		{
			BeltNode above = view.at(pos.above());
			run = BeltRun.of(stacked&&below.joinsCoveredRun(), stacksOn(above, node)&&above.joinsCoveredRun());
		}
		return new RiserResolution(shape, run, feedsForward(view, pos, node));
	}

	private static boolean feedsForward(BeltView view, BlockPos pos, BeltNode riser)
	{
		if(view.at(pos.above())!=null||!view.isOpen(pos.above()))
			return false;
		BeltNode next = view.at(pos.relative(riser.facing()).above());
		if(next==null||next.vertical())
			return false;
		if(next.facing()==riser.facing())
			return next.acceptsBack();
		return next.facing().getAxis()!=riser.facing().getAxis()&&next.acceptsSides();
	}

	@Nullable
	private static BeltNode straightFeeder(BeltView view, BlockPos pos, BeltNode node)
	{
		BlockPos behind = pos.relative(node.facing().getOpposite());
		for(BlockPos candidate : new BlockPos[]{behind, behind.below(), behind.above()})
			if(feeds(view, candidate, pos))
				return view.at(candidate);
		return null;
	}

	private static boolean stacksOn(@Nullable BeltNode upper, @Nullable BeltNode lower)
	{
		return upper!=null&&lower!=null&&upper.vertical()&&lower.vertical()&&upper.facing()==lower.facing();
	}

	public static boolean riserAcceptsFrom(BeltView view, BlockPos riserPos, Direction travel)
	{
		BeltNode riser = view.at(riserPos);
		if(riser==null||!riser.vertical()||stacksOn(riser, view.at(riserPos.below())))
			return false;
		return travel!=riser.facing().getOpposite();
	}

	public static Resolution resolve(BeltView view, BlockPos pos)
	{
		BeltNode node = view.at(pos);
		if(node==null)
			return new Resolution(BeltShape.STRAIGHT, BeltRun.SINGLE, false, false);
		Direction f = node.facing();

		BeltShape shape;
		boolean sideLoad = false;
		if(ascends(view, pos))
		{
			boolean continues = ascends(view, pos.relative(f).above());
			if(formsValley(view, node, pos.relative(f.getOpposite()), false))
				shape = continues?BeltShape.VALLEY_RISE: BeltShape.VALLEY_RISE_END;
			else
				shape = continues?BeltShape.RAMP: BeltShape.RAMP_END;
		}
		else if(descends(view, pos))
		{
			boolean continues = descends(view, pos.relative(f.getOpposite()).above());
			if(formsValley(view, node, pos.relative(f), true))
				shape = continues?BeltShape.VALLEY_DROP: BeltShape.VALLEY_DROP_END;
			else
				shape = continues?BeltShape.RAMP_DOWN: BeltShape.RAMP_END_DOWN;
		}
		else if(node.slopes()&&sameFacing(view.at(pos.relative(f)), f)&&ascends(view, pos.relative(f)))
			shape = BeltShape.RAMP_START;
		else if(node.slopes()&&sameFacing(view.at(pos.relative(f.getOpposite())), f)&&descends(view, pos.relative(f.getOpposite())))
			shape = BeltShape.RAMP_START_DOWN;
		else if(node.vertical())
			shape = BeltShape.STRAIGHT;
		else
		{
			boolean left = node.acceptsSides()&&feedsSide(view, pos, node.left());
			boolean right = node.acceptsSides()&&feedsSide(view, pos, node.right());
			if(left&&right)
				shape = BeltShape.FROM_BOTH;
			else if(left)
				shape = BeltShape.FROM_LEFT;
			else if(right)
				shape = BeltShape.FROM_RIGHT;
			else
				shape = BeltShape.STRAIGHT;
			sideLoad = (left||right)&&(backFeeder(view, pos, node)!=null||feeds(view, pos.below(), pos));
			BeltShape toRiser = toRiser(view, pos, node);
			if(toRiser==BeltShape.TO_RISER_LEFT)
			{
				shape = right?BeltShape.TO_RISER_LEFT_FROM_RIGHT: toRiser;
				sideLoad = right&&(backFeeder(view, pos, node)!=null||feeds(view, pos.below(), pos));
			}
			else if(toRiser==BeltShape.TO_RISER_RIGHT)
			{
				shape = left?BeltShape.TO_RISER_RIGHT_FROM_LEFT: toRiser;
				sideLoad = left&&(backFeeder(view, pos, node)!=null||feeds(view, pos.below(), pos));
			}
		}

		BeltRun run = BeltRun.SINGLE;
		if(node.joinsCoveredRun()&&!node.vertical()&&(shape==BeltShape.STRAIGHT||!shape.isFlat()))
			run = BeltRun.of(coveredBehind(view, pos, node), coveredAhead(view, pos, node));
		return new Resolution(shape, run, sideLoad, lowEndOpen(view, pos, node, shape));
	}

	private static boolean lowEndOpen(BeltView view, BlockPos pos, BeltNode node, BeltShape shape)
	{
		if(shape==BeltShape.RAMP)
			return backFeeder(view, pos, node)==null;
		if(shape==BeltShape.RAMP_DOWN)
		{
			BlockPos target = outputTarget(view, pos);
			BeltNode next = view.at(target);
			return next==null||!pos.equals(backFeeder(view, target, next));
		}
		return false;
	}

	public static BeltShape poweredShape(BeltShape resolved, BeltShape current)
	{
		return switch(resolved.sideInputs())
		{
			case FROM_LEFT -> BeltShape.FROM_LEFT;
			case FROM_RIGHT -> BeltShape.FROM_RIGHT;
			case FROM_BOTH -> current==BeltShape.FROM_RIGHT?BeltShape.FROM_RIGHT: BeltShape.FROM_LEFT;
			default -> BeltShape.STRAIGHT;
		};
	}

	public static BlockPos outputTarget(BeltView view, BlockPos pos)
	{
		BeltNode node = view.at(pos);
		if(node==null)
			return pos;
		if(node.tunnelEntry())
			return pos;
		if(node.vertical())
			return feedsForward(view, pos, node)?pos.relative(node.facing()).above(): pos.above();
		Direction f = node.facing();
		BlockPos ahead = pos.relative(f);
		if(ascends(view, pos))
			return ahead.above();
		if(view.at(ahead)==null&&sameFacing(view.at(ahead.below()), f)&&descends(view, ahead.below()))
			return ahead.below();
		return ahead;
	}

	public static boolean ascends(BeltView view, BlockPos pos)
	{
		BeltNode node = view.at(pos);
		if(node==null||!node.slopes()||!view.isOpen(pos.above()))
			return false;
		Direction f = node.facing();
		BlockPos ahead = pos.relative(f);
		BeltNode next = view.at(ahead.above());
		return view.at(ahead)==null&&sameFacing(next, f)&&next.acceptsBack()&&!next.vertical();
	}

	public static boolean descends(BeltView view, BlockPos pos)
	{
		BeltNode node = view.at(pos);
		if(node==null||!node.slopes()||!node.acceptsBack()||!view.isOpen(pos.above()))
			return false;
		Direction f = node.facing();
		BlockPos behind = pos.relative(f.getOpposite());
		BeltNode previous = view.at(behind.above());
		return view.at(behind)==null&&sameFacing(previous, f)&&!previous.vertical();
	}

	@Nullable
	private static BeltShape toRiser(BeltView view, BlockPos pos, BeltNode node)
	{
		BlockPos ahead = pos.relative(node.facing());
		BeltNode next = view.at(ahead);
		if(next!=null&&next.tunnelSide()!=null&&next.acceptsSides()&&next.facing().getAxis()!=node.facing().getAxis())
			return next.tunnelSide()==node.left()?BeltShape.TO_RISER_LEFT: next.tunnelSide()==node.right()?BeltShape.TO_RISER_RIGHT: null;
		if(!riserAcceptsFrom(view, ahead, node.facing()))
			return null;
		Direction riserFacing = view.at(ahead).facing();
		if(node.facing()==riserFacing.getClockWise())
			return BeltShape.TO_RISER_LEFT;
		if(node.facing()==riserFacing.getCounterClockWise())
			return BeltShape.TO_RISER_RIGHT;
		return null;
	}

	private static boolean formsValley(BeltView view, BeltNode node, BlockPos partner, boolean partnerAscends)
	{
		BeltNode other = view.at(partner);
		if(node.joinsCoveredRun()||!sameFacing(other, node.facing())||other.joinsCoveredRun())
			return false;
		return partnerAscends?ascends(view, partner): descends(view, partner);
	}

	private static boolean feedsSide(BeltView view, BlockPos pos, Direction side)
	{
		BlockPos neighbour = pos.relative(side);
		if(feeds(view, neighbour, pos))
			return true;
		BeltNode below = view.at(neighbour.below());
		return below!=null&&below.vertical()&&feeds(view, neighbour.below(), pos);
	}

	private static boolean feeds(BeltView view, BlockPos from, BlockPos into)
	{
		return view.at(from)!=null&&outputTarget(view, from).equals(into);
	}

	@Nullable
	private static BlockPos backFeeder(BeltView view, BlockPos pos, BeltNode node)
	{
		if(!node.acceptsBack())
			return null;
		BlockPos behind = pos.relative(node.facing().getOpposite());
		for(BlockPos candidate : new BlockPos[]{behind, behind.below(), behind.above()})
		{
			BeltNode other = view.at(candidate);
			if(sameFacing(other, node.facing())&&outputTarget(view, candidate).equals(pos))
				return candidate;
		}
		return null;
	}

	private static boolean coveredBehind(BeltView view, BlockPos pos, BeltNode node)
	{
		BlockPos feeder = backFeeder(view, pos, node);
		return feeder!=null&&view.at(feeder).joinsCoveredRun()&&!view.at(feeder).vertical();
	}

	private static boolean coveredAhead(BeltView view, BlockPos pos, BeltNode node)
	{
		BlockPos target = outputTarget(view, pos);
		BeltNode next = view.at(target);
		return sameFacing(next, node.facing())&&next.joinsCoveredRun()&&!next.vertical()&&pos.equals(backFeeder(view, target, next));
	}

	private static boolean sameFacing(@Nullable BeltNode node, Direction facing)
	{
		return node!=null&&node.facing()==facing;
	}
}
