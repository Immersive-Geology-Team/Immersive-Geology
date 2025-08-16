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

        List<AABB> main = new ArrayList<>();
        if (bX == 0 && bY == 0 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 0 && bY == 0 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 0 && bY == 0 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.5625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 0.0625D));
            main.add(new AABB(0.0000D, 0.9375D, 0.4375D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0625D, 0.0000D, 0.5625D, 0.3125D, 1.0000D, 0.9375D));
            main.add(new AABB(0.6875D, 0.0000D, 0.5625D, 0.9375D, 1.0000D, 0.9375D));
        }
        if (bX == 0 && bY == 1 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 0 && bY == 1 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 0.0625D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 1.0000D));
            main.add(new AABB(0.0625D, 0.0000D, 0.0000D, 0.9375D, 0.1875D, 0.9375D));
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
        }
        if (bX == 0 && bY == 1 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 0.0625D));
            main.add(new AABB(0.0000D, 0.0000D, 0.4375D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 0 && bY == 2 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 1.0000D));
        }
        if (bX == 0 && bY == 2 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 0.0625D));
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
        }
        if (bX == 0 && bY == 2 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.4375D, 1.0000D, 0.0625D, 1.0000D));
        }
        if (bX == 0 && bY == 3 && bZ == 1) {
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 0.9375D, 0.8125D));
            main.add(new AABB(0.1875D, 0.3125D, 0.1875D, 1.0000D, 0.9375D, 0.8125D));
            main.add(new AABB(0.8750D, 0.1875D, 0.0625D, 1.0000D, 1.0000D, 0.9375D));
        }
        if (bX == 0 && bY == 4 && bZ == 1) {
            main.add(new AABB(0.8750D, 0.0000D, 0.0625D, 1.0000D, 0.0625D, 0.9375D));
        }
        if (bX == 1 && bY == 0 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.5625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 0.0625D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.5625D, 1.0000D, 0.6875D, 1.0000D));
            main.add(new AABB(0.0000D, 0.7500D, 0.5625D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0000D, 0.8750D, 0.5000D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.1875D, 0.0000D, 0.6875D, 0.8125D, 1.0000D, 1.0000D));
            main.add(new AABB(0.9375D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 1 && bY == 0 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.5625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 0.0625D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 0.9375D, 0.6875D, 0.4375D));
            main.add(new AABB(0.0000D, 0.0000D, 0.5625D, 0.9375D, 0.6875D, 1.0000D));
            main.add(new AABB(0.0000D, 0.7500D, 0.0000D, 0.9375D, 1.0000D, 0.4375D));
            main.add(new AABB(0.0000D, 0.7500D, 0.5625D, 0.9375D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0000D, 0.8750D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.1875D, 0.0000D, 0.0000D, 0.8125D, 1.0000D, 0.3125D));
            main.add(new AABB(0.1875D, 0.0000D, 0.6875D, 0.8125D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.6875D, 0.0625D));
            main.add(new AABB(0.0000D, 0.0000D, 0.9375D, 1.0000D, 0.6875D, 1.0000D));
            main.add(new AABB(0.9375D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 0.0625D));
            main.add(new AABB(0.9375D, 0.0000D, 0.9375D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 1 && bY == 0 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.5625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.6875D, 0.0625D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 0.0625D, 1.0000D, 0.0625D));
            main.add(new AABB(0.0000D, 0.8750D, 0.0000D, 1.0000D, 1.0000D, 0.5000D));
            main.add(new AABB(0.0000D, 0.9375D, 0.0000D, 0.0625D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0625D, 0.0000D, 0.0000D, 1.0000D, 0.6875D, 0.4375D));
            main.add(new AABB(0.0625D, 0.7500D, 0.0000D, 1.0000D, 1.0000D, 0.4375D));
            main.add(new AABB(0.1875D, 0.0000D, 0.0000D, 0.8125D, 1.0000D, 0.3125D));
            main.add(new AABB(0.9375D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 1 && bY == 1 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 0.0625D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.5000D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.5000D, 0.2500D, 0.4375D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.9375D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 1.0000D));
        }
        if (bX == 1 && bY == 1 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 1 && bY == 1 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 0.5000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 0.0625D, 1.0000D, 1.0000D));
            main.add(new AABB(0.5000D, 0.2500D, 0.0000D, 1.0000D, 1.0000D, 0.5625D));
            main.add(new AABB(0.9375D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 1.0000D));
        }
        if (bX == 1 && bY == 2 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 0.0625D, 0.0625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.5000D, 1.0000D, 0.3750D, 1.0000D));
            main.add(new AABB(0.0000D, 0.6875D, 0.5000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 1 && bY == 2 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.3750D, 1.0000D));
            main.add(new AABB(0.0000D, 0.6875D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0625D, 0.0000D, 0.0625D, 0.9375D, 0.5000D, 0.9375D));
            main.add(new AABB(0.0625D, 0.5625D, 0.0625D, 0.9375D, 1.0000D, 0.9375D));
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
        }
        if (bX == 1 && bY == 2 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.3750D, 0.5000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 0.0625D, 0.0625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.6875D, 0.0000D, 1.0000D, 1.0000D, 0.5000D));
        }
        if (bX == 1 && bY == 3 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.5000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 1 && bY == 3 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 1 && bY == 3 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 0.5000D));
        }
        if (bX == 1 && bY == 4 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.5000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 1 && bY == 4 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 1 && bY == 4 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 0.5000D));
        }
        if (bX == 1 && bY == 5 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.5000D, 1.0000D, 0.5625D, 1.0000D));
        }
        if (bX == 1 && bY == 5 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.5625D, 1.0000D));
            main.add(new AABB(0.5625D, 0.0000D, 0.0625D, 1.0000D, 0.6875D, 0.9375D));
            main.add(new AABB(0.6875D, 0.0000D, 0.1875D, 1.0000D, 1.0000D, 0.8125D));
        }
        if (bX == 1 && bY == 5 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.5625D, 0.5000D));
        }
        if (bX == 1 && bY == 6 && bZ == 1) {
            main.add(new AABB(0.6875D, 0.0000D, 0.1875D, 1.0000D, 0.4375D, 0.8125D));
        }
        if (bX == 2 && bY == 0 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 2 && bY == 0 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.5625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 0.0625D));
            main.add(new AABB(0.0000D, 0.0000D, 0.9375D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0000D, 0.8750D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0625D, 0.0000D, 0.0000D, 0.9375D, 0.6875D, 1.0000D));
            main.add(new AABB(0.0625D, 0.7500D, 0.0000D, 0.9375D, 1.0000D, 1.0000D));
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
        }
        if (bX == 2 && bY == 0 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 2 && bY == 1 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.5000D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0000D, 0.2500D, 0.4375D, 0.5000D, 1.0000D, 1.0000D));
        }
        if (bX == 2 && bY == 1 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 2 && bY == 1 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 0.5000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.2500D, 0.0000D, 0.5000D, 1.0000D, 0.5625D));
        }
        if (bX == 2 && bY == 2 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.5000D, 1.0000D, 0.3750D, 1.0000D));
            main.add(new AABB(0.0000D, 0.6875D, 0.5000D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0625D, 0.0000D, 0.5625D, 0.9375D, 0.5000D, 1.0000D));
            main.add(new AABB(0.0625D, 0.5625D, 0.5625D, 0.9375D, 1.0000D, 1.0000D));
            main.add(new AABB(0.1875D, 0.0000D, 0.6875D, 0.8125D, 1.0000D, 1.0000D));
        }
        if (bX == 2 && bY == 2 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.3750D, 1.0000D));
            main.add(new AABB(0.0000D, 0.6875D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0625D, 0.0000D, 0.0000D, 0.9375D, 0.5000D, 0.4375D));
            main.add(new AABB(0.0625D, 0.0000D, 0.5625D, 0.9375D, 0.5000D, 1.0000D));
            main.add(new AABB(0.0625D, 0.5625D, 0.0000D, 0.9375D, 1.0000D, 0.4375D));
            main.add(new AABB(0.0625D, 0.5625D, 0.5625D, 0.9375D, 1.0000D, 1.0000D));
            main.add(new AABB(0.1875D, 0.0000D, 0.0000D, 0.8125D, 1.0000D, 0.3125D));
            main.add(new AABB(0.1875D, 0.0000D, 0.6875D, 0.8125D, 1.0000D, 1.0000D));
        }
        if (bX == 2 && bY == 2 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.3750D, 0.5000D));
            main.add(new AABB(0.0000D, 0.6875D, 0.0000D, 1.0000D, 1.0000D, 0.5000D));
            main.add(new AABB(0.0625D, 0.0000D, 0.0000D, 0.9375D, 0.5000D, 0.4375D));
            main.add(new AABB(0.0625D, 0.5625D, 0.0000D, 0.9375D, 1.0000D, 0.4375D));
            main.add(new AABB(0.1875D, 0.0000D, 0.0000D, 0.8125D, 1.0000D, 0.3125D));
        }
        if (bX == 2 && bY == 3 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.5000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 2 && bY == 3 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 2 && bY == 3 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 0.5000D));
        }
        if (bX == 2 && bY == 4 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.5000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 2 && bY == 4 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 2 && bY == 4 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 0.5000D));
        }
        if (bX == 2 && bY == 5 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.5000D, 1.0000D, 0.5625D, 1.0000D));
        }
        if (bX == 2 && bY == 5 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.5625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0625D, 0.4375D, 0.6875D, 0.9375D));
            main.add(new AABB(0.0000D, 0.0000D, 0.1875D, 0.3125D, 1.0000D, 0.8125D));
            main.add(new AABB(0.0000D, 0.8125D, 0.1875D, 1.0000D, 1.0000D, 0.8125D));
        }
        if (bX == 2 && bY == 5 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.5625D, 0.5000D));
        }
        if (bX == 2 && bY == 6 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.1875D, 1.0000D, 0.4375D, 0.8125D));
        }
        if (bX == 3 && bY == 0 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 3 && bY == 0 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.5625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 0.0625D));
            main.add(new AABB(0.0000D, 0.0000D, 0.3125D, 0.8125D, 1.0000D, 0.4375D));
            main.add(new AABB(0.0000D, 0.0000D, 0.9375D, 1.0000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.0625D, 0.0000D, 0.3125D, 0.8125D, 1.0000D, 0.5000D));
            main.add(new AABB(0.1250D, 0.0000D, 0.2500D, 0.8125D, 1.0000D, 0.6250D));
            main.add(new AABB(0.1875D, 0.0000D, 0.2500D, 0.8125D, 1.0000D, 0.7500D));
            main.add(new AABB(0.2500D, 0.0000D, 0.1875D, 0.7500D, 1.0000D, 0.8125D));
            main.add(new AABB(0.2500D, 0.0000D, 0.1875D, 0.6250D, 1.0000D, 0.8750D));
            main.add(new AABB(0.3125D, 0.0000D, 0.1875D, 0.5000D, 1.0000D, 1.0000D));
            main.add(new AABB(0.3750D, 0.0000D, 0.1250D, 0.7500D, 1.0000D, 0.8125D));
            main.add(new AABB(0.5000D, 0.0000D, 0.0000D, 0.6875D, 1.0000D, 0.8125D));
            main.add(new AABB(0.1875D, 0.0000D, 0.3750D, 0.8750D, 1.0000D, 0.7500D));
            main.add(new AABB(0.1875D, 0.0000D, 0.5000D, 0.9375D, 1.0000D, 0.6875D));
            main.add(new AABB(0.1875D, 0.0000D, 0.5625D, 1.0000D, 1.0000D, 0.6875D));
        }
        if (bX == 3 && bY == 0 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 1.0000D, 1.0000D));
        }
        if (bX == 3 && bY == 1 && bZ == 0) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 1.0000D));
            main.add(new AABB(0.0625D, 0.0000D, 0.0625D, 0.9375D, 0.1875D, 0.9375D));
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
            main.add(new AABB(0.1875D, 0.5000D, 0.1875D, 0.8125D, 1.0000D, 1.0000D));
        }
        if (bX == 3 && bY == 1 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 0.0625D));
            main.add(new AABB(0.0000D, 0.0000D, 0.3125D, 0.8125D, 0.1875D, 0.4375D));
            main.add(new AABB(0.0000D, 0.0000D, 0.9375D, 1.0000D, 0.0625D, 1.0000D));
            main.add(new AABB(0.0000D, 0.3750D, 0.0625D, 0.1250D, 1.0000D, 0.9375D));
            main.add(new AABB(0.0625D, 0.0000D, 0.3125D, 0.8125D, 0.1875D, 0.5000D));
            main.add(new AABB(0.1250D, 0.0000D, 0.2500D, 0.8125D, 0.1875D, 0.6250D));
            main.add(new AABB(0.0000D, 0.5000D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
            main.add(new AABB(0.1875D, 0.0000D, 0.2500D, 0.8125D, 0.1875D, 0.7500D));
            main.add(new AABB(0.1875D, 0.5000D, 0.0000D, 0.8125D, 1.0000D, 0.8125D));
            main.add(new AABB(0.2500D, 0.0000D, 0.1875D, 0.7500D, 0.1875D, 0.8125D));
            main.add(new AABB(0.2500D, 0.0000D, 0.1875D, 0.6250D, 0.1875D, 0.8750D));
            main.add(new AABB(0.3125D, 0.0000D, 0.1875D, 0.5000D, 0.1875D, 0.9375D));
            main.add(new AABB(0.3125D, 0.0000D, 0.1875D, 0.4375D, 0.1875D, 1.0000D));
            main.add(new AABB(0.3750D, 0.0000D, 0.1250D, 0.7500D, 0.1875D, 0.8125D));
            main.add(new AABB(0.5000D, 0.0000D, 0.0625D, 0.6875D, 0.1875D, 0.8125D));
            main.add(new AABB(0.5625D, 0.0000D, 0.0000D, 0.6875D, 0.1875D, 0.8125D));
            main.add(new AABB(0.1875D, 0.0000D, 0.3750D, 0.8750D, 0.1875D, 0.7500D));
            main.add(new AABB(0.1875D, 0.0000D, 0.5000D, 0.9375D, 0.1875D, 0.6875D));
            main.add(new AABB(0.1875D, 0.0000D, 0.5625D, 1.0000D, 0.1875D, 0.6875D));
        }
        if (bX == 3 && bY == 1 && bZ == 2) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0000D, 1.0000D, 0.0625D, 1.0000D));
            main.add(new AABB(0.0625D, 0.0000D, 0.0625D, 0.9375D, 0.1875D, 0.9375D));
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
        }
        if (bX == 3 && bY == 2 && bZ == 0) {
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 0.1250D, 1.0000D));
        }
        if (bX == 3 && bY == 2 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.0625D, 0.1250D, 0.2500D, 0.9375D));
            main.add(new AABB(0.0000D, 0.0000D, 0.1875D, 0.8125D, 0.1250D, 0.8125D));
            main.add(new AABB(0.1875D, 0.0000D, 0.0000D, 0.8125D, 0.1250D, 0.8125D));
        }
        if (bX == 3 && bY == 2 && bZ == 2) {
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
        }
        if (bX == 3 && bY == 3 && bZ == 2) {
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
        }
        if (bX == 3 && bY == 4 && bZ == 2) {
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
        }
        if (bX == 3 && bY == 5 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.8125D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
            main.add(new AABB(0.1875D, 0.8125D, 0.1875D, 0.8125D, 1.0000D, 1.0000D));
        }
        if (bX == 3 && bY == 5 && bZ == 2) {
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 1.0000D, 0.8125D));
            main.add(new AABB(0.1875D, 0.8125D, 0.0000D, 0.8125D, 1.0000D, 0.8125D));
        }
        if (bX == 3 && bY == 6 && bZ == 1) {
            main.add(new AABB(0.0000D, 0.0000D, 0.1875D, 0.8125D, 0.4375D, 0.8125D));
            main.add(new AABB(0.1875D, 0.0000D, 0.1875D, 0.8125D, 0.4375D, 1.0000D));
        }
        if (bX == 3 && bY == 6 && bZ == 2) {
            main.add(new AABB(0.1875D, 0.0000D, 0.0000D, 0.8125D, 0.4375D, 0.8125D));
        }
        return main;
    }
}
