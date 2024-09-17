package com.igteam.immersivegeology.core.material.helper.flags;

import com.igteam.immersivegeology.client.IGClientRenderHandler.RenderTypeSkeleton;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;

import java.util.ArrayList;
import java.util.List;

public interface IFlagType<T extends Enum<T>> {
    T getValue();

    String getTagPrefix();

    static List<IFlagType<?>> getAllRegistryFlags() {
        List<IFlagType<?>> list = new ArrayList<>();
        list.addAll(List.of(BlockCategoryFlags.values()));
        list.addAll(List.of(ItemCategoryFlags.values()));
        return list;
    }

    default String getRegistryKey(MaterialHelper material) {
        return getValue().name().toLowerCase() + "_" + material.getName().toLowerCase();
    }

    default String getRegistryKey(MaterialHelper ore, MaterialHelper stone) {
        return getValue().name().toLowerCase() + "_" + ore.getName().toLowerCase() + "_" + stone.getName().toLowerCase();
    }

    default String getRegistryKey(MaterialInterface<?> material) {
        return getRegistryKey(material.instance());
    }

    default String getRegistryKey(MaterialInterface<?> ore, MaterialInterface<?> stone) {
        return getRegistryKey(ore.instance(), stone.instance());
    }

    default String getRegistryKey(MaterialInterface<?> ore, MaterialInterface<?> stone, OreRichness richness) {
        String prefix = "";

        for(ModFlags modflag : ModFlags.values())
        {
            if(stone.hasFlag(modflag))
            {
                prefix = modflag.name().toLowerCase() + "_";
            }
        }

        return prefix + (richness.name().toLowerCase() + "_" + getRegistryKey(ore.instance(), stone.instance()));
    }

    default String getRegistryKey(MaterialHelper ore, MaterialInterface<?> stone, OreRichness richness) {
        String prefix = "";

        for(ModFlags modflag : ModFlags.values())
        {
            if(stone.hasFlag(modflag))
            {
                prefix = modflag.name().toLowerCase() + "_";
            }
        }

        return prefix +(richness.name().toLowerCase() + "_" + getRegistryKey(ore, stone.instance()));
    }

    default String getRegistryKey(MaterialInterface<?> material, BlockCategoryFlags blockCategory){
        return getValue().name().toLowerCase() + "_" + material.getName().toLowerCase() + "_" + blockCategory.getName().toLowerCase();
    }

    default ItemSubGroup getSubGroup() {
        return ItemSubGroup.values()[0];
    };

    default String getName() {
        return getValue().name().toLowerCase();
    }

	default RenderTypeSkeleton getRenderType() {return RenderTypeSkeleton.SOLID;};

}
