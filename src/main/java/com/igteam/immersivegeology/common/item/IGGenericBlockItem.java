/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.IGDeskBlock;
import com.igteam.immersivegeology.common.block.IGWeatheringOreBlock;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return this.block.getColor(index > 0 ? 1 : 0, this.block.getBlock().defaultBlockState());
    }

    @Override
    protected boolean placeBlock(@NotNull BlockPlaceContext context, @NotNull BlockState state)
    {
        Block b = state.getBlock();
        if(b instanceof IGDeskBlock<?> desk)
        {
            boolean ret = super.placeBlock(context, state);
            if(ret) desk.onIEBlockPlacedBy(context, state);
            return ret;
        }
        return super.placeBlock(context, state);
    }

    @Override
    public @NotNull Component getName(ItemStack pStack) {
        if(getBlock() instanceof IGDeskBlock<?>)
        {
            return Component.translatable("item.immersivegeology.drawing_table");
        }

        Map<MaterialTexture, MaterialInterface<?>> materialMap = block.getMaterialMap();
        List<String> materialList = new ArrayList<>();
        BlockCategoryFlags flag = (BlockCategoryFlags) getFlag();
        switch(flag)
        {
            case ORE_BLOCK -> {
                if(getBlock() instanceof IOreBlock oreBlock){
                    MutableComponent normalName = Component.translatable("material.immersivegeology.ore." + oreBlock.getOreRichness().name().toLowerCase());
                    if(!oreBlock.getOreRichness().equals(OreRichness.NORMAL)) normalName.append(Component.translatable("formatting.space"));
                    normalName.append(Component.translatable("material.immersivegeology." + materialMap.get(MaterialTexture.base).getName()));
                    normalName.append(Component.translatable("formatting.space"));
                    normalName.append(Component.translatable("material.immersivegeology." + materialMap.get(MaterialTexture.overlay).getName()));
                    return normalName;
                }
            }
            case ENGINEERING_BLOCK -> {
                materialList.add(I18n.get("material.immersivegeology.engineering." + materialMap.get(MaterialTexture.base).getName()));
            }
            default ->
            {
                for(MaterialTexture t : MaterialTexture.values()){
                    if (materialMap.containsKey(t)) {
                        materialList.add(I18n.get("material.immersivegeology." + materialMap.get(t).getName()));
                    }
                }
            }
        }

        return Component.translatable("block.immersivegeology." + block.getFlag().getName(), materialList.toArray());
    }

    public boolean cancelDatagen()
    {
        // For now, this is used to prevent some items from running in the data generation stage,
        // as this may cause issues if we don't have a flag or material.
        if(block.getMaterials().isEmpty()) IGLib.IG_LOGGER.warn("Block for Item has no Material (Are we using a pre defined model?) [{}]", getDescriptionId());
		return block.getMaterials().isEmpty();
	}

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType)
    {
        MaterialInterface<?> base = getMaterial(MaterialTexture.base);
        if(base.hasBurnTime()) return ((base.getBurnTime() * 2) * 10);
        return super.getBurnTime(itemStack, recipeType);
    }
}
