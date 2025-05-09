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

public class PelletizerShape extends GenericShape {

    public static final PelletizerShape GETTER = new PelletizerShape();

    private PelletizerShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();

        if (bY == 0) {
            main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));

            boolean shouldAddTopBox = (bX > 0 && bZ == 0) || (bX == 1 && bZ == 1) || ((bX == 0 || bX == 1) && bZ == 3);

            if (shouldAddTopBox) {
                main.add(new AABB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0));
            }

            if(bX == 0)
            {
                if(bZ == 1)
                {
                    main.add(new AABB(0.375, 0.5, 0.1875, 0.625, 0.75, 1.0));
                    main.add(new AABB(0.5625, 0.5, 0.125, 0.625, 0.75, 1.0));

                    main.add(new AABB(0.625, 0.5, 0.125, 1, .75, 0.375));
                    main.add(new AABB(0.75, 0.5, 0.125, 1, 1, 0.375));
                }
                if(bZ == 2)
                {
                    main.add(new AABB(0.375, 0.5, 0, 0.625, 0.75, 0.875));
                    main.add(new AABB(0.375, 0.5, 0.625, 0.625, 1.0625, 0.875));

                    main.add(new AABB(0.3125, 0.75, 0.9375, 0.6875, 1.125, 1));

                    main.add(new AABB(0.375, 0.8125, 0.875, 0.625, 1.0625, 0.9375));
                }
            }
        }

        if(bY == 1)
        {
            if(bX == 1 && bZ == 0)
            {
                main.add(new AABB(0.25, 0, 0.25, 0.75, 0.0625, 0.75));
                main.add(new AABB(0.375, 0.0625, 0.375, 0.625, 0.6875, 0.625));
                main.add(new AABB(0.375, 0.5, 0.5625, 0.625, 0.75, 1));

                main.add(new AABB(0.3125, 0.4375, 0.9375, 0.6875, 0.8125, 1));
            }
            if(bX == 0 && bZ == 3)
            {
                main.add(new AABB(0.0, 0, 0.0, 1.0, 1.0, 1.0));
            }
            if (bX == 1 && bZ == 1) {
                // Center cube
                main.add(new AABB(0.3125, 0, 0.3125, 0.6875, 0.6875, 0.6875));

                double yTop = 0.9375;
                double zStart = 0.0;
                double yStep = 0.0625;

                // First segment (3/16 width)
                double z1 = zStart;
                double z2 = z1 + 0.1875;  // 3/16
                main.add(new AABB(0.125, 0.3125, z1, 0.875, yTop, z2));

                // Remaining 6 segments (2/16 width)
                z1 = z2;
                for (int i = 1; i <= 6; i++) {
                    z2 = z1 + 0.125; // 2/16
                    double y2 = yTop - i * yStep;
                    main.add(new AABB(0.125, 0.3125, z1, 0.875, y2, z2));
                    z1 = z2;
                }

                main.add(new AABB(-0.25, 0, 0.125, 0, 0.625, 0.375));
                main.add(new AABB(-0.0625, 0.4375, 0.125, 0.125, 0.6875, 0.375));
                main.add(new AABB(0.0625, 0.375, 0.0625, 0.125, 0.75, 0.4375));
            }

        }

        return main;
    }
}
