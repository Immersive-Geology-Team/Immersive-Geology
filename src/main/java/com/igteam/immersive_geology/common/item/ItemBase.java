package com.igteam.immersive_geology.common.item;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ItemBase extends Item implements IGSubGroup, IEItemInterfaces.IColouredItem {

    protected ItemSubGroup subGroup;
    protected MaterialUseType useType;
    protected HashMap<BlockMaterialType, Material> itemMaterials = new HashMap<>();

    private String holding_name;
    private int burn_time = 0;

    public ItemBase(String registry_name, Material material, MaterialUseType useType) {
        super(material.getMaterialItemProperties());
        this.setRegistryName(registry_name.toLowerCase());
        this.useType = useType;
        this.holding_name = registry_name;
        itemMaterials.put(BlockMaterialType.BASE_MATERIAL, material);
        setSubGroup(useType.getSubgroup());
    }

    public void setBurnTime(int burn_time) {
        this.burn_time = burn_time;
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable IRecipeType<?> recipeType) {
        return burn_time;
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

    @Override
    public void addInformation(ItemStack item, World world, List<ITextComponent> list, ITooltipFlag flag) {
        StringBuilder elements = new StringBuilder();

        for(PeriodicTableElement.ElementProportion e : itemMaterials.get(BlockMaterialType.BASE_MATERIAL).getElements()) {
            elements.append(e.getElement().getSymbol());
            if(e.getQuantity() > 1) {
                elements.append(e.getQuantity());
            }
        }

        list.add(new StringTextComponent(elements.toString()));
        super.addInformation(item, world, list, flag);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        ArrayList<String> localizedNames = new ArrayList<>();
        localizedNames.add(I18n.format("material."+ IGLib.MODID + "." + itemMaterials.get(BlockMaterialType.BASE_MATERIAL).getName().toLowerCase()));
        TranslationTextComponent name = new TranslationTextComponent("item."+ IGLib.MODID+"."+useType.getName().toLowerCase(Locale.ENGLISH), localizedNames.toArray(new Object[localizedNames.size()]));
        return name;
    }
}
