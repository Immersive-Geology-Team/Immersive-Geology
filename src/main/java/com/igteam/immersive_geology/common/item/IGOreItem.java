package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

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
}
