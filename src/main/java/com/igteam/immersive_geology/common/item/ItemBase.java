package com.igteam.immersive_geology.common.item;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemBase extends Item implements IGSubGroup, IEItemInterfaces.IColouredItem {

    private ItemSubGroup subGroup;
    private MaterialUseType useType;
    private Material itemMaterial;

    private String holding_name;

    public ItemBase(String registry_name, Material material, MaterialUseType useType) {
        super(material.getMaterialItemProperties());
        this.setRegistryName(registry_name.toLowerCase());
        this.useType = useType;
        this.holding_name = registry_name;
        this.itemMaterial = material;
        setSubGroup(useType.getSubgroup());
    }

    public void setSubGroup(ItemSubGroup subGroup){
        this.subGroup = subGroup;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return subGroup;
    }

    public MaterialUseType getUseType() {
        return useType;
    }

    public String getHoldingName() {
        return holding_name;
    }

    @Override
    public boolean hasCustomItemColours()
    {
        return true;
    }

    @Override
    public int getColourForIEItem(ItemStack stack, int pass)
    {
        return itemMaterial.getColor(0);
    }
}
