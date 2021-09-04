package com.igteam.immersive_geology.common.item;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Locale;

public class IGBlockItem extends BlockItem implements IGSubGroup, IEItemInterfaces.IColouredItem {

    private final ItemSubGroup subGroup;
    private final String holder_name;
    private final Material itemMaterial;
    private final MaterialUseType useType;

    public IGBlockItem(Block blockIn, IGBlockType blockType, ItemSubGroup subGroup, Material material){
        super(blockIn, new Item.Properties().group(ImmersiveGeology.IGGroup).rarity(material.getRarity()));
        this.subGroup = subGroup;
        this.holder_name = blockType.getHolderName();
        this.itemMaterial = material;
        this.useType = blockType.getBlockUseType();
    }

    public String getHolderName() {
        return holder_name;
    }

    @Override
    public ItemSubGroup getSubGroup(){
        return this.subGroup;
    }

    @Override
    public boolean hasCustomItemColours()
    {
        return true;
    }

    @Override
    public int getColourForIEItem(ItemStack stack, int pass)
    {
        Material[] materials = new Material[2];
        if((getBlock() instanceof IGOreBlock)) {
            IGOreBlock oreBlock = (IGOreBlock) getBlock();
            materials[0] = oreBlock.getMaterial(BlockMaterialType.ORE_MATERIAL);
            materials[1] = itemMaterial;

            return materials[MathHelper.clamp(pass,0,materials.length-1)].getColor(1);
        } else {

            return itemMaterial.getColor(0);
        }
    }
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        ArrayList<String> localizedNames = new ArrayList<>();

        if((getBlock() instanceof IGOreBlock)) {
            IGOreBlock oreBlock = (IGOreBlock) getBlock();
            localizedNames.add(I18n.format("material." + IGLib.MODID + "." + oreBlock.getMaterial(BlockMaterialType.ORE_MATERIAL).getName()));
        }
        localizedNames.add(I18n.format("material."+ IGLib.MODID +"."+itemMaterial.getName()));
        String base_name = "block."+IGLib.MODID+"."+useType.getName().toLowerCase(Locale.ENGLISH);
        return new TranslationTextComponent(base_name, localizedNames.toArray(new String[localizedNames.size()]));
    }

    public MaterialUseType getUseType(){
        return useType;
    }
}
