package generators.tags;

import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.core.lib.IGLib;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.data.stone.MaterialBaseStone;
import igteam.api.materials.helper.APIMaterials;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
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
        for (BlockPattern pattern : BlockPattern.values()) {
            for (MaterialInterface<?> genMaterial : APIMaterials.generatedMaterials()) {
                if (genMaterial.hasPattern(pattern)) {
                    if (pattern == BlockPattern.ore) {
                        for (MaterialInterface<MaterialBaseStone> stone : StoneEnum.values()) {
                            tag(genMaterial.getBlockTag(pattern, stone.instance())).add(genMaterial.getBlock(pattern, stone));
                        }
                    }
                }
            }

            for (MaterialInterface<?> material : APIMaterials.all()) {
                if (pattern != BlockPattern.ore) {
                    if (material.hasPattern(pattern)) {
                        Block block = material.getBlock(pattern);
                        if (block != null) {
                            ITag.INamedTag<Block> blockTag = material.getBlockTag(pattern);
                            log.warn("Attempting to bind block tag: " + blockTag.toString() + " with " + block.getRegistryName());
                            tag(blockTag).add(block);
                        } else {
                            log.error("Failed to get Block with singleton Pattern: " + pattern.getName());
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
