/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block;

import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

public class IGOreBlock extends IGGenericBlock implements IOreBlock
{
    protected final OreRichness richness;

    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial, OreRichness richness) {
        super(flag, baseMaterial);
        this.materialMap.put(MaterialTexture.overlay, oreMaterial);
        this.richness = richness;
    }

    @Override
    public int getColor(int index, BlockState state) {
        if(index > 0)
        {
            return materialMap.get(MaterialTexture.values()[1]).getColor(category, 0);
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
        return materialMap.values().stream().anyMatch(MaterialInterface::canTarnish);
    }

    @Override
    public BlockState getDefaultBlockState()
    {
        return defaultBlockState();
    }

    @Override
    public @NotNull Block asBlock()
    {
        return this;
    }

    public OreRichness getOreRichness()
    {
        return richness;
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
        MaterialInterface<?> ore_mat = getMaterial(MaterialTexture.overlay);
        if(ore_mat.hasFlag(ItemCategoryFlags.POOR_ORE) && ore_mat.hasFlag(ItemCategoryFlags.RICH_ORE))
        {
			return ore_mat.getStack(this.getOreRichness().toCategory());
        }
		return ore_mat.getStack(ItemCategoryFlags.NORMAL_ORE, 1 + this.getOreRichness().ordinal());
    }
}