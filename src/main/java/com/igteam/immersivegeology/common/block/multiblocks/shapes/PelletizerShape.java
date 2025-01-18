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
        if(bX == 2 && bY == 2 && bZ == 3) main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
        else {
            main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
        }
        return main;
    }
}
