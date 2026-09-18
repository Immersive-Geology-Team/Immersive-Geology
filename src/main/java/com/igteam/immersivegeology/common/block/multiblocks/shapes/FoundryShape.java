/*
 * Muddykat
 * Copyright (c) 2024
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

public class FoundryShape extends GenericShape {

    public static final FoundryShape GETTER = new FoundryShape();

    private static final AABB[] PARTS = {
            new AABB(0.0, 0.0, 0.0, 5.0, 0.5, 5.0),
            new AABB(0.0, 0.5, 0.0, 0.625, 4.9375, 0.5),
            new AABB(4.4375, 0.5, 0.0, 5.0, 4.9375, 0.5),
            new AABB(0.625, 4.375, 0.0, 4.4375, 4.9375, 0.5),
            new AABB(2.5625, 0.5, 0.0, 4.4375, 2.5, 0.5),
            new AABB(1.0625, 2.0, 0.0, 2.0625, 3.0, 0.5625),
            new AABB(0.0, 0.5, 0.5, 0.625, 1.5, 3.125),
            new AABB(0.625, 0.5, 0.0, 2.5625, 1.5, 3.125),
            new AABB(0.0, 0.5, 3.125, 5.0, 1.5, 5.0),
            new AABB(0.0, 1.5, 0.5, 2.5625, 3.125, 3.125),
            new AABB(0.0, 3.125, 0.5, 2.5625, 4.4375, 3.125),
            new AABB(0.6875, 4.4375, 1.15625, 1.9375, 4.9375, 2.46875),
            new AABB(2.5625, 0.5, 0.5, 3.0625, 3.625, 1.0),
            new AABB(2.5625, 0.5, 2.625, 3.0625, 3.625, 3.125),
            new AABB(4.5, 0.5, 0.5, 5.0, 3.625, 1.0),
            new AABB(4.5, 0.5, 2.625, 5.0, 3.625, 3.125),
            new AABB(2.65625, 0.875, 1.0625, 4.90625, 2.1875, 2.8125),
            new AABB(2.5625, 2.625, 1.5625, 4.0625, 3.125, 2.0625),
            new AABB(1.5, 1.5, 3.125, 2.0, 3.125, 3.25),
            new AABB(1.5, 1.5, 4.875, 2.0, 3.125, 5.0),
            new AABB(1.5, 2.6875, 3.25, 2.0, 3.375, 4.875)
    };

    private FoundryShape(){};

    private static void addCell(List<AABB> shape, AABB part, int x, int y, int z)
    {
        double minX = Math.max(part.minX-x, 0), maxX = Math.min(part.maxX-x, 1);
        double minY = Math.max(part.minY-y, 0), maxY = Math.min(part.maxY-y, 1);
        double minZ = Math.max(part.minZ-z, 0), maxZ = Math.min(part.maxZ-z, 1);
        if(minX < maxX&&minY < maxY&&minZ < maxZ) shape.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
    }

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> shape = new ArrayList<>();
        for(AABB part : PARTS) addCell(shape, part, bX, bY, bZ);
        return shape;
    }
}
