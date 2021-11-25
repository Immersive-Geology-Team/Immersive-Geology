package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.Item;

public class IGStaticItem extends Item implements IGSubGroup {

    private final ItemSubGroup subGroup;

    public IGStaticItem(String registryName, ItemSubGroup subGroup) {
        super(new Item.Properties().group(ImmersiveGeology.IGGroup));
        this.setRegistryName(registryName);
        this.subGroup = subGroup;

        if(IGRegistrationHolder.registeredIGItems.putIfAbsent(registryName, this) != null){
            ImmersiveGeology.getNewLogger().error("Tried to add a duplicate key to item registry");
        }
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return subGroup;
    }
}
