/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.menu;

import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import com.igteam.immersivegeology.common.item.blueprint.IGBlueprintSettings;
import com.igteam.immersivegeology.common.item.blueprint.IGBlueprintSettings.Mode;
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
import java.util.*;

public class IGItemGroup extends CreativeModeTab {
    private static final ResourceLocation CMB_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/ig_tab_menu.png");
    private static final ResourceLocation CMT_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/ig_tabs.png");

    private static final ResourceLocation GEOLOGIC_BACKGROUND_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/geologic_background.png");
    private static final ResourceLocation PRODUCT_BACKGROUND_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/product_background.png");
    private static final ResourceLocation PYROMETALLURGY_BACKGROUND_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/pyrometallurgy_background.png");
    private static final ResourceLocation HYDROMETALLURGY_BACKGROUND_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/hydrometallurgy_background.png");
    private static final ResourceLocation STRUCTURAL_BACKGROUND_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/structural_background.png");

    private static final ResourceLocation GEOLOGIC_TAB_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/creative_tabs/geologic_tab.png");


    public static ItemSubGroup selectedGroup = ItemSubGroup.geologic;

    public IGItemGroup(CreativeModeTab.Builder builder)
    {
        super(builder.withSearchBar(89));
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
        return CMB_TEXTURES;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("itemGroup.immersivegeology." + selectedGroup.name());
    }

    @Override
    public int getLabelColor() {
        return 0x2e6c26;
    }

    @Override
    public @NotNull ResourceLocation getTabsImage() {
        if(selectedGroup == ItemSubGroup.geologic) return GEOLOGIC_TAB_TEXTURES;
        return CMT_TEXTURES;
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
                        // A special case for Blueprints, to ensure they have the correct NBT data.
                        if(pattern.equals(ItemCategoryFlags.BLUEPRINT))
                        {
                            for(TemplateMultiblock mb : IGRegistrationHolder.MB_TEMPLATE_MAP.values())
                            {
                                ItemStack blueprint = new ItemStack(item);
                                IGBlueprintSettings settings = new IGBlueprintSettings(blueprint);
                                settings.setMultiblock(mb);
                                settings.setMirror(false);
                                settings.setRotation(Rotation.NONE);
                                settings.setPlaced(false);
                                settings.applyTo(blueprint);
                                ret.add(blueprint);
                            }
                            continue;
                        }

                        ItemStack stack = new ItemStack(item);
                        ret.add(stack);
                    }
                }
            }
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
        }
        return dis;
    }

    public static ItemSubGroup getCurrentSubGroup() {
        return selectedGroup;
    }
}
