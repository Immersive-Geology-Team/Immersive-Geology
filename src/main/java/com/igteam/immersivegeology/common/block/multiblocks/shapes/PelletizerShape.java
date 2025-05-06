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
        }

        if(bY == 1)
        {
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
            }

        }

        return main;
    }
}
