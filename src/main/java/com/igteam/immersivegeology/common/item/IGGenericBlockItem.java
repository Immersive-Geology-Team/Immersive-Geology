/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IGGenericBlockItem extends BlockItem implements IGFlagItem {

    private final IGBlockType block;

    public IGGenericBlockItem(IGBlockType block) {
        super(block.getBlock(), new Properties());
        this.block = block;
    }

    @Override
    public IFlagType<?> getFlag() {
        return block.getFlag();
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return block.getGroup();
    }

    @Override
    public Collection<MaterialInterface<?>> getMaterials() {
        return block.getMaterials();
    }

    @Override
    public MaterialInterface<?> getMaterial(MaterialTexture t) {
        return block.getMaterial(t);
    }

    @Override
    public int getColor(int index) {
        return this.block.getColor(index);
    }

    @Override
    public @NotNull Component getName(ItemStack pStack) {
        Map<MaterialTexture, MaterialInterface<?>> materialMap = block.getMaterialMap();
        List<String> materialList = new ArrayList<>();

        if(getFlag().equals(BlockCategoryFlags.ORE_BLOCK)) {
            if(getBlock() instanceof IGOreBlock oreBlock){
                materialList.add(I18n.get("material.immersivegeology.ore." + oreBlock.getOreRichness().name().toLowerCase()));
                materialList.add(I18n.get("material.immersivegeology." + materialMap.get(MaterialTexture.base).getName()));
                materialList.add(I18n.get("material.immersivegeology." + materialMap.get(MaterialTexture.overlay).getName()));
            }
        } else {
            for(MaterialTexture t : MaterialTexture.values()){
                if (materialMap.containsKey(t)) {
                    materialList.add(I18n.get("material.immersivegeology." + materialMap.get(t).getName()));
                }
            }
        }

        return Component.translatable("block.immersivegeology." + block.getFlag().getName(), materialList.toArray());
    }
}
