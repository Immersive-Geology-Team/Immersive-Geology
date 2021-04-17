package com.igteam.immersive_geology.common.item;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class ItemBase extends Item implements IGSubGroup, IEItemInterfaces.IColouredItem {

    protected ItemSubGroup subGroup;
    protected MaterialUseType useType;
    protected HashMap<BlockMaterialType, Material> itemMaterials = new HashMap<>();

    private String holding_name;

    public ItemBase(String registry_name, Material material, MaterialUseType useType) {
        super(material.getMaterialItemProperties());
        this.setRegistryName(registry_name.toLowerCase());
        this.useType = useType;
        this.holding_name = registry_name;
        itemMaterials.put(BlockMaterialType.BASE_MATERIAL, material);
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
        return getMaterial(BlockMaterialType.BASE_MATERIAL).getColor(0);
    }

    public Material getMaterial(BlockMaterialType type) {
        return itemMaterials.getOrDefault(type, itemMaterials.get(BlockMaterialType.BASE_MATERIAL));
    }
}
