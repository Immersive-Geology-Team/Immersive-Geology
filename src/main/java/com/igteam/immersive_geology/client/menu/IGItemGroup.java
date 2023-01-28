package com.igteam.immersive_geology.client.menu;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.configuration.CommonConfiguration;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.common.item.helper.IGFlagItem;
import com.igteam.immersive_geology.common.item.helper.IGItemType;
import com.igteam.immersive_geology.core.material.helper.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.ItemCategoryFlags;
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
import java.util.List;

public class IGItemGroup extends CreativeModeTab {
    private static final ResourceLocation CMB_TEXTURES = new ResourceLocation("immersive_geology", "textures/gui/ig_tab_menu.png");
    private static final ResourceLocation CMT_TEXTURES = new ResourceLocation("immersive_geology", "textures/gui/ig_tabs.png");

    public static ItemSubGroup selectedGroup = ItemSubGroup.natural;
    public static final IGItemGroup IGGroup = new IGItemGroup("immersive_geology");

    public IGItemGroup(String label) {
        super(label);
        setBackgroundImage(CMB_TEXTURES);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("itemGroup.immersive_geology." + selectedGroup.name());
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
        HashMap<ItemCategoryFlags, ArrayList<Item>> itemMap = new HashMap<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if(item instanceof IGFlagItem type){
                ItemCategoryFlags pattern = type.getFlag();
                List<ItemCategoryFlags> itemFlags = CommonConfiguration.ITEM_FLAGS.get(type.getMaterials().get(0).getName()).get();
                List<BlockCategoryFlags> blockFlags = CommonConfiguration.BLOCK_FLAGS.get(type.getMaterials().get(0).getName()).get();

                boolean isConfigured = itemFlags.toString().contains(pattern.toString()) || blockFlags.toString().contains(pattern.toString());
                if(type.getSubGroup() == selectedGroup && isConfigured) {
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

        ArrayList<ItemCategoryFlags> allPatternList = new ArrayList<>();
        allPatternList.addAll(Arrays.asList(ItemCategoryFlags.values()));

        for (ItemCategoryFlags pattern : allPatternList)
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
