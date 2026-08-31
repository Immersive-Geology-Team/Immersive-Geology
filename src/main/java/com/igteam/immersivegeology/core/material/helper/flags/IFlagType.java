package com.igteam.immersivegeology.core.material.helper.flags;

import com.igteam.immersivegeology.client.IGClientRenderHandler.RenderTypeSkeleton;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        return getValue().name().toLowerCase(Locale.ROOT) + "_" + material.getName().toLowerCase(Locale.ROOT);
    }

    default String getRegistryKey(MaterialHelper ore, MaterialHelper stone) {
        return getValue().name().toLowerCase(Locale.ROOT) + "_" + ore.getName().toLowerCase(Locale.ROOT) + "_" + stone.getName().toLowerCase(Locale.ROOT);
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
                prefix = modflag.name().toLowerCase(Locale.ROOT) + "_";
            }
        }

        return prefix + (richness.name().toLowerCase(Locale.ROOT) + "_" + getRegistryKey(ore.instance(), stone.instance()));
    }

    default String getRegistryKey(MaterialHelper ore, MaterialInterface<?> stone, OreRichness richness) {
        String prefix = "";

        for(ModFlags modflag : ModFlags.values())
        {
            if(stone.hasFlag(modflag))
            {
                prefix = modflag.name().toLowerCase(Locale.ROOT) + "_";
            }
        }

        return prefix +(richness.name().toLowerCase(Locale.ROOT) + "_" + getRegistryKey(ore, stone.instance()));
    }

    default String getRegistryKey(MaterialHelper ore, MaterialHelper stone, OreRichness richness) {
        String prefix = "";

        for(ModFlags modflag : ModFlags.values())
        {
            if(stone.hasFlag(modflag))
            {
                prefix = modflag.name().toLowerCase(Locale.ROOT) + "_";
            }
        }

        return prefix +(richness.name().toLowerCase(Locale.ROOT) + "_" + getRegistryKey(ore, stone));
    }

    default String getRegistryKey(MaterialInterface<?> material, BlockCategoryFlags blockCategory){
        return getValue().name().toLowerCase(Locale.ROOT) + "_" + material.getName().toLowerCase(Locale.ROOT) + "_" + blockCategory.getName().toLowerCase(Locale.ROOT);
    }

    default ItemSubGroup getSubGroup() {
        return ItemSubGroup.values()[0];
    };

    default String getName() {
        return getValue().name().toLowerCase(Locale.ROOT);
    }

	default RenderTypeSkeleton getRenderType() {return RenderTypeSkeleton.SOLID;};
}
