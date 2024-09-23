package com.igteam.immersivegeology.client.menu;

import com.ibm.icu.impl.Row;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.CreativeModeTab.Builder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class IGItemGroup extends CreativeModeTab {
    private static final ResourceLocation CMB_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/ig_tab_menu.png");
    private static final ResourceLocation CMT_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/ig_tabs.png");

    public static ItemSubGroup selectedGroup = ItemSubGroup.natural;

    public IGItemGroup(CreativeModeTab.Builder builder)
    {
        super(builder);
        ret.addAll(getSearchTabDisplayItems());
    }

    @Override
    public @NotNull ResourceLocation getBackgroundLocation() {
        return CMB_TEXTURES;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("itemGroup.immersivegeology." + selectedGroup.name());
    }

    @Override
    public int getLabelColor() {
        return 0xffd700;
    }

    @Override
    public @NotNull ResourceLocation getTabsImage() {
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
                        ret.add(new ItemStack(item));
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
