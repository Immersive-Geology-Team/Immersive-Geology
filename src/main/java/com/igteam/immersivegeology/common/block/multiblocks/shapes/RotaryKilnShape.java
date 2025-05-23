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
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RotaryKilnShape extends GenericShape {

    public static final RotaryKilnShape GETTER = new RotaryKilnShape();

    private RotaryKilnShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();
        if(bX == 6 && bY == 2)
        {
            if(bZ == 2)
            {
                main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.625, 0.4375));
            }
            if(bZ == 0)
            {
                main.add(new AABB(0, 0.0, 0.5625, 1.0, 0.625, 1));
            }
            if(bZ == 1)
            {
                main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.625, 1));
            }
        }
        else {
            if(bX == 2)
            {
                if(bY == 2)
                {
                    main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0));
                }
                else
                if(bY < 2 && bZ != 1)
                {
                    if(bY == 0) main.add(new AABB(0,0,0,1,.5,1));
                    if(bY == 1 && bZ == 0) main.add(new AABB(0,0,0.5,1,1,1));
                    if(bY == 1 && bZ == 2) main.add(new AABB(0,0,0,1,1,.5));
                }
                else
                {
                    main.add(new AABB(0,0,0,1,1,1));
                }
            }else if(bY == 0 && bX == 0)
            {
                main.add(new AABB(0.0, 0.0, 0.0, 1, 0.5, 1.0));
            } else if(bX == 3 && bY == 1 && bZ == 2)
            {
                main.add(new AABB(0,0,0.5,1,1,1));
            }
            else
            if(bX == 4)
            {
                if(bY == 2)
                {
                    main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.625, 1.0));
                }
                else
                if(bY < 2 && bZ != 1)
                {
                    if(bY == 0) main.add(new AABB(0,0,0,1,.5,1));
                    if(bY == 1 && bZ == 0) main.add(new AABB(0,0,0.5,1,1,1));
                    if(bY == 1 && bZ == 2) main.add(new AABB(0,0,0,1,1,.5));
                }
                else
                {
                    main.add(new AABB(0,0,0,1,1,1));
                }
            }
            else if(bX == 5 && bY == 2)
            {
                main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.75, 1.0));
                main.add(new AABB(0.25, 0.75, 0.25, 0.75, 1, 0.75));
            }
            else
            {
                if(bX==7)
                {
                    if(bY==2&&bZ==1)
                    {
                        main.add(new AABB(0.0, 0.0, 0.0, 0.4375, 0.625, 1.0));
                    }
                    else
                    {
                        if(bZ==1)
                        {
                            if(bY==1)
                            {
                                main.add(new AABB(0.0, 0.0, 0.0, 0.9375, 1, 1.0));
                            }
                            if(bY==0)
                            {
                                main.add(new AABB(0.0, 0.5, 0.0, 0.9375, 1, 1.0));
                                main.add(new AABB(0.0, 0.0, 0.0, 1, 0.5, 1.0));
                            }
                        }
                        else
                        {
                            main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1, 1.0));
                        }
                    }
                }
                else
                {
                    main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1, 1.0));
                }
            }
        }


        return main;
    }
}
