package com.igteam.immersivegeology.common.block.multiblocks.shim.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public enum RelativeBlockFace
{
	FRONT,
	LEFT,
	BACK,
	RIGHT,
	UP,
	DOWN;

	public static final RelativeBlockFace[] HORIZONTAL = {FRONT, LEFT, BACK, RIGHT};

	public RelativeBlockFace getOpposite()
	{
		return switch(this)
		{
			case FRONT -> BACK;
			case BACK -> FRONT;
			case LEFT -> RIGHT;
			case RIGHT -> LEFT;
			case UP -> DOWN;
			case DOWN -> UP;
		};
	}

	public EnumFacing forFront(MultiblockOrientation orientation)
	{
		if(this==UP) return EnumFacing.UP;
		if(this==DOWN) return EnumFacing.DOWN;

		RelativeBlockFace effective = this;
		if(orientation.mirrored())
		{
			if(effective==LEFT) effective = RIGHT;
			else if(effective==RIGHT) effective = LEFT;
		}

		EnumFacing base = switch(effective)
		{
			case FRONT -> EnumFacing.NORTH;
			case BACK -> EnumFacing.SOUTH;
			case LEFT -> EnumFacing.WEST;
			case RIGHT -> EnumFacing.EAST;
			default -> EnumFacing.NORTH;
		};
		return orientation.rotateHorizontal(base);
	}

	public static RelativeBlockFace from(MultiblockOrientation orientation, EnumFacing absolute)
	{
		if(absolute==EnumFacing.UP) return UP;
		if(absolute==EnumFacing.DOWN) return DOWN;
		for(RelativeBlockFace face : HORIZONTAL)
			if(face.forFront(orientation)==absolute) return face;
		return FRONT;
	}

	public BlockPos offsetRelative(BlockPos pos, int amount)
	{
		return switch(this)
		{
			case UP -> pos.add(0, amount, 0);
			case DOWN -> pos.add(0, -amount, 0);
			case FRONT -> pos.add(0, 0, -amount);
			case BACK -> pos.add(0, 0, amount);
			case LEFT -> pos.add(-amount, 0, 0);
			case RIGHT -> pos.add(amount, 0, 0);
		};
	}
}
