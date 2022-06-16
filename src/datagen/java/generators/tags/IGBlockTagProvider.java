package generators.tags;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.MineralEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.data.stone.MaterialBaseStone;
import igteam.immersive_geology.materials.helper.APIMaterials;
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
        for (BlockPattern pattern : BlockPattern.values()) {
            for (MaterialInterface<?> genMaterial : APIMaterials.generatedMaterials()) {
                if (genMaterial.hasPattern(pattern)) {
                    if (pattern == BlockPattern.ore) {
                        for (MaterialInterface<MaterialBaseStone> stone : StoneEnum.values()) {
                            tag(genMaterial.getBlockTag(pattern, stone.get())).add(genMaterial.getBlock(pattern, stone));
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
