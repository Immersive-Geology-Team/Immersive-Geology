package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.client.menu.IGItemGroup;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.common.item.helper.IGItemType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class IGGenericItem extends Item implements IGItemType, IGFlagItem {
    private final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
    private final ItemCategoryFlags category;

    public IGGenericItem(ItemCategoryFlags flag, MaterialInterface<?> material) {
        super(new Properties().tab(IGItemGroup.IGGroup));
        this.materialMap.put(MaterialTexture.base, material);
        this.category = flag;
    }

    public int getColor(int index) {
        if (index >= materialMap.values().size()) index = materialMap.values().size() - 1;
        //let's use last available colour. map could not be empty
        return materialMap.get(MaterialTexture.values()[index]).getColor(category);
    }

    @Override
    public Component getName(ItemStack pStack) {
        List<String> materialList = new ArrayList<>();
        for(MaterialTexture t : MaterialTexture.values()){
            if (materialMap.containsKey(t)) {
                materialList.add(I18n.get("material.immersive_geology." + materialMap.get(t).getName()));
            }
        }

        return new TranslatableComponent("item.immersive_geology." + category.getName(), materialList.toArray());
    }

    @Override
    public ItemCategoryFlags getFlag() {
        return category;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return category.getSubGroup();
    }

    @Override
    public Collection<MaterialInterface<?>> getMaterials() {
        return materialMap.values();
    }

    @Override
    public MaterialInterface<?> getMaterial(MaterialTexture t) {
        return materialMap.get(t);
    }
}
