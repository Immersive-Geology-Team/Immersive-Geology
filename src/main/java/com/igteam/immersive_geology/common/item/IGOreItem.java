package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IGOreItem extends ItemBase {
    public IGOreItem(String holder_key, Material[] material, MaterialUseType useType) {
        super(holder_key, material[0], useType);
        itemMaterials.put(BlockMaterialType.ORE_MATERIAL, material[1]);
    }

    @Override
    public int getColourForIEItem(ItemStack stack, int pass)
    {
        Material[] materials = {itemMaterials.get(BlockMaterialType.BASE_MATERIAL), itemMaterials.get(BlockMaterialType.ORE_MATERIAL)};
        return materials[MathHelper.clamp(pass,0,materials.length-1)].getColor(0);
    }

    @Override
    public void addInformation(ItemStack item, World world, List<ITextComponent> list, ITooltipFlag flag) {
        StringBuilder elements = new StringBuilder();

        for(PeriodicTableElement.ElementProportion e : itemMaterials.get(BlockMaterialType.ORE_MATERIAL).getElements()) {
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
        localizedNames.add(I18n.format("material."+IGLib.MODID + "." + itemMaterials.get(BlockMaterialType.ORE_MATERIAL).getName().toLowerCase()));
        localizedNames.add(I18n.format("material."+IGLib.MODID + "." + itemMaterials.get(BlockMaterialType.BASE_MATERIAL).getName().toLowerCase()));
        TranslationTextComponent name = new TranslationTextComponent("item."+ IGLib.MODID+"."+useType.getName().toLowerCase(Locale.ENGLISH), localizedNames.toArray(new Object[localizedNames.size()]));
        return name;
    }
}
