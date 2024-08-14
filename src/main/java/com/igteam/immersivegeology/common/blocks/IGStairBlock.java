package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.blocks.helper.IGBlockType;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class IGStairBlock extends StairBlock implements IGBlockType {
    private final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
    private final BlockCategoryFlags category = BlockCategoryFlags.STAIRS;

    public IGStairBlock(Supplier<BlockState> supplier, MaterialInterface<?> material) {
        super(supplier, BlockBehaviour.Properties.ofFullCopy(supplier.get().getBlock()));
        this.materialMap.put(MaterialTexture.base, material);
    }

    @Override
    public Block getBlock() {
        return this;
    }

    @Override
    public Collection<MaterialInterface<?>> getMaterials() {
        return materialMap.values();
    }

    @Override
    public MaterialInterface<?> getMaterial(MaterialTexture t){
        return materialMap.get(t);
    }

    @Override
    public IFlagType<?> getFlag() {
        return category;
    }

    @Override
    public ItemSubGroup getGroup() {
        return category.getSubGroup();
    }

    @Override
    public Map<MaterialTexture, MaterialInterface<?>> getMaterialMap() {
        return materialMap;
    }

    @Override
    public int getColor(int index) {
        return materialMap.get(MaterialTexture.values()[index]).getColor(category);
    }
}
