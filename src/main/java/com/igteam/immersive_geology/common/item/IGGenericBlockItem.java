package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.client.menu.IGItemGroup;
import com.igteam.immersive_geology.client.menu.ItemSubGroup;
import com.igteam.immersive_geology.common.block.helper.IGBlockType;
import com.igteam.immersive_geology.common.item.helper.IGFlagItem;
import com.igteam.immersive_geology.common.item.helper.IGItemType;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.material.MaterialInterface;
import com.igteam.immersive_geology.core.material.helper.material.MaterialTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class IGGenericBlockItem extends BlockItem implements IGItemType, IGFlagItem {

    private final IGBlockType block;

    public IGGenericBlockItem(IGBlockType block) {
        super(block.getBlock(), new Properties().tab(IGItemGroup.IGGroup));
        this.block = block;
    }

    @Override
    public IFlagType<?> getFlag() {
        return block.getFlag();
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return block.getGroup();
    }

    @Override
    public Collection<MaterialInterface<?>> getMaterials() {
        return block.getMaterials();
    }

    @Override
    public MaterialInterface<?> getMaterial(MaterialTexture t) {
        return block.getMaterial(t);
    }

    @Override
    public int getColor(int index) {
        return this.block.getColor(index);
    }

    @Override
    public Component getName(ItemStack pStack) {
        Map<MaterialTexture, MaterialInterface<?>> materialMap = block.getMaterialMap();
        List<String> materialList = new ArrayList<>();
        for(MaterialTexture t : MaterialTexture.values()){
            if (materialMap.containsKey(t)) {
                materialList.add(I18n.get("block.immersive_geology." + materialMap.get(t).getName()));
            }
        }

        return new TranslatableComponent("block.immersive_geology." + block.getFlag().getName(), materialList.toArray());
    }
}
