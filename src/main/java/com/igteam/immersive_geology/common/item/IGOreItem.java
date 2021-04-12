package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.block.blocks.IGOreBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class IGOreItem extends ItemBase {
    private Material[] materials;

    public IGOreItem(String holder_key, Material[] material, MaterialUseType useType) {
        super(holder_key, material[0], useType);
        this.materials = material;
    }

    @Override
    public int getColourForIEItem(ItemStack stack, int pass)
    {
        return materials[MathHelper.clamp(pass,0,materials.length-1)].getColor(0);
    }
}
