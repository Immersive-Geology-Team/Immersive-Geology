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

public class SmallChemicalReactorShape extends GenericShape {

    public static final SmallChemicalReactorShape GETTER = new SmallChemicalReactorShape();

    private SmallChemicalReactorShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();
        final AABB pipeConnectionBottom = new AABB(0.125, 0, 0.125, 0.875, 0.125, 0.875);
        final AABB pipeMiddleVertical = new AABB(0.25, 0, 0.25, 0.75, 1, 0.75);
        final AABB fullBlock = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        final AABB slabTop = new AABB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
        final AABB slabBottom = new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
        final AABB pipeVerticalBend = new AABB(0.25, 0, 0.25, 0.75, 0.875, 0.75);
        final AABB pipeHorizontalLR = new AABB(0, 0.25, 0.25, 1, 0.75, 0.75);
        final List<AABB> redstonePanelLegs = List.of(
                new AABB(0.125, 0.5, 0.625, 0.25, 1, 0.875),
                new AABB(0.75, 0.5, 0.625, 0.875, 1, 0.875)
        );
        List<AABB> main = new ArrayList<>();

        if (bY == 5) {
            if (bX == 3 && (bZ == 1 || bZ == 2)) {
                float z = (bZ == 2) ? -(2/16f) : 6/16f;
                if (bZ == 2) {
                    main.add(pipeMiddleVertical.move(0, 6/16f, 0));
                } else {
                    main.add(pipeHorizontalLR.move(-4/16f, 10/16f, 0));
                }
                main.add(new AABB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75).move(0, 10/16f, z));
                if (bZ == 1) return main;
            }
            if (bX == 2 && bZ == 1) {
                main.add(pipeConnectionBottom.move(-8/16f, 8/16f, 0));
                main.add(pipeVerticalBend.move(-8/16f, 8/16f, 0));
                main.add(pipeHorizontalLR.move(-4/16f, 10/16f, 0));
                return main;
            }
        }

        if (bY == 4 && (bZ == 0 || bZ == 1)) {
            float zOffset = bZ == 0 ? 9/16f : 8/16f;
            if (bX == 1) main.add(new AABB(1/16f, 0, 0, 1, 1.5f, 15/16f).move(0, 0, zOffset));
            if (bX == 2) main.add(new AABB(0, 0, 0, 15/16f, 1.5f, 15/16f).move(0, 0, zOffset));
            return main;
        }



        if(bX == 3 && bZ == 2 && bY >= 1 && bY < 6)
        {
            if(bY == 1) main.add(pipeConnectionBottom);
            main.add(pipeMiddleVertical);
            return main;
        }

        if(bX == 0)
        {
            if(bZ == 1 && bY >= 1 && bY < 3)
            {
                if(bY == 1) main.add(pipeConnectionBottom);
                main.add(pipeMiddleVertical);
                return main;
            }
            if(bY == 3)
            {
                main.add(pipeVerticalBend);
                main.add(new AABB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75).move(0.25f,0.125f,0));
                main.add(new AABB(14/16f,2/16f,2/16f,1,14/16f,14/16f).move(1/16f,0.125f,0));
                return main;
            }

            if(bZ==2)
            {
                if(bY == 1)main.add(new AABB(0, 0, 0.5, 1, 1, 1));
                if(bY == 0)
                {
                    main.add(slabBottom);
                    main.addAll(redstonePanelLegs);
                }
                return main;
            }
        }
        if(bX == 1 && bY == 0 && bZ == 2)
        {
            main.add(slabBottom);
            return main;
        }

        if (bY == 3 && (bZ == 0 || bZ == 1)) {
            float zOffset = bZ == 0 ? 9/16f : 8/16f;
            if (bX == 1) main.add(new AABB(1/16f, -(4/16f), 0, 1, 1f, 15/16f).move(0, 0, zOffset));
            if (bX == 2) main.add(new AABB(0, -(4/16f), 0, 15/16f, 1f, 15/16f).move(0, 0, zOffset));
            return main;
        }

        main.add(new AABB(0,0,0,1,1,1));
        return main;
    }
}
