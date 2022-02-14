package com.igteam.immersive_geology.common.item.legacy;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.Item;

public class IGStaticItem extends Item implements IGSubGroup {

    private final ItemSubGroup subGroup;
    private String resourceName = null;

    public IGStaticItem(String registryName, ItemSubGroup subGroup) {
        super(new Item.Properties().group(ImmersiveGeology.IGGroup));
        this.setRegistryName(registryName);
        this.subGroup = subGroup;

        if(resourceName == null) resourceName = registryName;
        if(IGRegistrationHolder.registeredIGItems.putIfAbsent(registryName, this) != null){
            ImmersiveGeology.getNewLogger().error("Tried to add a duplicate key to item registry");
        }
    }

    /**
     * @description use this method if a custom resource is required for the item image/json, by default it's the same as the registry name
     * @param registryName
     * @param subGroup
     * @param resourceName
     */
    public IGStaticItem(String registryName, ItemSubGroup subGroup, String resourceName){
        this(registryName, subGroup);
        this.resourceName = resourceName;
    }

    public String getResourceName(){
        return resourceName;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return subGroup;
    }
}
