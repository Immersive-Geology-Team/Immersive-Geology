package com.igteam.immersive_geology.common.item;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.common.block.blocks.IGOreBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class IGBlockItem extends BlockItem implements IGSubGroup, IEItemInterfaces.IColouredItem {

    private ItemSubGroup subGroup;
    private String holder_name;
    private Material itemMaterial;
    private MaterialUseType useType;

    public IGBlockItem(BlockBase blockIn, ItemSubGroup subGroup, Material material){
        super(blockIn, new Item.Properties().group(ImmersiveGeology.IGGroup).rarity(material.getRarity()));
        this.subGroup = subGroup;
        this.holder_name = blockIn.getHolderName();
        this.itemMaterial = material;
        this.useType = blockIn.getBlockUseType();
    }

    @Override
    public ItemSubGroup getSubGroup(){
        return this.subGroup;
    }

    public String getHolderName(){
        return holder_name;
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
            materials[0] = oreBlock.getOreBase();
            materials[1] = itemMaterial;

            return materials[MathHelper.clamp(pass,0,materials.length-1)].getColor(0);
        } else {

            return itemMaterial.getColor(0);
        }
    }

    public MaterialUseType getUseType(){
        return useType;
    }
}
