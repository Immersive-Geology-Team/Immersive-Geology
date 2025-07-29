/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GeothermalExchangerShape extends GenericShape {

    public static final GeothermalExchangerShape GETTER = new GeothermalExchangerShape();

    private GeothermalExchangerShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock)
    {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();

        // AABB Definitions
        final AABB redstonePanel = new AABB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0);
        final AABB fullBlock = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        final AABB slabTop = new AABB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
        final AABB slabBottom = new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
        final AABB pipeConnectionBottom = new AABB(0.125, 0, 0.125, 0.875, 0.125, 0.875);
        final AABB pipeMiddleVertical = new AABB(0.25, 0, 0.25, 0.75, 1, 0.75);
        final AABB pipeVerticalBend = new AABB(0.25, 0, 0.25, 0.75, 0.875, 0.75);
        final AABB pipeHorizontalLR = new AABB(0, 0.25, 0.25, 1, 0.75, 0.75);
        final AABB leftTankBottomFront = new AABB(0.5, -0.0625, 0.0, 1.0, 1.0, 0.9375);

        final List<AABB> leftTankTopFront = List.of(
                new AABB(0.5, 0.0, 0.0, 1.0, 0.8125, 0.9375),
                new AABB(0.6875, 0.8125, 0.0, 1.0, 1, 0.5)
        );

        final List<AABB> leftTankTopBack = List.of(
                new AABB(0.5, 0.0, 0.0, 1.0, 0.8125, 0.9375).move(0,0,0.0625),
                new AABB(0.6875, 0.8125, 0.0, 1.0, 1, 0.5).move(0,0,.5)
        );

        final List<AABB> redstonePanelLegs = List.of(
                new AABB(0.125, 0.5, 0.625, 0.25, 1, 0.875),
                new AABB(0.75, 0.5, 0.625, 0.875, 1, 0.875)
        );

        // ===== Conditional Shape Logic =====

        // Pipe center column
        if((bY==4||bY==5)&&(bZ==2||bZ==0)&&bX==2)
        {
            main.add(pipeMiddleVertical);
            if(bY==4) main.add(pipeConnectionBottom);
            return main;
        }

        // Top pipe bend and horizontal extension
        if(bY==6&&(bZ==2||bZ==0)&&bX==2)
        {
            main.add(pipeVerticalBend);
            float offset = (bZ==0)?-0.25f: 0.25f;
            main.add(pipeHorizontalLR.deflate(0.25, 0, 0).move(offset, 0.125f, 0));
            return main;
        }

        // L-shaped pipe segment
        if(bY==6&&bZ==0&&bX==1)
        {
            main.add(pipeConnectionBottom.move(-0.3125, 0, 0.5));
            main.add(pipeVerticalBend.move(-0.3125, 0, 0.5));
            main.add(pipeHorizontalLR.move(-0.03125, 0.125, 0).inflate(0.03125, 0, 0)); // 1px thickness fix
            return main;
        }

        // Left tank front (bottom and top)
        if(bZ==1&&bX==0)
        {
            if(bY==4)
            {
                main.add(leftTankBottomFront);
                return main;
            }
            if(bY==5)
            {
                main.addAll(leftTankTopFront);
                return main;
            }
        }

        if(bZ==0&&bX==0)
        {
            if(bY==4)
            {
                main.add(leftTankBottomFront.move(0,0,0.0625f));
                return main;
            }
            if(bY==5)
            {
                main.addAll(leftTankTopBack);
                return main;
            }
        }

        // Redstone panel and legs
        if(bY==4&&bZ==2&&bX==3)
        {
            main.add(redstonePanel);
            return main;
        }

        if(bY==3)
        {
            main.add(slabBottom);

            if(bZ==2)
            {
                if(bX==1||bX==2)
                {
                    main.add(slabTop);
                }
                else if(bX==3)
                {
                    main.addAll(redstonePanelLegs);
                }
            }

            if(bX==2&&bZ==0)
            {
                main.add(slabTop);
            }

            return main;
        }

        // Default case
        main.add(fullBlock);
        return main;
    }
}
