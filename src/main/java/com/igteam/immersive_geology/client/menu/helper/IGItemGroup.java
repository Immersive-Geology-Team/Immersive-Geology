package com.igteam.immersive_geology.client.menu.helper;

import igteam.immersive_geology.item.IGItemType;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import igteam.immersive_geology.menu.ItemSubGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IGItemGroup extends ItemGroup {
    private static final ResourceLocation CMB_TEXTURES = new ResourceLocation("immersive_geology", "textures/gui/ig_tab_menu.png");
    private static final ResourceLocation CMT_TEXTURES = new ResourceLocation("immersive_geology", "textures/gui/ig_tabs.png");

    public static ItemSubGroup selectedGroup = ItemSubGroup.natural;
    public static final IGItemGroup IGGroup = new IGItemGroup("immersive_geology");

    public IGItemGroup(String label) {
        super(label);
        setBackgroundImage(CMB_TEXTURES);
    }

    @Override
    public ItemStack createIcon() {
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

    public static ItemSubGroup getCurrentSubGroup() {
        return selectedGroup;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fill(NonNullList<ItemStack> items) {
        HashMap<MaterialPattern, ArrayList<Item>> itemMap = new HashMap<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if(item instanceof IGItemType){
                IGItemType type = (IGItemType) item;
                if(type.getSubGroup() == selectedGroup) {
                    MaterialPattern pattern = type.getPattern();
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
        ArrayList<MaterialPattern> allPatternList = new ArrayList<>();
        allPatternList.addAll(Arrays.asList(ItemPattern.values()));
        allPatternList.addAll(Arrays.asList(BlockPattern.values()));
        allPatternList.addAll(Arrays.asList(MiscPattern.values()));

        for (MaterialPattern pattern : allPatternList)
        {
            if(itemMap.containsKey(pattern)){
                ArrayList<Item> list = itemMap.get(pattern);
                for (Item item : list) {
                    item.fillItemGroup(this, items);
                }
            }
        }
    }
}
