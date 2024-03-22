package com.igteam.immersivegeology.client.menu;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IGItemGroup extends CreativeModeTab {
    private static final ResourceLocation CMB_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/ig_tab_menu.png");
    private static final ResourceLocation CMT_TEXTURES = new ResourceLocation("immersivegeology", "textures/gui/ig_tabs.png");

    public static ItemSubGroup selectedGroup = ItemSubGroup.natural;
    public static final IGItemGroup IGGroup = new IGItemGroup("immersivegeology");

    public IGItemGroup(String label) {
        super(label);
        setBackgroundImage(CMB_TEXTURES);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("itemGroup.immersivegeology." + selectedGroup.name());
    }

    @Override
    public ItemStack makeIcon() {
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

    Logger log = ImmersiveGeology.getNewLogger();
    public static ItemSubGroup getCurrentSubGroup() {
        return selectedGroup;
    }

    @Override
    public void fillItemList(NonNullList<ItemStack> items) {
        HashMap<IFlagType<?>, ArrayList<Item>> itemMap = new HashMap<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
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

        for (IFlagType<?> pattern : allPatternList)
        {
            if(itemMap.containsKey(pattern)){
                ArrayList<Item> list = itemMap.get(pattern);
                for (Item item : list) {
                    item.fillItemCategory(this, items);
                }
            }
        }
    }
}
