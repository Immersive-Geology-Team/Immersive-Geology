package igteam.immersive_geology.generators.tags;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.helper.IGRegistryProvider;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class IGItemTagProvider extends ItemTagsProvider {
    public IGItemTagProvider(DataGenerator generator, BlockTagsProvider blockProvider, @Nullable ExistingFileHelper fileHelper) {
        super(generator, blockProvider, IGLib.MODID, fileHelper);
    }

    @Override
    protected void addTags() {
        for (MaterialInterface metal : MetalEnum.values()) {
            for (ItemPattern pattern : ItemPattern.values()) {
                if (metal.hasPattern(pattern)) {
                    switch (pattern){
                        case dirty_crushed_ore, ore_chunk, ore_bit -> {
                            for (MaterialInterface stone : StoneEnum.values()) {
                                tag(metal.getItemTag(pattern)).add(metal.getItem(pattern, stone));
                            }
                        }
                        default -> {
                            Item item = metal.getItem(pattern);
                            metal.getItem(pattern);
                            tag(metal.getItemTag(pattern)).add(item);
                        }
                    }
                }
            }
        }
    }
}
