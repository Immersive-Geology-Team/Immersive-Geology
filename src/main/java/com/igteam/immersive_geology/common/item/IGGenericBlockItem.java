package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.client.menu.IGItemGroup;
import com.igteam.immersive_geology.client.menu.ItemSubGroup;
import com.igteam.immersive_geology.common.block.IGGenericBlock;
import com.igteam.immersive_geology.common.item.helper.IGFlagItem;
import com.igteam.immersive_geology.core.material.helper.IFlagType;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class IGGenericBlockItem extends BlockItem implements IGFlagItem {

    private final IGGenericBlock block;

    public IGGenericBlockItem(IGGenericBlock block) {
        super(block, new Properties().tab(IGItemGroup.IGGroup));
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
    public List<MaterialInterface<?>> getMaterials() {
        return block.getMaterials();
    }
}
