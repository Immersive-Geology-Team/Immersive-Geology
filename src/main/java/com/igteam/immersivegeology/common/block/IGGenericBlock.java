/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IGGenericBlock extends Block implements IGBlockType {
    protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
    protected final BlockCategoryFlags category;

    public IGGenericBlock(BlockCategoryFlags flag, MaterialInterface<?> material) {
        this(flag, material, material.instance().getProperties().mapColor(MapColor.COLOR_GRAY));
    }

    public IGGenericBlock(BlockCategoryFlags flag, MaterialInterface<?> material, Properties props) {
        super(props);
        this.materialMap.put(MaterialTexture.base, material);
        this.category = flag;
    }

    public IFlagType<?> getFlag() {
        return category;
    }

    public ItemSubGroup getGroup() {
        return category.getSubGroup();
    }

    @Override
    public int getColor(int index, BlockState state) {
        // By default, we don't need any additional information; the secondaryColors are used for mineral oxidation
        // or other state based color changes
        if(materialMap == null || materialMap.isEmpty()) return 0xffffffff;
        if(index > materialMap.size()) return materialMap.get(MaterialTexture.values()[0]).getColor(category, 0);
        return materialMap.get(MaterialTexture.values()[index > 0 ? 1 : 0]).getColor(category, 0);
    }

    public @NotNull Collection<MaterialInterface<?>> getMaterials() {
        return materialMap.values();
    }

    @Override
    public MaterialInterface<?> getMaterial(MaterialTexture t) {
        return materialMap.get(t);
    }
    @Override
    public Block getIGBlock() {
        return this;
    }

    @Override
    public Map<MaterialTexture, MaterialInterface<?>> getMaterialMap() {
        return materialMap;
    }
}
