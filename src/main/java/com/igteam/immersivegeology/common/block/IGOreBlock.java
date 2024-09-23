package com.igteam.immersivegeology.common.block;

import com.igteam.immersivegeology.client.IGClientRenderHandler;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class IGOreBlock extends IGGenericBlock {

    protected final OreRichness richness;

    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial) {
        this(flag, baseMaterial, oreMaterial, OreRichness.POOR);
    }

    public IGOreBlock(BlockCategoryFlags flag, MaterialInterface<?> baseMaterial, MaterialInterface<?> oreMaterial, OreRichness richness) {
        super(flag, baseMaterial);
        this.materialMap.put(MaterialTexture.overlay, oreMaterial);
        this.richness = richness;
    }

    public OreRichness getOreRichness()
    {
        return richness;
    }

    public StoneFormation getStoneFormation()
    {
        if(materialMap.get(MaterialTexture.base).instance() instanceof MaterialStone stone){
            return stone.getStoneFormation();
        }
        return null;
    }

    public ItemLike getDroppedItem()
    {
        ItemStack stack = this.getMaterial(MaterialTexture.overlay).getStack(this.getOreRichness().toCategory());
        return stack.getItem();
    }

    public enum OreRichness
    {
        POOR,
        NORMAL,
        RICH;

        public ItemCategoryFlags toCategory()
        {
            return this == POOR ? ItemCategoryFlags.POOR_ORE : (this == NORMAL ? ItemCategoryFlags.NORMAL_ORE : ItemCategoryFlags.RICH_ORE);
        }
    }
}