package com.igteam.immersivegeology.core.material.helper.flags;

import com.igteam.immersivegeology.client.IGClientRenderHandler.RenderTypeSkeleton;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;

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
        String prefix = stonePrefix(stone);

        return prefix + (richness.name().toLowerCase(Locale.ROOT) + "_" + getRegistryKey(ore.instance(), stone.instance()));
    }

    default String getRegistryKey(MaterialHelper ore, MaterialInterface<?> stone, OreRichness richness) {
        String prefix = stonePrefix(stone);

        return prefix +(richness.name().toLowerCase(Locale.ROOT) + "_" + getRegistryKey(ore, stone.instance()));
    }

    default String getRegistryKey(MaterialHelper ore, MaterialHelper stone, OreRichness richness) {
        String prefix = stonePrefix(stone);

        return prefix +(richness.name().toLowerCase(Locale.ROOT) + "_" + getRegistryKey(ore, stone));
    }

    /**
     * Ore block names are prefixed by where the host rock comes from. A rock type has its own prefix; anything
     * else falls back to deriving one from its mod flags, which is how these ids were built normally.
     * <p>
     * Obviously, not feasible for a pack developer's custom stone types.
     */
    private static String stonePrefix(Object stone)
    {
        if(stone instanceof IStoneType type) return type.getRegistryPrefix();

        String prefix = "";
        if(stone instanceof MaterialHelper helper)
        {
            for(ModFlags modflag : ModFlags.values())
            {
                if(helper.hasFlag(modflag)) prefix = modflag.name().toLowerCase(Locale.ROOT)+"_";
            }
        }
        return prefix;
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
