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
import java.util.function.Function;
import java.util.function.UnaryOperator;

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


        final List<AABB> redstonePanelLegs = List.of(
                new AABB(0.125, 0.5, 0.625, 0.25, 1, 0.875),
                new AABB(0.75, 0.5, 0.625, 0.875, 1, 0.875)
        );
        // ===== Conditional Shape Logic =====
        List<AABB> left_tank = tank_blocks(main, bX, bY, bZ, false);
        if(left_tank != null) return left_tank;
        List<AABB> right_tank = tank_blocks(main, bX, bY, bZ, true);
        if(right_tank != null) return right_tank;

        // Pipe center column
        if((bY==4||bY==5)&&(bZ==2||bZ==0)&&bX==2)
        {
            main.add(pipeMiddleVertical);
            if(bY==4) main.add(pipeConnectionBottom);
            return main;
        }

        if(bX == 3 && bY == 6 && bZ == 2)
        {
            main.add(pipeHorizontalLR.move(0,0.125f,0));
            main.add(new AABB(9/16f,6/16f,0,17/16f,14/16f,12/16f));
            return main;
        }

        if(bX == 3 && bY == 6 && bZ == 1)
        {
            main.add(pipeVerticalBend.move(0.3125f,0,-0.5));
            main.add(new AABB(9/16f,6/16f,0,17/16f,14/16f,16/16f));
            main.add(pipeConnectionBottom.move(0.3125f,0,-0.5));
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


    private List<AABB> tank_blocks(List<AABB> main, int bX, int bY, int bZ, boolean offset)
    {
        int xText = offset ? 3 : 0;

        UnaryOperator<AABB> mirrorIfOffset = aabb -> {
            if (!offset) return aabb;
            double minX = aabb.minX, maxX = aabb.maxX;
            return new AABB(
                    1.0 - maxX, aabb.minY, aabb.minZ,
                    1.0 - minX, aabb.maxY, aabb.maxZ
            );
        };

		final AABB leftTankBottomFront = mirrorIfOffset.apply(
                new AABB(0.5, -0.0625, 0.0, 1.0, 1.0, 0.9375)
        );

        final AABB rightTankBottomFront = mirrorIfOffset.apply(
                new AABB(0.0, -0.0625, 0.0, 0.875, 1.0, 0.9375)
        );

        final List<AABB> leftTankTopFront = List.of(
                mirrorIfOffset.apply(new AABB(0.5, 0.0, 0.0, 1.0, 0.8125, 0.9375)),
                mirrorIfOffset.apply(new AABB(0.6875, 0.8125, 0.0, 1.0, 1, 0.5))
        );

        final List<AABB> leftTankTopBack = List.of(
                mirrorIfOffset.apply(new AABB(0.5, 0.0, 0.0625, 1.0, 0.8125, 1.0)),
                mirrorIfOffset.apply(new AABB(0.6875, 0.8125, 0.5, 1.0, 1, 1.0))
        );

        final List<AABB> rightTankTopFront = List.of(
                mirrorIfOffset.apply(new AABB(0.0, 0.0, 0.0, 0.875, 0.8125, 0.9375)),
                mirrorIfOffset.apply(new AABB(0.0, 0.8125, 0.0, 0.6875, 1, 0.5))
        );

        final List<AABB> rightTankTopBack = List.of(
                mirrorIfOffset.apply(new AABB(0.0, 0.0, 0.0625, 0.875, 0.8125, 1.0)),
                mirrorIfOffset.apply(new AABB(0.0, 0.8125, 0.5, 0.6875, 1, 1.0))
        );

        int localX = offset ? 1 - (bX - 3) : bX - xText;

		// Logic blocks
        if (bZ== 1 && localX == 0) {
            if (bY == 4) {
                main.add(leftTankBottomFront);
                return main;
            }
            if (bY == 5) {
                main.addAll(leftTankTopFront);
                return main;
            }
        }

        if (bZ== 1 && localX == 1) {
            if (bY == 4) {
                main.add(rightTankBottomFront);
                return main;
            }
            if (bY == 5) {
                main.addAll(rightTankTopFront);
                return main;
            }
        }

        if (bZ== 0 && localX == 0) {
            if (bY == 4) {
                main.add(leftTankBottomFront.move(0, 0, 0.0625f));
                return main;
            }
            if (bY == 5) {
                main.addAll(leftTankTopBack);
                return main;
            }
        }

        if (bZ== 0 && localX == 1) {
            if (bY == 4) {
                main.add(rightTankBottomFront.move(0, 0, 0.0625f));
                return main;
            }
            if (bY == 5) {
                main.addAll(rightTankTopBack);
                return main;
            }
        }

        return null;
    }
}
