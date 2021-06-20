package com.igteam.immersive_geology.common.item;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Supplier;

public class IGBucketItem extends BucketItem implements IGSubGroup, IEItemInterfaces.IColouredItem{

    protected ItemSubGroup subGroup;
    private final Material fluidMaterial;

    public IGBucketItem(Supplier<? extends Fluid> p_i244800_1_, Material material, Properties p_i244800_2_) {
        super(p_i244800_1_, p_i244800_2_.group(ImmersiveGeology.IGGroup));
        this.fluidMaterial = material;
        this.subGroup = ItemSubGroup.misc;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        ArrayList<String> localizedNames = new ArrayList<>();
        localizedNames.add(I18n.format("material."+IGLib.MODID + "." + fluidMaterial.getName().toLowerCase()));
        TranslationTextComponent name = new TranslationTextComponent("item."+ IGLib.MODID+"."+ MaterialUseType.BUCKET.getName().toLowerCase(Locale.ENGLISH), localizedNames.toArray(new Object[localizedNames.size()]));
        return name;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return subGroup;
    }

    @Override
    public boolean hasCustomItemColours()
    {
        return true;
    }

    @Override
    public int getColourForIEItem(ItemStack stack, int pass)
    {
        return fluidMaterial.getColor(0);
    }
}
