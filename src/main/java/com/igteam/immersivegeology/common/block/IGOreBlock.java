/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class IGOreBlock extends IGGenericBlock {

    protected final OreRichness richness;
    public static final EnumProperty<MineralWeathering> OXIDATION_UP, OXIDATION_DOWN, OXIDATION_EAST, OXIDATION_WEST, OXIDATION_NORTH, OXIDATION_SOUTH;
    static
    {
        OXIDATION_UP = EnumProperty.create("oxidation_up", MineralWeathering.class);
        OXIDATION_DOWN = EnumProperty.create("oxidation_down", MineralWeathering.class);
        OXIDATION_EAST = EnumProperty.create("oxidation_east", MineralWeathering.class);
        OXIDATION_WEST = EnumProperty.create("oxidation_west", MineralWeathering.class);
        OXIDATION_NORTH = EnumProperty.create("oxidation_north", MineralWeathering.class);
        OXIDATION_SOUTH = EnumProperty.create("oxidation_south", MineralWeathering.class);
    }

    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial) {
        this(flag, baseMaterial, oreMaterial, OreRichness.POOR);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(OXIDATION_UP, MineralWeathering.PRISTINE)
                .setValue(OXIDATION_DOWN, MineralWeathering.PRISTINE)
                .setValue(OXIDATION_EAST, MineralWeathering.PRISTINE)
                .setValue(OXIDATION_WEST, MineralWeathering.PRISTINE)
                .setValue(OXIDATION_NORTH, MineralWeathering.PRISTINE)
                .setValue(OXIDATION_SOUTH, MineralWeathering.PRISTINE));
    }

    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial, OreRichness richness) {
        super(flag, baseMaterial);
        this.materialMap.put(MaterialTexture.overlay, oreMaterial);
        this.richness = richness;
    }

    @Override
    public int getColor(int index, BlockState state) {
        if(index > 0)
        {
            MineralWeathering mineralWeathering = state.getValue(OXIDATION_PROPERTIES.get(index-1));
            return materialMap.get(MaterialTexture.values()[index > 0 ? 1: 0]).getColor(category, mineralWeathering.ordinal());
        } else {
            return materialMap.get(MaterialTexture.values()[0]).getColor(category, 0);
        }
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {

        super.initializeClient(consumer);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return materialMap.values().stream().anyMatch(MaterialInterface::hasOxidationOverTime);
    }

    public static List<EnumProperty<MineralWeathering>> OXIDATION_PROPERTIES;
    static
    {
        OXIDATION_PROPERTIES = List.of(OXIDATION_DOWN, OXIDATION_UP,  OXIDATION_NORTH, OXIDATION_SOUTH, OXIDATION_WEST, OXIDATION_EAST);
    }

    private static final Direction[] DIRECTIONS = Direction.values();

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rnd)
    {
        if (!level.isClientSide)
        {
            // Iterate over directions and corresponding oxidation properties
            for (int i = 0; i < DIRECTIONS.length; i++) {
                Direction direction = DIRECTIONS[i];
                EnumProperty<MineralWeathering> oxidationProperty = OXIDATION_PROPERTIES.get(i);
                BlockPos adjacentPos = pos.offset(direction.getNormal());

                handleOxidation(state, level, pos, rnd, oxidationProperty, adjacentPos);
            }
        }
    }

    private void handleOxidation(BlockState state, ServerLevel level, BlockPos pos, RandomSource rnd, EnumProperty<MineralWeathering> oxidationProperty, BlockPos adjacentPos)
    {
        if (level.getBlockState(adjacentPos).isAir()) {
            MineralWeathering currentOxidation = state.getValue(oxidationProperty);

            if (currentOxidation == MineralWeathering.PRISTINE && rnd.nextFloat() < 0.2) {
                level.setBlock(pos, state.setValue(oxidationProperty, MineralWeathering.TARNISHED), 2);
            }
//            else if (currentOxidation == MineralOxidation.TARNISHED && rnd.nextFloat() < 0.1) {
//                level.setBlock(pos, state.setValue(oxidationProperty, MineralOxidation.OXIDIZED), 2);
//            }
        }

//        if (level.getBlockState(adjacentPos).is(Blocks.WATER)) {
//            MineralOxidation currentOxidation = state.getValue(oxidationProperty);
//
//            if (currentOxidation == MineralOxidation.PRISTINE && rnd.nextFloat() < 0.2) {
//                level.setBlock(pos, state.setValue(oxidationProperty, MineralOxidation.TARNISHED), 2);
//            }
//        }
    }

    public OreRichness getOreRichness()
    {
        return richness;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{OXIDATION_DOWN, OXIDATION_UP, OXIDATION_EAST, OXIDATION_WEST, OXIDATION_NORTH, OXIDATION_SOUTH});
    }

    public StoneFormation getStoneFormation()
    {
        if(materialMap.get(MaterialTexture.base).instance() instanceof MaterialStone stone){
            return stone.getStoneFormation();
        }
        return null;
    }

    public ItemLike getDroppedItem()
    {
        ItemStack stack = this.getMaterial(MaterialTexture.overlay).getStack(this.getOreRichness().toCategory());
        return stack.getItem();
    }

    public enum OreRichness
    {
        POOR,
        NORMAL,
        RICH;

        public ItemCategoryFlags toCategory()
        {
            return this == POOR ? ItemCategoryFlags.POOR_ORE : (this == NORMAL ? ItemCategoryFlags.NORMAL_ORE : ItemCategoryFlags.RICH_ORE);
        }
    }

    public enum MineralWeathering implements StringRepresentable
    {
        PRISTINE,
        TARNISHED;

        @Override
        public @NotNull String getSerializedName()
        {
            return name().toLowerCase();
        }
    }
}