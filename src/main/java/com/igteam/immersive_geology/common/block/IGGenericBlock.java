package com.igteam.immersive_geology.common.block;

import com.igteam.immersive_geology.client.menu.ItemSubGroup;
import com.igteam.immersive_geology.common.block.helper.IGBlockType;
import com.igteam.immersive_geology.core.material.helper.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.IFlagType;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import com.igteam.immersive_geology.core.material.helper.MaterialTexture;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.*;

public class IGGenericBlock extends Block implements IGBlockType {
    protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
    protected final BlockCategoryFlags category;

    public IGGenericBlock(BlockCategoryFlags flag, MaterialInterface<?> material) {
        super(Properties.of(Material.STONE, MaterialColor.STONE));
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
        return materialMap.get(MaterialTexture.base).getColor(category);
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
}
