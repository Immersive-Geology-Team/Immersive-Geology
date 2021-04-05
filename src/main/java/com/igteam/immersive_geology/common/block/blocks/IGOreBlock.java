package com.igteam.immersive_geology.common.block.blocks;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.StoneEnum;
import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;

import java.util.ArrayList;
import java.util.List;

public class IGOreBlock extends BlockBase {

    protected StoneEnum blockStoneType;

    public IGOreBlock(String registryName, Material material, MaterialUseType useType, StoneEnum stoneType) {
        super(registryName, material, useType);
        blockStoneType = stoneType;
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
        List<ItemStack> dropList = new ArrayList<>();
        dropList.add(new ItemStack(IGRegistrationHolder.getItemByMaterial(blockMaterial, MaterialUseType.ORE_CHUNK, blockStoneType)));
        return dropList;
    }

    public StoneEnum getStoneBase(){
        return blockStoneType;
    }
}
