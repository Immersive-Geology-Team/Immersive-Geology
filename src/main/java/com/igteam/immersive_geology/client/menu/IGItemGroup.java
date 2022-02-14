package com.igteam.immersive_geology.client.menu;

import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import igteam.immersive_geology.item.IGItemType;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;

public class IGItemGroup extends ItemGroup {
    public static ItemSubGroup selectedGroup = ItemSubGroup.natural;

    public IGItemGroup(String label){
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Items.CRAFTING_TABLE);
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

        for(Item item : Registry.ITEM) {
            if(item instanceof IGSubGroup) {
                IGSubGroup itm = (IGSubGroup )item;
                if(itm.getSubGroup() == selectedGroup) {
                    if(item instanceof IGItemType){
                        IGItemType base_item = (IGItemType) item;
                        MaterialPattern pattern = base_item.getPattern();
                        if(itemMap.containsKey(pattern)){
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
        }

        for(MaterialPattern key : ItemPattern.values()){
            ArrayList<Item> list = itemMap.get(key);
            if(list != null) {
                for (Item item : list) {
                    item.fillItemGroup(this, items);
                }
            }
        }

        for(MaterialPattern key : BlockPattern.values()){
            ArrayList<Item> list = itemMap.get(key);
            if(list != null) {
                for (Item item : list) {
                    item.fillItemGroup(this, items);
                }
            }
        }

        for(MaterialPattern key : MiscPattern.values()){
            ArrayList<Item> list = itemMap.get(key);
            if(list != null) {
                for (Item item : list) {
                    item.fillItemGroup(this, items);
                }
            }
        }
    }
}
