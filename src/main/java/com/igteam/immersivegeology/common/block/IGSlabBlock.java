package com.igteam.immersivegeology.common.block;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IGSlabBlock extends SlabBlock implements IGBlockType
{
    protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
    protected final BlockCategoryFlags category;

    public IGSlabBlock(BlockCategoryFlags flag, MaterialInterface<?> material) {
        super(BlockBehaviour.Properties.copy(Blocks.IRON_ORE));
        this.materialMap.put(MaterialTexture.base, material);
        this.category = flag;
    }

    public IFlagType<?> getFlag() {
        return category;
    }

    public ItemSubGroup getGroup() {
        return category.getSubGroup();
    }

    @Override
    public int getColor(int index) {
        return materialMap.get(MaterialTexture.values()[index]).getColor(category);
    }

    public Collection<MaterialInterface<?>> getMaterials() {
        return materialMap.values();
    }

    @Override
    public MaterialInterface<?> getMaterial(MaterialTexture t) {
        return materialMap.get(t);
    }
    @Override
    public Block getBlock() {
        return this;
    }

    @Override
    public Map<MaterialTexture, MaterialInterface<?>> getMaterialMap() {
        return materialMap;
    }
}