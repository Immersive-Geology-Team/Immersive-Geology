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

public class BloomeryShape extends GenericShape {

    public static final BloomeryShape GETTER = new BloomeryShape();

    private BloomeryShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();
        if(bY == 0)
        {
            if(bZ == 0 && bX == 0)
            {
                main.add(new AABB(0.375, 0.0, 0.375, 1.0, 1.0, 1.0));
                main.add(new AABB(0.1875, 0.6875, 0.1875, 1.0, 1.0, 1.0));
            }
            if(bZ == 0 && bX == 1)
            {
                main.add(new AABB(0.0, 0.0, 0.375, 0.625, 1.0, 1.0));
                main.add(new AABB(0.0, 0.6875, 0.1875, 0.8125, 1.0, 1.0));
            }
            if(bZ == 1 && bX == 0)
            {
                main.add(new AABB(0.375, 0.0, 0, 1.0, 1.0, 0.625));
                main.add(new AABB(0.1875, 0.6875, 0, 1.0, 1.0, 0.8125));
                main.add(new AABB(0.625, 0, 0, 1.0, 1.0, 0.8125));
            }
            if(bZ == 1 && bX == 1)
            {
                main.add(new AABB(0, 0.0, 0, 0.375, 0.6875, 0.8125));
                main.add(new AABB(0, 0.0, 0, 0.625, 0.6875, 0.625));
                main.add(new AABB(0, 0.6875, 0, 0.8125, 1, 0.8125));
            }
        }
        if(bY == 1)
        {
            if(bZ == 1 && bX == 1)
            {
                main.add(new AABB(0, 0, 0, 0.75, 1, 0.75));
            }
            if(bZ == 0 && bX == 0)
            {
                main.add(new AABB(0.25, 0, 0.25, 1, 1, 1));
            }
            if(bZ == 1 && bX == 0)
            {
                main.add(new AABB(0.25, 0, 0, 1, 1, 0.75));
            }
            if(bZ == 0 && bX == 1)
            {
                main.add(new AABB(0, 0, 0.25, 0.75, 1, 1));
            }
        }
        if(bY == 2)
        {
            if(bZ == 1 && bX == 1)
            {
                main.add(new AABB(0, 0.375, 0, 0.4375, 0.875, 0.4375));
                main.add(new AABB(0, 0, 0, 0.5625, 0.375, 0.5625));
            }
            if(bZ == 0 && bX == 0)
            {
                main.add(new AABB(0.5625, 0.375, 0.5625, 1, 0.875, 1));
                main.add(new AABB(0.4375, 0, 0.4375, 1, 0.375, 1));
            }
            if(bZ == 1 && bX == 0)
            {
                main.add(new AABB(0.5625, 0.375, 0, 1, 0.875, 0.4375));
                main.add(new AABB(0.4375, 0, 0, 1, 0.375, 0.5625));
            }
            if(bZ == 0 && bX == 1)
            {
                main.add(new AABB(0, 0.375, 0.5625, 0.4375, 0.875, 1));
                main.add(new AABB(0, 0, 0.4375, 0.5625, 0.375, 1));
            }
        }
        return main;
    }
}
