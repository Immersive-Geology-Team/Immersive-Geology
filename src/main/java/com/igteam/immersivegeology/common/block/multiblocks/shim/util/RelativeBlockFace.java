package com.igteam.immersivegeology.common.block.multiblocks.shim.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

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
		EnumFacing front = orientation.front();
		boolean mirror = orientation.mirrored();
		return switch(this)
		{
			case FRONT -> front;
			case LEFT -> mirror?front.rotateYCCW(): front.rotateY();
			case BACK -> front.getOpposite();
			case RIGHT -> mirror?front.rotateY(): front.rotateYCCW();
			case UP -> EnumFacing.UP;
			case DOWN -> EnumFacing.DOWN;
		};
	}

	@Nullable
	public static RelativeBlockFace from(MultiblockOrientation orientation, @Nullable EnumFacing absoluteFace)
	{
		if(absoluteFace==null) return null;
		if(absoluteFace==EnumFacing.UP) return UP;
		if(absoluteFace==EnumFacing.DOWN) return DOWN;

		EnumFacing front = orientation.front();
		boolean mirror = orientation.mirrored();
		int rotations = Math.floorMod(front.getHorizontalIndex()-absoluteFace.getHorizontalIndex(), 4);
		return switch(rotations)
		{
			case 0 -> FRONT;
			case 1 -> mirror?LEFT: RIGHT;
			case 2 -> BACK;
			default -> mirror?RIGHT: LEFT;
		};
	}

	public static boolean selfTest()
	{
		for(EnumFacing front : EnumFacing.HORIZONTALS)
			for(boolean mirrored : new boolean[]{false, true})
			{
				MultiblockOrientation orientation = new MultiblockOrientation(front, mirrored);
				for(RelativeBlockFace face : values())
					if(from(orientation, face.forFront(orientation))!=face) return false;
			}
		return true;
	}

	public BlockPos offsetRelative(BlockPos startPos, int amount)
	{
		return switch(this)
		{
			case FRONT -> startPos.offset(EnumFacing.NORTH, amount);
			case LEFT -> startPos.offset(EnumFacing.EAST, amount);
			case BACK -> startPos.offset(EnumFacing.SOUTH, amount);
			case RIGHT -> startPos.offset(EnumFacing.WEST, amount);
			case UP -> startPos.offset(EnumFacing.UP, amount);
			case DOWN -> startPos.offset(EnumFacing.DOWN, amount);
		};
	}
}
