package com.igteam.immersive_geology.common.block;

import com.igteam.immersive_geology.client.IGClientRenderHandler;
import com.igteam.immersive_geology.client.menu.ItemSubGroup;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.core.material.helper.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.IFlagType;
import com.igteam.immersive_geology.core.material.helper.ItemCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.*;

public class IGGenericBlock extends Block {

    private final List<MaterialInterface<?>> materials;
    private final BlockCategoryFlags category;

    public IGGenericBlock(BlockCategoryFlags flag, MaterialInterface<?>... materials) {
        super(Properties.of(Material.STONE, MaterialColor.STONE));
        this.materials = List.of(materials);
        this.category = flag;
    }

    public IFlagType<?> getFlag() {
        return category;
    }

    public ItemSubGroup getGroup() {
        return category.getSubGroup();
    }

    public List<MaterialInterface<?>> getMaterials() {
        return materials;
    }
}
