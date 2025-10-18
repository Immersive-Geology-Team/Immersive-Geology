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
import java.util.stream.Collectors;

public class SteamTurbineShape extends GenericShape {

    public static final SteamTurbineShape GETTER = new SteamTurbineShape();

    private SteamTurbineShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        AABB pipeHorizontalBF = new AABB(0.25, 0.25, 0, 0.75, 0.75, 1);
        AABB pipeConnectionBottom = new AABB(0.125, 0, 0.125, 0.875, 0.1875, 0.875);
        AABB pipeMiddleVertical = new AABB(0.25, 0, 0.25, 0.75, 1, 0.75);

        List<AABB> main = new ArrayList<>();

        if(bX == 0)
        {
            if(bZ == 11 && bY == 1)
            {
                main.add(new AABB(0,0,0,8/16f,1,1));
            }
            if(bZ == 10)
            {
                main.add(new AABB(8/16f,0,-(10/16f),1,1,1));
            }
            if(bZ == 9)
            {
                main.add(new AABB(8/16f,0,6/16f,1,1,2));
            }
            if(bZ == 8)
            {
                if(bY == 2) main.add(new AABB(6/16f,0,0,1,6/16f,22/16f));
                if(bY!=2) main.add(new AABB(0,0,0,1,1,22/16f));
            }
            if(bZ == 7)
            {
                if(bY != 2)
                {
                    main.add(new AABB(8/16f, 0, 0, 1, 1, 1));
                }
                else {
                    main.add(new AABB(8/16f, 0, 0, 1, 8/16f, 1));
                }
            }
            if(bZ == 5 || bZ == 4 || bZ == 2 || bZ == 1)
            {
                if(bY != 2) main.add(new AABB(-(8/16f),0,0,1,1,1));
                if(bY == 2)
                {
                    main.add(new AABB(0,0,0,1,12/16f,1));
                    main.add(new AABB(-(4/16f),0,0,1,8/16f,1));
                }
            }
            if(bZ == 3 && bY == 2)
            {
                main.add(new AABB(6/16f, 0, 0, 1, 6/16f, 1));
            }
        }

