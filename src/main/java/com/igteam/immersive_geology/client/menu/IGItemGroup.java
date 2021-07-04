package com.igteam.immersive_geology.client.menu;

import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.item.IGBlockItem;
import com.igteam.immersive_geology.common.item.IGBucketItem;
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
                    }

                    if(item instanceof IGBlockItem) {
                        IGBlockItem block_item = (IGBlockItem) item;
                        if(block_item.getBlock() instanceof IGBlockType){
                            IGBlockType block = ((IGBlockType) block_item.getBlock());
                            MaterialUseType use_type = block.getBlockUseType();
                            if(!itemMap.containsKey(use_type)){
                                ArrayList<Item> list = new ArrayList<>();
                                list.add(item);
                                itemMap.put(use_type, list);
                            } else {
                                ArrayList<Item> list = itemMap.get(use_type);
                                list.add(item);
                                itemMap.replace(use_type, list);
                            }
                        }
                    }

                    if(item instanceof IGBucketItem){
                        IGBucketItem bucketItem = (IGBucketItem) item;
                        MaterialUseType use_type = MaterialUseType.BUCKET;
                        if(itemMap.containsKey(use_type)){
                            ArrayList<Item> list = itemMap.get(use_type);
                            list.add(bucketItem);
                            itemMap.replace(use_type, list);
                        } else {
                            ArrayList<Item> list = new ArrayList<>();
                            list.add(bucketItem);
                            itemMap.put(use_type, list);
                        }
                    }

                }
            }
        }

        for(MaterialUseType key : MaterialUseType.values()){
            ArrayList<Item> list = itemMap.get(key);
            if(list != null) {
                for (Item item : list) {
                    item.fillItemGroup(this, items);
                }
            }
        }
    }
}
