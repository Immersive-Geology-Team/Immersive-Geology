package com.igteam.immersive_geology.client.menu.helper;

import blusunrize.immersiveengineering.common.items.IEItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum ItemSubGroup {
    rocks(Items.STONE),
    metals(Items.IRON_BLOCK),
    machines(Items.FURNACE),
    misc(Items.WATER_BUCKET);

    private Item icon;

    ItemSubGroup(Item stack) { this.icon = stack; }

    public Item getIcon(){
        return icon;
    }
}
