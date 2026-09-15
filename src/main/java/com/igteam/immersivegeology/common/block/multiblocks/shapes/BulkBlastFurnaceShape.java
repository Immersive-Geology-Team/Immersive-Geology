/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BulkBlastFurnaceShape extends GenericShape
{
	public static final BulkBlastFurnaceShape GETTER = new BulkBlastFurnaceShape();

	private static final double STACK_INSET = 0.125;
	private static final double PORT_DEPTH = 0.125;
	private static final double PORT_MIN_Y = 0.125;
	private static final double PORT_MAX_Y = 0.875;
	private static final double PORT_MIN_Z = 0.125;
	private static final double PORT_MAX_Z = 0.875;

	private BulkBlastFurnaceShape()
	{
	}

	@NotNull
	@Override
	protected List<AABB> getShape(BlockPos posInMultiblock)
	{
		final int x = posInMultiblock.getX();
		final int y = posInMultiblock.getY();
		final int z = posInMultiblock.getZ();

		List<AABB> shape = new ArrayList<>();
		if(y <= 1)
		{
			shape.add(new AABB(0, 0, 0, 1, 1, 1));
			if(y==1&&z==1)
			{
				if(x==0) shape.add(new AABB(-PORT_DEPTH, PORT_MIN_Y, PORT_MIN_Z, 0, PORT_MAX_Y, PORT_MAX_Z));
				if(x==2) shape.add(new AABB(1, PORT_MIN_Y, PORT_MIN_Z, 1+PORT_DEPTH, PORT_MAX_Y, PORT_MAX_Z));
			}
			return shape;
		}

		shape.add(new AABB(
				x==0?STACK_INSET: 0, 0, z==0?STACK_INSET: 0,
				x==2?1-STACK_INSET: 1, 1, z==2?1-STACK_INSET: 1
		));
		return shape;
	}
}
