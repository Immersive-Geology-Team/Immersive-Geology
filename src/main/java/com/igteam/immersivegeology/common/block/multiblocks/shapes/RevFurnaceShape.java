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

public class RevFurnaceShape extends GenericShape {

    public static final RevFurnaceShape GETTER = new RevFurnaceShape();

    private RevFurnaceShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock)
    {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();

        if(bX < 3)
        {
            if(bY==2)
            {
                if(bZ==0)
                {
                    main.add(bX==0?new AABB(0.125, 0.0, 0.25, 1.0, 0.5, 1.0): new AABB(0.0, 0.0, 0.25, 1.0, 0.5, 1.0));
                }
                else if(bZ==2||bZ==3)
                {
                    main.add(bX==0?new AABB(0.125, 0.0, 0.0, 1.0, 0.5, 1.0): new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
                }
                else if(bZ==5)
                {
                    main.add(bX==0?new AABB(0.125, 0.0, 0.0, 1.0, 0.5, 0.75): new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 0.75));
                }
            }
            else if(bY < 2)
            {
                if(bZ==0)
                {
                    main.add(bX==0?new AABB(0.125, 0.0, 0.25, 1.0, 1.0, 1.0): new AABB(0.0, 0.0, 0.25, 1.0, 1.0, 1.0));
                }
                else if(bZ==5)
                {
                    main.add(bX==0?new AABB(0.125, 0.0, 0.0, 1.0, 1.0, 0.75): new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 0.75));
                }
                else if(bX==0)
                {
                    if(bY==0&&(bZ==1||bZ==4))
                    {
                        main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
                    }
                    else
                    {
                        main.add(new AABB(0.125, 0.0, 0.0, 1.0, 1.0, 1.0));
                    }
                }
            }
        }

        // Handle thin strips at bY == 6, bX == 2
        if(bX==2&&bY==6&&bZ <= 5)
        {
            main.add(new AABB(0.9375D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 1.0000D));
        }
        double[] unifiedHeights = {0.25, 0.5, 0.75, 1.0};
        // Define common stepped patterns
        double[][] steppedPattern1 = {
                {0.0, 0.25},
                {0.125, 0.5},
                {0.25, 0.75},
                {0.375, 1.0}};
        double[][] steppedPattern2 = {
                {0.4375, 0.25},
                {0.5625, 0.5},
                {0.6875, 0.75},
                {0.8125, 1.0}};

        if(bX==3&&bY==6)
        {
            if(bZ==0||bZ==1||bZ==3||bZ==4)
            {
                for(double[] step : steppedPattern1)
                {
                    double zStart = (bZ==0||bZ==3)?step[0]: 0.0;
                    double zEnd = 1.0;
                    main.add(new AABB(step[0], 0.0, zStart, 1.0, step[1], zEnd));
                }
            }
            else if(bZ==2||bZ==5)
            {
                double[] zEnds = {1.0, 0.875, 0.75, 0.625, 0.5625};
                for(int i = 0; i < steppedPattern1.length; i++)
                {
                    main.add(new AABB(steppedPattern1[i][0], 0.0, 0.0, 1.0, steppedPattern1[i][1], zEnds[i]));
                }
            }
        }

        // Handle bX == 3, bY == 9
        if(bX==3&&bY==9)
        {
            if(bZ==0||bZ==1||bZ==3||bZ==4)
            {
                for(double[] step : steppedPattern2)
                {
                    double zStart = (bZ==0||bZ==3)?step[0]: 0.0;
                    double zEnd = 1.0;
                    main.add(new AABB(step[0], 0.0, zStart, 1.0, step[1], zEnd));
                }
            }
            else if(bZ==2||bZ==5)
            {
                double[] zEnds = {0.5625, 0.4375, 0.3125, 0.1875, 0.0625};
                for(int i = 0; i < steppedPattern2.length; i++)
                {
                    main.add(new AABB(steppedPattern2[i][0], 0.0, 0.0, 1.0, steppedPattern2[i][1], zEnds[i]));
                }
            }
        }

        if (bX == 5 && bY == 9)
        {
            if (bZ == 0 || bZ == 1 || bZ == 3 || bZ == 4)
            {
                for (double[] step : steppedPattern2)
                {
                    double zStart = (bZ == 0 || bZ == 3) ? step[0] : 0.0;
                    double zEnd = 1.0;

                    // Flip X direction by mirroring across X = 0.5
                    main.add(new AABB(0.0, 0.0, zStart, 1.0 - step[0], step[1], zEnd));
                }
            }
            else if (bZ == 2 || bZ == 5)
            {
                double[] zEnds = {0.5625, 0.4375, 0.3125, 0.1875, 0.0625};
                for (int i = 0; i < steppedPattern2.length; i++)
                {
                    // Flip X direction by mirroring across X = 0.5
                    main.add(new AABB(0.0, 0.0, 0.0, 1.0 - steppedPattern2[i][0], steppedPattern2[i][1], zEnds[i]));
                }
            }
        }

        // Handle bX == 4 cases
        if(bX==4)
        {
            if(bY==6)
            {
                if(bZ==1||bZ==4)
                {
                    main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
                }
                else if(bZ==0||bZ==3)
                {
                    for(double[] step : steppedPattern1)
                    {
                        double zStart = step[0];
                        main.add(new AABB(0.0, 0.0, zStart, 1.0, step[1], 1.0));
                    }
                }
                else if(bZ==2||bZ==5)
                {
                    double[] heights = {1.0, 0.75, 0.5, 0.25};
                    double[] zEnds   = {0.5625, 0.6875, 0.8125, 1.0};

                    for(int i = 0; i < heights.length; i++)
                    {
                        main.add(new AABB(0.0, 0.0, 0.0, 1.0, heights[i], zEnds[i]));
                    }
                }
            }
            else if(bY==9)
            {
                if(bZ==1||bZ==4)
                {
                    main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
                }
                else if(bZ==0||bZ==3)
                {
                    for(double[] step : steppedPattern2)
                    {
                        double zStart = step[0];
                        main.add(new AABB(0.0, 0.0, zStart, 1.0, step[1], 1.0));
                    }
                }
                else if(bZ==2||bZ==5)
                {
                    double[] heights = {1.0, 0.75, 0.5, 0.25};
                    double[] zEnds = {0.0625, 0.1875, 0.3125, 0.4375};
                    for(int i = 0; i < heights.length; i++)
                    {
                        main.add(new AABB(0.0, 0.0, 0.0, 1.0, heights[i], zEnds[i]));
                    }
                }
            }
        }

        // Handle bX == 5, bY == 6 cases
        if(bX==5&&bY==6)
        {
            double[][] reversedPattern = {
                    {1.0, 0.25},
                    {0.875, 0.5},
                    {0.75, 0.75},
                    {0.625, 1.0}
            };

            if (bZ == 0 || bZ == 3) {
                // Base layer
                main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0));

                for (double[] step : reversedPattern)
                {
                    main.add(new AABB(0.0, 0.0, 1.0 - step[0], step[0], step[1], 1.0));
                }
            }
            else if(bZ==1||bZ==4)
            {
                for(double[] step : reversedPattern)
                {
                    main.add(new AABB(0.0, 0.0, 0.0, step[0], step[1], 1.0));
                }
            }
            else if(bZ==2||bZ==5)
            {
                for(double[] step : reversedPattern)
                {
                    main.add(new AABB(0.0, 0.0, 0.0, step[0], step[1], step[0]));
                }
            }
        }

        // Handle middle sections (bY between 6 and 9)
        if(bY > 6&&bY < 9)
        {
            if(bX==3)
            {
                main.add(new AABB(0.5, 0.0, -0.5, 1.0, 1.0, 1.5));
            }
            else if(bX==5)
            {
                main.add(new AABB(0.0, 0.0, -0.5, 0.5, 1.0, 1.5));
            }
            else if(bX==4)
            {
                if(bZ==5||bZ==2)
                {
                    main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5));
                }
                else if(bZ==0||bZ==3)
                {
                    main.add(new AABB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));
                }
            }
        }

        if(main.isEmpty()) main.add(new AABB(0,0,0,1,1,1));

        return main;
    }
}
