/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IGGenericItem extends Item implements IGFlagItem {
    protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
    protected final ItemCategoryFlags category;
    protected String customLang = "";
    boolean hasCustomLang = false;
    public IGGenericItem(ItemCategoryFlags flag, MaterialInterface<?> material) {
        this(flag, material, new Properties());
    }

    public IGGenericItem(ItemCategoryFlags flag, MaterialInterface<?> material, Properties properties) {
        super(properties);
        this.materialMap.put(MaterialTexture.base, material);
        this.category = flag;
    }

    public int getColor(int index) {
        if(getFlag().hasPalette() || getFlag().equals(ItemCategoryFlags.PELLET) || getFlag().equals(ItemCategoryFlags.OXIDE_PELLET) || getFlag().equals(ItemCategoryFlags.HAMMER) || getMaxStackSize(getDefaultInstance()) == 1) return 0xffffff;
        if (index >= materialMap.values().size()) index = index % materialMap.values().size();

        //let's use last available colour. map could not be empty
        return materialMap.get(MaterialTexture.values()[index]).getColor(category, 0);
    }

    public boolean isIGRepairable(ItemStack stack)
    {
        return false;
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        if(hasCustomLang)
        {
            return Component.translatable("item.immersivegeology." + customLang).withStyle(getMaterial(MaterialTexture.base).getRarity().color);
        }

        List<String> materialList = new ArrayList<>();
        for(MaterialTexture t : MaterialTexture.values()){
            if (materialMap.containsKey(t)) {
                materialList.add(I18n.get("material.immersivegeology." + materialMap.get(t).getName()));
            }
        }

        return Component.translatable("item.immersivegeology." + category.getName(), materialList.toArray()).withStyle(getMaterial(MaterialTexture.base).getRarity().color);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if(getFlag().equals(ItemCategoryFlags.INGOT) && (getMaterial(MaterialTexture.base) instanceof MetalEnum metal))
        {
            pTooltipComponents.add(Component.translatable("immersivegeology.item.text.sources"));
            Set<MaterialHelper> sources = metal.getOriginMaterials();
            for(MaterialHelper source : sources)
            {
                pTooltipComponents.add(Component.translatable("material.immersivegeology." + source.getName()).withStyle(ChatFormatting.GOLD));
            }
        }
    }

    @Override
    public ItemCategoryFlags getFlag() {
        return category;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return category.getSubGroup();
    }

    @Override
    public Collection<MaterialInterface<?>> getMaterials() {
        return materialMap.values();
    }

    @Override
    public MaterialInterface<?> getMaterial(MaterialTexture t) {
        return materialMap.get(t);
    }

	public Item setCustomLangString(String rawRefractoryBrick)
	{
        hasCustomLang = true;
        customLang = rawRefractoryBrick;
        return this;
	}
}
