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

public class GeothermalExchangerShape extends GenericShape {

    public static final GeothermalExchangerShape GETTER = new GeothermalExchangerShape();

    private GeothermalExchangerShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();
        if(bY == 5 && bZ == 1 && bX == 2)
        {
            main.add(new AABB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));
        }
        else if(bY == 4)
        {
            main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
            if(bZ == 1)
            {
                if(bX == 1 || bX == 3)
                {
                    main.add(new AABB(0.0, 0.5, 0.0, 1.0, 1, 1.0));
                }
                if(bX == 2)
                {
                    main.add(new AABB(0.125, 0.5, 0.5, 0.25, 1, 1.0));
                    main.add(new AABB(0.75, 0.5, 0.5, 0.875, 1, 1.0));
                }
            }
            if(bX == 3 && bZ == 0) main.add(new AABB(0.0, 0.5, 0.0, 1.0, 1, 1.0));
        }
        else
        {
            main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
        }
        return main;
    }
}
