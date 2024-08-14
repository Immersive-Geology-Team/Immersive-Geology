package com.igteam.immersivegeology.client.menu;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import org.slf4j.Logger;

import java.util.*;

public class IGItemGroup extends CreativeModeTab {
    private static final ResourceLocation CMB_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/ig_tab_menu.png");
    private static final ResourceLocation CMT_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/ig_tabs.png");

    public static ItemSubGroup selectedGroup = ItemSubGroup.natural;

    protected IGItemGroup(Builder builder) {
        super(builder);
    }

    @Override
    public ResourceLocation getBackgroundLocation() {
        return CMB_TEXTURES;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("itemGroup.immersivegeology." + selectedGroup.name());
    }

    @Override
    public ItemStack getIconItem() {
        return new ItemStack(Items.GOLD_INGOT);
    }

    @Override
    public int getLabelColor() {
        return 0xffd700;
    }

    @Override
    public ResourceLocation getTabsImage() {
        return CMT_TEXTURES;
    }

    public static void updateSubGroup(ItemSubGroup group) {
        selectedGroup = group;
    }

    Logger log = IGLib.getNewLogger();
    public static ItemSubGroup getCurrentSubGroup() {
        return selectedGroup;
    }

    @Override
    public Collection<ItemStack> getDisplayItems() {

        HashMap<IFlagType<?>, ArrayList<Item>> itemMap = new HashMap<>();
        for (Item item : IGRegistrationHolder.getIGItems()) {
            if(item instanceof IGFlagItem type) {
                IFlagType<?> pattern = type.getFlag();
                if(type.getSubGroup() == selectedGroup) {
                    if (itemMap.containsKey(pattern)) {
                        ArrayList<Item> list = itemMap.get(pattern);
                        list.add(item);
                        itemMap.replace(pattern, list);
                    } else {
                        ArrayList<Item> list = new ArrayList<>();
                        list.add(item);
                        itemMap.put(pattern, list);
                    }
                }
            }
        }

        ArrayList<IFlagType<?>> allPatternList = new ArrayList<>(Arrays.asList(ItemCategoryFlags.values()));
        allPatternList.addAll(Arrays.asList(BlockCategoryFlags.values()));

        Set<ItemStack> set = ItemStackLinkedSet.createTypeAndTagSet();

        for (IFlagType<?> pattern : allPatternList)
        {
            if(itemMap.containsKey(pattern)){
                ArrayList<Item> list = itemMap.get(pattern);
                for (Item item : list) {
                    set.add(new ItemStack(item));
                }
            }
        }

        return set;
    }
}
