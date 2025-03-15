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

public class BallmillShape extends GenericShape {

    public static final BallmillShape GETTER = new BallmillShape();

    private BallmillShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();
        if(bY == 0)
        {
            if(bX != 0 && bX != 4) main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
            if((bZ == 3 || bZ == 2) && (bX == 0 || bX == 2)) main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1, 1.0));
            if(bX == 0 && bZ < 2)
            {
                main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
                main.add(new AABB(0.0, 0.0, 0.0, 0.5, 1, 1.0));
            }
            if(bX == 4 && bZ < 3)
            {
                main.add(new AABB(0.0, 0.0, 0.0, 0.5, .5, 1.0));
                main.add(new AABB(.5, 0.0, 0.0, 1, 1, 1.0));
            }
            if(bX == 4 && bZ == 3)
            {
                main.add(new AABB(0.0, 0.0, 0.0, 1, .5, 1.0));
                main.add(new AABB(0.125, 0.0, .5, 0.25, 1, 1.0));
                main.add(new AABB(0.75, 0.0, .5, 0.875, 1, 1.0));
            }
        }

        if(bY == 1)
        {
            if(bX == 0 && bZ == 3) main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1, 1.0));
            if(bX == 0 && bZ < 3)
            {
                main.add(new AABB(0.0, 0.0, 0.0, 0.5, 1, 1.0));
            }
            if(bX == 4 && bZ < 3) main.add(new AABB(.5, 0.0, 0.0, 1, 1, 1.0));
            if(bX > 0 && bX < 4)
            {
                if(bZ ==0) main.add(new AABB(0.0, 0, 0.375, 1, 1, 1.0));
                if(bZ == 1) main.add(new AABB(0.0, 0, 0, 1, 1, 1.0));
                if(bZ == 2) main.add(new AABB(0.0, 0, 0, 1, 1, 0.625));
            }
            if(bX == 4 && bZ == 3)
            {
                main.add(new AABB(0.0, 0.0, 0.5, 1, 1, 1.0));
            }
        }
        if(bY == 2)
        {
            if(bX > 0 && bX < 4)
            {
                if(bZ ==0) main.add(new AABB(0.0, 0, 0.375, 1, 1, 1.0));
                if(bZ == 1) main.add(new AABB(0.0, 0, 0, 1, 1, 1.0));
                if(bZ == 2) main.add(new AABB(0.0, 0, 0, 1, 1, 0.625));
            }
            if(bX == 0 && bZ == 0)
            {
                main.add(new AABB(0.0, 0.0, 0.0, 0.5, 0.375, 0.5));
                main.add(new AABB(0.0, 0.0, 0.5, 0.5, 0.75, 1.0));
            }
            if(bX == 0 && bZ == 1)
            {
                main.add(new AABB(0.0, 0.0, 0.0, 0.5, 0.75, 1.0));
                main.add(new AABB(0.0, 0.0, 0.1875, 1, .375, 0.8125));
            }
            if(bX == 0 && bZ == 2)
            {
                main.add(new AABB(0.0, 0.0, 0.0, 0.5, 0.75, 0.5));
                main.add(new AABB(0.0, 0.0, 0.5, 0.5, 0.375, 1.0));
            }

            if(bX == 4 && bZ == 0)
            {
                main.add(new AABB(0.5, 0.0, 0.0, 1, 0.375, 0.5));
                main.add(new AABB(0.5, 0.0, 0.5, 1, 0.75, 1.0));
            }
            if(bX == 4 && bZ == 1)
            {
                main.add(new AABB(0.5, 0.0, 0.0, 1, 0.75, 1.0));
            }
            if(bX == 4 && bZ == 2)
            {
                main.add(new AABB(0.5, 0.0, 0.0, 1, 0.75, 0.5));
                main.add(new AABB(0.5, 0.0, 0.5, 1, 0.375, 1.0));
            }
        }
        return main;
    }
}
