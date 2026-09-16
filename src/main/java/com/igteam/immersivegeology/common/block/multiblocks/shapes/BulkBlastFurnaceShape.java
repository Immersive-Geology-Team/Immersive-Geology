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

	private static final double PORT_DEPTH = 0.125;
	private static final double PORT_INSET = 0.0625;
	private static final double PORT_MIN = 0.125;
	private static final double PORT_MAX = 0.875;

	private static final AABB HEARTH = new AABB(0.5625, 0.0, 0.5625, 2.4375, 1.875, 2.4375);

	private static final AABB[] BUTTRESSES = {
			new AABB(0.0, 0.0, 0.875, 0.5625, 1.8125, 2.125),
			new AABB(0.125, 1.8125, 0.875, 0.5625, 2.1875, 2.125),
			new AABB(0.375, 2.1875, 0.875, 0.5625, 2.5625, 2.125),
			new AABB(2.4375, 0.0, 0.875, 3.0, 1.8125, 2.125),
			new AABB(2.4375, 1.8125, 0.875, 2.875, 2.1875, 2.125),
			new AABB(2.4375, 2.1875, 0.875, 2.625, 2.5625, 2.125),
			new AABB(0.875, 0.0, 0.0, 2.125, 1.25, 0.5625),
			new AABB(0.875, 1.25, 0.125, 2.125, 1.625, 0.5625),
			new AABB(0.875, 1.625, 0.375, 2.125, 2.0, 0.5625),
			new AABB(0.875, 0.0, 2.4375, 2.125, 1.25, 3.0),
			new AABB(0.875, 1.25, 2.4375, 2.125, 1.625, 2.875),
			new AABB(0.875, 1.625, 2.4375, 2.125, 2.0, 2.625)
	};

	private static final AABB[] STACK = {
			new AABB(0.3125, 1.875, 0.3125, 2.6875, 2.3125, 2.6875),
			new AABB(0.125, 2.3125, 0.125, 2.875, 3.5, 2.875),
			new AABB(0.1875, 3.5, 0.1875, 2.8125, 4.0, 2.8125),
			new AABB(0.25, 4.0, 0.25, 2.75, 4.5, 2.75),
			new AABB(0.375, 4.5, 0.375, 2.625, 5.0, 2.625),
			new AABB(0.4375, 5.0, 0.4375, 2.5625, 5.5, 2.5625),
			new AABB(0.5, 5.5, 0.5, 2.5, 6.0, 2.5),
			new AABB(0.5625, 6.0, 0.5625, 2.4375, 6.5, 2.4375),
			new AABB(0.625, 6.5, 0.625, 2.375, 6.9375, 2.375),
			new AABB(0.75, 6.9375, 0.75, 2.25, 7.375, 2.25),
			new AABB(0.75, 7.375, 0.75, 0.9375, 8.0, 2.25),
			new AABB(2.0625, 7.375, 0.75, 2.25, 8.0, 2.25),
			new AABB(0.9375, 7.375, 0.75, 2.0625, 8.0, 0.9375),
			new AABB(0.9375, 7.375, 2.0625, 2.0625, 8.0, 2.25)
	};

	private BulkBlastFurnaceShape()
	{
	}

	private static void addCell(List<AABB> shape, AABB part, int x, int y, int z)
	{
		double minX = Math.max(part.minX-x, 0), maxX = Math.min(part.maxX-x, 1);
		double minY = Math.max(part.minY-y, 0), maxY = Math.min(part.maxY-y, 1);
		double minZ = Math.max(part.minZ-z, 0), maxZ = Math.min(part.maxZ-z, 1);
		if(minX < maxX&&minY < maxY&&minZ < maxZ) shape.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
	}

	@NotNull
	@Override
	protected List<AABB> getShape(BlockPos posInMultiblock)
	{
		final int x = posInMultiblock.getX();
		final int y = posInMultiblock.getY();
		final int z = posInMultiblock.getZ();

		List<AABB> shape = new ArrayList<>();
		addCell(shape, HEARTH, x, y, z);
		for(AABB part : BUTTRESSES) addCell(shape, part, x, y, z);
		for(AABB part : STACK) addCell(shape, part, x, y, z);

		if(y==1&&z==1)
		{
			if(x==0) shape.add(new AABB(-PORT_DEPTH, PORT_MIN, PORT_MIN, PORT_INSET, PORT_MAX, PORT_MAX));
			if(x==2) shape.add(new AABB(1-PORT_INSET, PORT_MIN, PORT_MIN, 1+PORT_DEPTH, PORT_MAX, PORT_MAX));
		}
		return shape;
	}
}
