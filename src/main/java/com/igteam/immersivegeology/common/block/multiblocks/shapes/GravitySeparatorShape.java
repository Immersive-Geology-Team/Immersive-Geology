/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GravitySeparatorShape extends GenericShape {

    public static final GravitySeparatorShape GETTER = new GravitySeparatorShape();

    private GravitySeparatorShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();
        if (bY == 5) {
            if (bZ == 1 && bX == 2) {
                main.add(new AABB(0.0, 0.75, 0.0, 0.5, 1.0, 1.0));
            }
            if (bZ == 2 && bX == 1) {
                main.add(new AABB(0.0, 0.75, 0.0, 1.0, 1.0, 0.5));
            }
            if (bZ == 0 && bX == 1) {
                main.add(new AABB(0.0, 0.75, 0.5, 1.0, 1.0, 1.0));
            }
            if (bZ == 1 && bX == 0) {
                main.add(new AABB(0.5, 0.75, 0.0, 1.0, 1.0, 1.0));
            }
            if(bZ == 0 && bX == 0)
            {
                main.add(new AABB(0.5, 0.75, 0.5, 1.0, 1.0, 1.0));
            }
            if(bZ == 2 && bX == 2)
            {
                main.add(new AABB(0, 0.75, 0, .5, 1.0, .5));
            }
            if(bZ == 0 && bX == 2)
            {
                main.add(new AABB(0, 0.75, 0.5, .5, 1.0, 1));
            }
            if(bZ == 2 && bX == 0)
            {
                main.add(new AABB(0.5, 0.75, 0, 1, 1.0, 0.5));
            }
        }
        if(bZ == 1 && bX == 1)
        {
            main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
        }

        if(bZ == 2 && bX == 1 && bY > 0 && bY < 4)
        {
            for(int xP = 0; xP < 4; xP++)
            {
                float xStart = xP * 0.25f;
                float xEnd = 0.25f + xStart;
                float yStart = 1.125f - xP * 0.125f;
                float yEnd = 1.125f-(0.125f) -  xP * 0.125f;
                main.add(new AABB(xStart, yStart, 0.0, xEnd, yEnd, .5));
            }

            if(bY == 1)
            {
                main.add(new AABB(0.0, 0.1875, 0.0, 0.5, 0, .5));
            }
        }

        if(bZ == 1 && bX == 0 && bY > 0 && bY < 5)
        {
            for(int xP = 0; xP < 4; xP++)
            {
                float xStart = xP * 0.25f;
                float xEnd = 0.25f + xStart;
                float yStart = 0.625f - xP * 0.125f;
                float yEnd = 0.625f-(0.125f) -  xP * 0.125f;
                main.add(new AABB(0.5, yStart, xStart, 1, yEnd, xEnd));
            }
        }

        if(bY == 0)
        {
            main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
        }

        if(bY > 0)
        {
            if(bY < 5)
            {
                if(bX == 0 && bZ == 0)
                {
                    main.add(new AABB(0.0, 0.0, 0.0, 0.5, 1.0, 0.5));
                }
                if(bX == 0 && bZ == 2)
                {
                    main.add(new AABB(0, 0.0, 0.5, 0.5, 1.0, 1));
                }
                if(bX == 2 && bZ == 0)
                {
                    main.add(new AABB(0.5, 0.0, 0, 1, 1.0, .5));
                }
                if(bX == 2 && bZ == 2)
                {
                    main.add(new AABB(0.5, 0.0, 0.5, 1, 1.0, 1));
                }
            }

            if(bY < 4)
            {
                if(bZ==2)
                {
                    main.add(new AABB(0, 0.0, 0.6875, 1, 1.0, 0.75));
                }
                if(bZ==0)
                {
                    main.add(new AABB(0, 0.0, 0.25, 1, 1.0, 0.3125));
                }
                if(bX==2)
                {
                    main.add(new AABB(0.6875, 0.0, 0.0, 0.75, 1.0, 1.0));
                }
                if(bX==0)
                {
                    main.add(new AABB(0.25, 0.0, 0, 0.31251, 1.0, 1));
                }
            }
            if(bY == 4)
            {
                if(bZ==2)
                {
                    main.add(new AABB(0, 0.0, 0.6875, 1, .5, 0.75));
                    main.add(new AABB(0, 0.375, 0.6875, 1, .5, 0.8125));
                }
                if(bZ==0)
                {
                    main.add(new AABB(0, 0.0, 0.25, 1, .5, 0.3125));
                    main.add(new AABB(0, 0.375, 0.1875, 1, .5, 0.3125));
                }
                if(bX==2)
                {
                    main.add(new AABB(0.6875, 0.0, 0.0, 0.75, .5, 1.0));
                    main.add(new AABB(0.6875, 0.375, 0, 0.8125, .5, 1));
                }
                if(bX==0)
                {
                    main.add(new AABB(0.25, 0.0, 0, 0.31251, .5, 1));
                    main.add(new AABB(0.1875, 0.375, 0, 0.31251, .5, 1));
                }
            }

        }

        return main;
    }
}
