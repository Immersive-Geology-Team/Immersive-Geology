package com.igteam.immersive_geology.client.menu;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
        for(Item item : Registry.ITEM) {
            if(item instanceof IGSubGroup) {
                IGSubGroup itm = (IGSubGroup )item;
                if(itm.getSubGroup() == selectedGroup) {
                    item.fillItemGroup(this, items);
                }
            }
        }
    }

    public static NonNullList<ItemStack> getCurrentList() {
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
