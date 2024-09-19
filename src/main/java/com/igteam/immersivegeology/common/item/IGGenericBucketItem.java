package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.client.menu.IGItemGroup;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
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
    private final BlockCategoryFlags fluid_category;

    public IGGenericBucketItem(Supplier<? extends Fluid> fluid, BlockCategoryFlags flag, MaterialInterface<?> material) {
        super(fluid, new Properties());
        this.materialMap.put(MaterialTexture.base, material);
        this.fluid_category = flag;
    }

    public IGGenericBucketItem(Supplier<? extends Fluid> fluid, BlockCategoryFlags flag, MaterialInterface<?> material, MaterialInterface<?> extra) {
        this(fluid, flag, material);
        this.materialMap.put(MaterialTexture.overlay, extra);
    }

    public int getColor(int index) {
        if(index == 0) return 0xffffffff;
        if (index >= materialMap.values().size()) index = materialMap.values().size() - 1;
        //let's use last available colour. map could not be empty
        return materialMap.get(MaterialTexture.values()[index]).getColor(ItemCategoryFlags.BUCKET);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        List<String> materialList = new ArrayList<>();
        String type = "bucket";
        MaterialInterface<?> baseMaterial = getMaterial(MaterialTexture.base);
        MaterialInterface<?> overlayMaterial = getMaterial(MaterialTexture.overlay);

        if(baseMaterial instanceof MetalEnum)
        {
            type = "bucket_molten";
        }

        if(baseMaterial instanceof ChemicalEnum)
        {
            type = "flask_slurry";

            if(overlayMaterial != null) {
                materialList.add(I18n.get("material.immersivegeology." + overlayMaterial.getName()));
                materialList.add(I18n.get("component.immersivegeology." + baseMaterial.getName()));
            } else {
                type = "flask";
                materialList.add(I18n.get("material.immersivegeology." + baseMaterial.getName()));
            }
        } else {
            materialList.add(I18n.get("material.immersivegeology." + baseMaterial.getName()));
        }

        return Component.translatable("item.immersivegeology." + type, materialList.toArray());
    }

    @Override
    public ItemCategoryFlags getFlag() {
        return ItemCategoryFlags.BUCKET;
    }

    public BlockCategoryFlags getFluidCategory()
    {
        return fluid_category;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return ItemCategoryFlags.BUCKET.getSubGroup();
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