        if(bX == 1)
        {
            if(bZ == 3)
            {

                if(bY==2)
                {
                    main.add(pipeMiddleVertical);
                    main.add(pipeConnectionBottom.move(0, 6/16f, 0));
                    main.add(new AABB(0, 0, 0, 1, 6/16f, 1));
                    return main;
                }
                if(bY==3)
                {
                    main.add(pipeMiddleVertical);
                    main.add(pipeConnectionBottom.move(0, 6/16f, 0));
                    return main;
                }
            }
            if(bY == 4 && bZ == 3)
            {
                main.add(new AABB(0,-0.5f,-(1/16f),1,0.5f,1));
                return main;
            }
            if(bY == 4 && bZ != 8 && bZ != 7)
            {
                main.add(new AABB(0,-0.5f,0,1,0.5f,1));
                return main;
            }
            if(bZ == 7)
            {
                if(bY == 2) main.add(new AABB(0,0,0,1,0.5,1));
                if(bY == 4) main.add(new AABB(0,-0.5f,0,1,0.5f,15/16f));
            }
            if(bZ == 8)
            {
                if(bY == 4 || bY == 3)
                {
                    boolean moveDown = bY == 4;
                    main.add(pipeMiddleVertical.inflate(0,0.125f,0).move(0,0.125f,3/16f));
                    main.add(new AABB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75).move(0,8/16f,-(5/16f)));
                    main.add(new AABB(2/16f, 2/16f, 0, 14/16f, 14/16f, 2/16f).move(0,0.5f,-(1/16f)));
                    List<AABB> alt = main.stream().map(f -> f.move(0,-1,0)).collect(Collectors.toList());
                    return moveDown ? alt : main;
                }
                main.add(pipeMiddleVertical.move(0,0,3/16f));
                if(bY == 2)
                {
                    main.add(new AABB(0,0,0,1,7/16f,22/16f));
                    main.add(pipeConnectionBottom.move(0, 6/16f, 3/16f));
                }
            }
            if(bZ == 9) main.add(new AABB(0,0,6/16f,1,2/16f,2));
            if(bZ == 10) main.add(new AABB(0,0,-(10/16f),1,2/16f,1));
            if(bZ == 11)
            {
                if(bY == 2)
                {
                    main.add(new AABB(13/16f,2/16f,2/16f,1,14/16f,14/16f).move(6/16f,0,0));
                    main.add(new AABB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75).move(8/16f, 0,0));
                    main.add(new AABB(0.25, 0, 0.25, 0.75, 0.75, 0.75));
                }
                if(bY == 1)
                {
                    main.add(pipeMiddleVertical.deflate(0,0.125f,0).move(0,0.125f,0));
                    main.add(new AABB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75).move(0, 0,3/16f));
                    main.add(new AABB(2/16f, 2/16f, 0, 14/16f, 14/16f, 2/16f).move(0,0,15/16f));
                    main.add(new AABB(0,0,0,1,1,3/16f));
                    main.add(new AABB(2/16f,5/16f,3/16f,4/16f,10/16f,9/16f));
                    main.add(new AABB(2/16f,5/16f,3/16f,4/16f,10/16f,9/16f).move(10/16f,0,0));
                }
            }
        }

        if(bX == 2)
        {
            if(bZ == 11)
            {
                if(bY == 1)
                {
                    main.add(new AABB(0.25, 3/16f, 0.25, 0.75, 1, 0.75).move(4/16f,3/16f,0));
                    main.add(new AABB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75).move(4/16f,2/16f,-4/16f));
                }
                if(bY == 2)
                {
                    main.add(new AABB(0.25, 3/16f, 0.25, 0.75, 0.75f, 0.75).move(4/16f,0,0));
                    main.add(new AABB(0.25, 0.25f, 0.25, 0.75, 0.75f, 0.75).move(2/16f,0,0));
                }
            }
            if(bZ == 10)
            {
                main.add(new AABB(0, 0, -(10/16f), 8/16f, 1, 1));
                if(bY == 1)
                {
                    main.add(pipeHorizontalBF.inflate(0,0,2.5/16f).move(4/16f,2/16f,-(2.5/16f)));
                    main.add(new AABB(0,4/16f,13/16f,1,1,1).move(2/16f,0,-4/16f));
                }
            }
            if(bZ == 9)
            {
                main.add(new AABB(0, 0, 6/16f, 8/16f, 1, 2));
                if(bY==1)
                {
                    main.add(pipeHorizontalBF.inflate(0, 0, 2.5/16f).move(4/16f, 2/16f, 13.5/16f));
                    main.add(new AABB(0, 4/16f, 13/16f, 1, 1, 1).move(2/16f, 0, 12/16f));
                }
            }
            if(bZ == 8)
            {
                if(bY == 2) main.add(new AABB(0, 0, 0, 10/16f, 6/16f, 22/16f));
                if(bY != 2) main.add(new AABB(0, 0, 0, 1, 1, 22/16f));
            }
            if(bZ == 7)
            {
                if(bY != 2)
                {
                    main.add(new AABB(0, 0, 0, 8/16f, 1, 1));
                }
                else {
                    main.add(new AABB(0, 0, 0, 8/16f, 8/16f, 1));
                }
            }
            if(bZ == 5 || bZ == 4 || bZ == 2 || bZ == 1)
            {
                if(bY != 2) main.add(new AABB(0, 0, 0, 1 + (8/16f), 1, 1));
                if(bY == 2)
                {
                    main.add(new AABB(0, 0, 0, 1, 12/16f, 1));
                    main.add(new AABB(0, 0, 0, 1 + (4/16f), 8/16f, 1));
                }
            }
            if(bZ == 3 && bY == 2)
            {
                main.add(new AABB(0, 0, 0, 10/16f, 6/16f, 1));
            }
        }

        if(main.isEmpty()) main.add(new AABB(0,0,0,1,1,1));
        return main;
    }
}
