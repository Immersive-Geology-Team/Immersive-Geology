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
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IGOreBlock extends IGGenericBlock {

    protected final OreRichness richness;
    public static final EnumProperty<MineralOxidation> OXIDATION;
    static
    {
        OXIDATION = EnumProperty.create("oxidation", MineralOxidation.class);
    }

    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial) {
        this(flag, baseMaterial, oreMaterial, OreRichness.POOR);
        this.registerDefaultState(this.defaultBlockState().setValue(OXIDATION, MineralOxidation.PRISTINE));
    }

    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial, OreRichness richness) {
        super(flag, baseMaterial);
        this.materialMap.put(MaterialTexture.overlay, oreMaterial);
        this.richness = richness;
    }

    @Override
    public int getColor(int index, BlockState state) {
        MineralOxidation mineralOxidation = state.getValue(OXIDATION);
        return materialMap.get(MaterialTexture.values()[index]).getColor(category, mineralOxidation.ordinal());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return materialMap.values().stream().anyMatch(MaterialInterface::hasOxidationOverTime);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rnd)
    {
        if (!level.isClientSide)
        {
            if(hasExposedFace(level, pos))
            {
                MineralOxidation currentOxidation = state.getValue(OXIDATION);
                if (currentOxidation == MineralOxidation.PRISTINE && rnd.nextFloat() < 0.2) {
                    level.setBlock(pos, state.setValue(OXIDATION, MineralOxidation.TARNISHED), 2);
                } else if (currentOxidation == MineralOxidation.TARNISHED && rnd.nextFloat() < 0.1) {
                    level.setBlock(pos, state.setValue(OXIDATION, MineralOxidation.OXIDIZED), 2);
                }
            }
        }
    }

    private boolean hasExposedFace(ServerLevel level, BlockPos pos)
    {
        return level.getBlockState(pos.above()).isAir() ||
                level.getBlockState(pos.below()).isAir() ||
                level.getBlockState(pos.north()).isAir() ||
                level.getBlockState(pos.south()).isAir() ||
                level.getBlockState(pos.east()).isAir() ||
                level.getBlockState(pos.west()).isAir();
    }

    public OreRichness getOreRichness()
    {
        return richness;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{OXIDATION});
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

    public enum MineralOxidation implements StringRepresentable
    {
        PRISTINE,
        TARNISHED,
        OXIDIZED;

        @Override
        public @NotNull String getSerializedName()
        {
            return name().toLowerCase();
        }
    }
}