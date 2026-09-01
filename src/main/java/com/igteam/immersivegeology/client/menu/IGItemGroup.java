/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu;

import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class IGItemGroup extends CreativeModeTab {
    private static final ResourceLocation GEOLOGIC_BACKGROUND_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/backgrounds/geologic.png");
    private static final ResourceLocation PRODUCT_BACKGROUND_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/backgrounds/product.png");
    private static final ResourceLocation PYROMETALLURGY_BACKGROUND_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/backgrounds/pyrometallurgy.png");
    private static final ResourceLocation HYDROMETALLURGY_BACKGROUND_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/backgrounds/hydrometallurgy.png");
    private static final ResourceLocation STRUCTURAL_BACKGROUND_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/backgrounds/structural.png");

    private static final ResourceLocation GEOLOGIC_TAB_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/tabs/geologic.png");
    private static final ResourceLocation COMPONENT_TAB_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/tabs/component.png");
    private static final ResourceLocation PYRO_TAB_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/tabs/pyro.png");
    private static final ResourceLocation HYDRO_TAB_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/tabs/hydro.png");
    private static final ResourceLocation STRUCTURAL_TAB_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/tabs/structural.png");

    public static ItemSubGroup selectedGroup = ItemSubGroup.geologic;

    private static final Map<ItemSubGroup, List<Supplier<Item>>> LOOSE_ITEMS = new EnumMap<>(ItemSubGroup.class);

    public static void addLooseItem(ItemSubGroup group, Supplier<Item> item)
    {
        LOOSE_ITEMS.computeIfAbsent(group, ignored -> new ArrayList<>()).add(item);
    }

    private static void addLooseItems(Collection<ItemStack> target, @Nullable ItemSubGroup group)
    {
        LOOSE_ITEMS.forEach((itemGroup, items) -> {
            if(group!=null&&itemGroup!=group) return;
            for(Supplier<Item> item : items) target.add(new ItemStack(item.get()));
        });
    }

    public IGItemGroup(CreativeModeTab.Builder builder)
    {
        super(builder.withSearchBar(62));
        ret.addAll(getSearchTabDisplayItems());
    }

    @Override
    public @NotNull ResourceLocation getBackgroundLocation() {
        switch(selectedGroup)
        {
            case geologic: return GEOLOGIC_BACKGROUND_TEXTURES;
            case components: return PRODUCT_BACKGROUND_TEXTURES;
            case pyrometallurgy: return PYROMETALLURGY_BACKGROUND_TEXTURES;
            case hydrometallurgy:return HYDROMETALLURGY_BACKGROUND_TEXTURES;
            case structural: return STRUCTURAL_BACKGROUND_TEXTURES;
        }
        return GEOLOGIC_BACKGROUND_TEXTURES;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("itemGroup.immersivegeology");
    }

    @Override
    public int getLabelColor() {
        return super.getLabelColor();//0x2e6c26;
    }

    @Override
    public @NotNull ResourceLocation getTabsImage() {
        if(selectedGroup == ItemSubGroup.geologic) return GEOLOGIC_TAB_TEXTURES;
        if(selectedGroup == ItemSubGroup.components) return COMPONENT_TAB_TEXTURES;
        if(selectedGroup == ItemSubGroup.pyrometallurgy) return PYRO_TAB_TEXTURES;
        if(selectedGroup == ItemSubGroup.hydrometallurgy) return HYDRO_TAB_TEXTURES;
        if(selectedGroup == ItemSubGroup.structural) return STRUCTURAL_TAB_TEXTURES;
        return GEOLOGIC_TAB_TEXTURES;
    }

    public static void updateSubGroup(ItemSubGroup group) {
        selectedGroup = group;
        updateRet = true;
        ret.clear();
    }

    static Collection<ItemStack> ret = ItemStackLinkedSet.createTypeAndTagSet();
    static Collection<ItemStack> dis = ItemStackLinkedSet.createTypeAndTagSet();
    private static boolean updateRet = true;
    @Override
    public @NotNull Collection<ItemStack> getDisplayItems()
    {
        if(updateRet)
        {
            HashMap<IFlagType<?>, ArrayList<Item>> itemMap = new HashMap<>();
            for(Item item : IGRegistrationHolder.getIGItems())
            {
                if(item instanceof IGFlagItem type)
                {
                    IFlagType<?> pattern = type.getFlag();
                    if(type.getSubGroup()==selectedGroup)
                    {
                        if(itemMap.containsKey(pattern))
                        {
                            ArrayList<Item> list = itemMap.get(pattern);
                            list.add(item);
                            itemMap.replace(pattern, list);
                        }
                        else
                        {
                            ArrayList<Item> list = new ArrayList<>();
                            list.add(item);
                            itemMap.put(pattern, list);
                        }
                    }
                }
            }

            ArrayList<IFlagType<?>> allPatternList = new ArrayList<>(Arrays.asList(ItemCategoryFlags.values()));
            allPatternList.addAll(Arrays.asList(BlockCategoryFlags.values()));
            for(IFlagType<?> pattern : allPatternList)
            {
                if(itemMap.containsKey(pattern))
                {
                    ArrayList<Item> list = itemMap.get(pattern);
                    for(Item item : list)
                    {
                        ItemStack stack = new ItemStack(item);
                        ret.add(stack);
                    }
                }
            }
            addLooseItems(ret, selectedGroup);
        }
        return ret;
    }

    @Override
    public @NotNull Collection<ItemStack> getSearchTabDisplayItems()
    {
        if(dis.isEmpty())
        {
            HashMap<IFlagType<?>, ArrayList<Item>> itemMap = new HashMap<>();
            for(Item item : IGRegistrationHolder.getIGItems())
            {
                if(item instanceof IGFlagItem type)
                {
                    IFlagType<?> pattern = type.getFlag();
                    if(itemMap.containsKey(pattern))
                    {
                        ArrayList<Item> list = itemMap.get(pattern);
                        list.add(item);
                        itemMap.replace(pattern, list);
                    }
                    else
                    {
                        ArrayList<Item> list = new ArrayList<>();
                        list.add(item);
                        itemMap.put(pattern, list);
                    }
                }
            }

            ArrayList<IFlagType<?>> allPatternList = new ArrayList<>(Arrays.asList(ItemCategoryFlags.values()));
            allPatternList.addAll(Arrays.asList(BlockCategoryFlags.values()));

            for(IFlagType<?> pattern : allPatternList)
            {
                if(itemMap.containsKey(pattern))
                {
                    ArrayList<Item> list = itemMap.get(pattern);
                    for(Item item : list)
                    {
                        dis.add(new ItemStack(item));
                    }
                }
            }
            addLooseItems(dis, null);
        }
        return dis;
    }

    public static ItemSubGroup getCurrentSubGroup() {
        return selectedGroup;
    }
}
