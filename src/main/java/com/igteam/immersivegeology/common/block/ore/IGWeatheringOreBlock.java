/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.ore;

import com.igteam.immersivegeology.common.block.IGGenericBlock;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class IGWeatheringOreBlock extends IGGenericBlock implements IOreBlock
{
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

    public IGWeatheringOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial) {
        this(flag, baseMaterial, oreMaterial, OreRichness.POOR);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(OXIDATION_UP, MineralWeathering.PRISTINE)
                .setValue(OXIDATION_DOWN, MineralWeathering.PRISTINE)
                .setValue(OXIDATION_EAST, MineralWeathering.PRISTINE)
                .setValue(OXIDATION_WEST, MineralWeathering.PRISTINE)
                .setValue(OXIDATION_NORTH, MineralWeathering.PRISTINE)
                .setValue(OXIDATION_SOUTH, MineralWeathering.PRISTINE));
    }

    public IGWeatheringOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial, OreRichness richness) {
        super(flag, baseMaterial);
        this.materialMap.put(MaterialTexture.overlay, oreMaterial);
        this.richness = richness;
        this.weathers = oreMaterial.canTarnish();

        if(!this.weathers)
        {
            // Registration only builds one of these for an ore that can oxidize, So... likely a misconfiguration.
            // The block would carry six oxidation properties it can never change, so we'd be adding
            // 729 states for nothing.
            IGLib.IG_LOGGER.error("Weathering ore block built for {}, which does not tarnish", oreMaterial.getName());
        }
    }

    private final boolean weathers;

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
    public boolean isRandomlyTicking(BlockState state)
    {
        return weathers;
    }

    public static List<EnumProperty<MineralWeathering>> OXIDATION_PROPERTIES;
    static
    {
        OXIDATION_PROPERTIES = List.of(OXIDATION_DOWN, OXIDATION_UP,  OXIDATION_NORTH, OXIDATION_SOUTH, OXIDATION_WEST, OXIDATION_EAST);
    }

    private static final Direction[] DIRECTIONS = Direction.values();

    /** Chance per random tick that one exposed face advances a stage. */
    private static final float WEATHER_CHANCE = 0.2f;

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rnd)
    {
        if(level.isClientSide) return;

        // fixed stale state referencing
        BlockState weathered = state;
        for(int i = 0; i < DIRECTIONS.length; i++)
        {
            BlockPos adjacentPos = pos.offset(DIRECTIONS[i].getNormal());
            weathered = weatherFace(weathered, level, rnd, OXIDATION_PROPERTIES.get(i), adjacentPos);
        }

        if(weathered!=state) level.setBlock(pos, weathered, 2);
    }

    private BlockState weatherFace(BlockState state, ServerLevel level, RandomSource rnd, EnumProperty<MineralWeathering> oxidationProperty, BlockPos adjacentPos)
    {
        if(!level.getBlockState(adjacentPos).isAir()) return state;

        MineralWeathering current = state.getValue(oxidationProperty);
        MineralWeathering next = nextWeatherStage(current);
        if(current.equals(next)) return state;
        if(rnd.nextFloat() >= WEATHER_CHANCE) return state;

        return state.setValue(oxidationProperty, next);
    }

    private MineralWeathering nextWeatherStage(MineralWeathering currentStage)
    {
        int ord = currentStage.ordinal();
        MineralWeathering[] values = MineralWeathering.values();
        if(ord+1 >= values.length) return currentStage;
        return values[ord + 1];
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

    @Override
    public ModFlags getModFlag()
    {
        ModFlags flag = ModFlags.MINECRAFT;
        if(materialMap.get(MaterialTexture.base).instance() instanceof MaterialStone stone){
            Set<IFlagType<?>> flags = stone.getFlags();
            for(IFlagType<?> unknown_flag : flags)
            {
                if(unknown_flag.getValue() instanceof ModFlags modFlag)
                {
                    if(Arrays.asList(ModFlags.values()).contains(modFlag))
                    {
                        flag = modFlag;
                    }
                }
            }
        }

        return flag;
    }

    @Override
    public ItemStack getItemDrop()
    {
		return this.getMaterial(MaterialTexture.overlay).getStack(this.getOreRichness().toCategory());
    }

    @Override
    public List<Pair<ItemStack, Float>> getExtraDrops()
    {
        return List.of();
    }


    @Override
    public BlockState getIGDefaultBlockState()
    {
        return defaultBlockState();
    }

    @Override
    public String getIGDescriptionId()
    {
        return getDescriptionId();
    }

    @Override
    public Item asIGItem()
    {
        return asItem();
    }

    @Override
    public @NotNull Block asIGBlock()
    {
        return asBlock();
    }
}