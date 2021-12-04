package com.igteam.immersive_geology.client.menu.helper;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum ItemSubGroup {
    natural(Items.STONE),
    processed(Items.IRON_BLOCK),
    machines(Items.FURNACE),
    misc(Items.WATER_BUCKET);

    private Item icon;

    ItemSubGroup(Item stack) { this.icon = stack; }

    public Item getIcon(){
        return icon;
    }
}
