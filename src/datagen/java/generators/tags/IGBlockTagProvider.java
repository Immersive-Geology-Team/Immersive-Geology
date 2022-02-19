package generators.tags;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.MineralEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

public class IGBlockTagProvider extends BlockTagsProvider {
    public IGBlockTagProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, IGLib.MODID, existingFileHelper);
    }

    private static Logger log = ImmersiveGeology.getNewLogger();

    @Override
    protected void registerTags() {
        for (MaterialInterface metal : MetalEnum.values()) {
            for (BlockPattern pattern : BlockPattern.values()) {
                if (metal.hasPattern(pattern)) {
                    switch (pattern){
                        case ore: {
                            for (MaterialInterface stone : StoneEnum.values()) {
                                tag(metal.getBlockTag(pattern, stone.get())).add(metal.getBlock(pattern, stone));
                            }
                        }
                        default: {
                            Block block = metal.getBlock(pattern);
                            tag(metal.getBlockTag(pattern)).add(block);
                        }
                    }
                }
            }
        }

        for (MaterialInterface mineral : MineralEnum.values()) {
            for (BlockPattern pattern : BlockPattern.values()) {
                if (mineral.hasPattern(pattern)) {
                    switch (pattern){
                        case ore: {
                            for (MaterialInterface stone : StoneEnum.values()) {
                                tag(mineral.getBlockTag(pattern, stone.get())).add(mineral.getBlock(pattern, stone));
                            }
                        }
                        default: {
                            Block block = mineral.getBlock(pattern);
                            tag(mineral.getBlockTag(pattern)).add(block);
                        }
                    }
                }
            }
        }
    }

    private TagsProvider.Builder<Block> tag(ITag.INamedTag<Block> tag) {
        return this.getOrCreateBuilder(tag);
    }

}
