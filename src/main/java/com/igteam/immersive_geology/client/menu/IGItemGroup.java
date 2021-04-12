package com.igteam.immersive_geology.client.menu;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.common.item.IGBlockItem;
import com.igteam.immersive_geology.common.item.ItemBase;
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

    public static ItemSubGroup selectedGroup = ItemSubGroup.natrual;

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
        HashMap<MaterialUseType, ArrayList<Item>> itemMap = new HashMap<>();

        for(Item item : Registry.ITEM) {
            if(item instanceof IGSubGroup) {
                IGSubGroup itm = (IGSubGroup )item;
                if(itm.getSubGroup() == selectedGroup) {
                    if(item instanceof ItemBase){
                        ItemBase base_item = (ItemBase) item;
                        MaterialUseType use_type = base_item.getUseType();
                        if(itemMap.containsKey(use_type)){
                            ArrayList<Item> list = itemMap.get(use_type);
                            list.add(item);
                            itemMap.replace(use_type, list);
                        } else {
                            ArrayList<Item> list = new ArrayList<>();
                            list.add(item);
                            itemMap.put(use_type, list);
                        }
                    } else if(item instanceof IGBlockItem) {
                        IGBlockItem block_item = (IGBlockItem) item;
                        MaterialUseType use_type = block_item.getUseType();
                        if(itemMap.containsKey(use_type)){
                            ArrayList<Item> list = itemMap.get(use_type);
                            list.add(item);
                            itemMap.replace(use_type, list);
                        } else {
                            ArrayList<Item> list = new ArrayList<>();
                            list.add(item);
                            itemMap.put(use_type, list);
                        }
                    }
                }
            }
        }

        for(ArrayList<Item> list : itemMap.values()){
            for(Item item : list){
                item.fillItemGroup(this, items);
            }
        }
    }

    public void empty(){

    }

    public NonNullList<ItemStack> getCurrentList() {
        NonNullList<ItemStack> list = NonNullList.create();
        for(Item item : Registry.ITEM) {
            if(item instanceof IGSubGroup) {
                IGSubGroup itm = (IGSubGroup)item;
                if(itm.getSubGroup() == selectedGroup) {
                    item.fillItemGroup(ImmersiveGeology.IGGroup, list);
                }
            }
        }
        return list;
    }
}
