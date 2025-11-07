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

public class CrystallizerShape extends GenericShape {

    public static final CrystallizerShape GETTER = new CrystallizerShape();

    private CrystallizerShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();


        if(bY == 0)
        {
            main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
            if(bZ == 1) main.add(new AABB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0));
            if(bZ == 0)
            {
                if(bX == 0) main.add(new AABB(3/16f,0,3/16f,7/16f,1,7/16f));
                if(bX == 1) main.add(new AABB(6/16f,0,3/16f,10/16f,1,7/16f));
                if(bX == 2) main.add(new AABB(9/16f,0,3/16f,13/16f,1,7/16f));
            }
            if(bZ == 2)
            {
                if(bX == 0) main.add(new AABB(3/16f,0,9/16f,7/16f,1,13/16f));
                if(bX == 1) main.add(new AABB(6/16f,0,9/16f,10/16f,1,13/16f));
                if(bX == 2) main.add(new AABB(9/16f,0,9/16f,13/16f,1,13/16f));
            }
        }
        if(bY == 1)
        {
            if(bZ == 0)
            {
                if(bX == 0) main.add(new AABB(1/16f,-1/16f,1/16f,1,1,1));
                if(bX == 1) main.add(new AABB(0,-1/16f,1/16f,1,1,1));
                if(bX == 2) main.add(new AABB(0,-1/16f,1/16f,15/16f,1,1));
            }
            if(bZ == 1)
            {
                if(bX == 0) main.add(new AABB(1/16f,0,0,1,1,1));
                if(bX == 1) main.add(new AABB(0,0,0,1,1,1));
                if(bX == 2) main.add(new AABB(0,0,0,15/16f,1,1));
            }
            if(bZ == 2)
            {
                if(bX == 0) main.add(new AABB(1/16f,-1/16f,0,1,1,15/16f));
                if(bX == 1) main.add(new AABB(0,-1/16f,0,1,1,15/16f));
                if(bX == 2) main.add(new AABB(0,-1/16f,0,15/16f,1,15/16f));
            }
        }
        if(bY == 2)
        {
            if(bZ == 0)
            {
                if(bX == 0) main.add(new AABB(1/16f,-1/16f,1/16f,1,12/16f,1));
                if(bX == 1) main.add(new AABB(0,-1/16f,1/16f,1,12/16f,1));
                if(bX == 2) main.add(new AABB(0,-1/16f,1/16f,15/16f,12/16f,1));
            }
            if(bZ == 1)
            {
                if(bX == 0) main.add(new AABB(1/16f,0,0,1,12/16f,1));
                if(bX == 1)
                {
                    main.add(new AABB(0,0,0,1,1,1));
                    main.add(new AABB(1/16f,0,1/16f,15/16f,22/16f,15/16f));
                }
                if(bX == 2) main.add(new AABB(0,0,0,15/16f,12/16f,1));
            }
            if(bZ == 2)
            {
                if(bX == 0) main.add(new AABB(1/16f,-1/16f,0,1,12/16f,15/16f));
                if(bX == 1) main.add(new AABB(0,-1/16f,0,1,12/16f,15/16f));
                if(bX == 2) main.add(new AABB(0,-1/16f,0,15/16f,12/16f,15/16f));
            }
        }

        return main;
    }
}
