package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.client.menu.IGItemGroup;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class IGGenericBucketItem extends BucketItem implements IGFlagItem {
    private final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
    private final ItemCategoryFlags category;

    public IGGenericBucketItem(Supplier<? extends Fluid> fluid, ItemCategoryFlags flag, MaterialInterface<?> material) {
        super(fluid, new Properties());
        this.materialMap.put(MaterialTexture.base, material);
        this.category = flag;
    }

    public int getColor(int index) {
        if(index == 0) return 0xffffffff;
        if (index >= materialMap.values().size()) index = materialMap.values().size() - 1;
        //let's use last available colour. map could not be empty
        return materialMap.get(MaterialTexture.values()[index]).getColor(category);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        List<String> materialList = new ArrayList<>();
        for(MaterialTexture t : MaterialTexture.values()){
            if (materialMap.containsKey(t)) {
                materialList.add(I18n.get("material.immersivegeology." + materialMap.get(t).getName()));
            }
        }

        return Component.translatable("item.immersivegeology." + category.getName(), materialList.toArray());
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
