package igteam.immersive_geology.generators.tags;

import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockFamily;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class IGBlockTagProvider extends BlockTagsProvider {
    public IGBlockTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, IGLib.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        for (MaterialInterface metal : MetalEnum.values()) {
            for (BlockFamily pattern : BlockFamily.values()) {
                if (metal.hasPattern(pattern)) {
                    switch (pattern){
                        case ore -> {
                            for (MaterialInterface stone : StoneEnum.values()) {
                                tag(metal.getBlockTag(pattern, stone.instance())).add(metal.getBlock(pattern, stone));
                            }
                        }
                        default -> {
                            Block block = metal.getBlock(pattern);
                            tag(metal.getBlockTag(pattern)).add(block);
                        }
                    }
                }
            }
        }
    }
}
