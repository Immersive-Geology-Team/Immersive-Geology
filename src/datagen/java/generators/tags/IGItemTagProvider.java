package generators.tags;

import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.core.lib.IGLib;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.data.stone.MaterialBaseStone;
import igteam.api.materials.helper.APIMaterials;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

import static igteam.api.tags.IGTags.IG_PATTERN_GROUP_TAGS;

public class IGItemTagProvider extends ItemTagsProvider {
    public IGItemTagProvider(DataGenerator generator, BlockTagsProvider blockProvider, ExistingFileHelper fileHelper) {
        super(generator, blockProvider, IGLib.MODID, fileHelper);
    }

    private Logger log = ImmersiveGeology.getNewLogger();

    @Override
    protected void registerTags() {
        for (BlockPattern pattern : BlockPattern.values()) {
            for (MaterialInterface<?> genMaterial : APIMaterials.generatedMaterials()) {
                if (genMaterial.hasPattern(pattern)) {
                    if (pattern == BlockPattern.ore) {
                        for (MaterialInterface<MaterialBaseStone> stone : StoneEnum.values()) {
                            ITag.INamedTag<Item> itag = genMaterial.getItemTag(pattern, stone.instance());
                            Block block = stone.getBlock(pattern, genMaterial);
                            if(block != null) {
                                tag(itag).add(block.asItem());
                                ITag.INamedTag<Item> groupTag = IG_PATTERN_GROUP_TAGS.get(pattern);
                                tag(groupTag).add(block.asItem());
                            } else {
                                log.error("Failed to find block with pattern: " + pattern.getName() + " and materials: " + genMaterial.getName());
                            }
                        }
                    }
                }
            }

            for (MaterialInterface<?> material : APIMaterials.all()) {
                if (pattern != BlockPattern.ore) {
                    if (material.hasPattern(pattern)) {
                        Block block = material.getBlock(pattern);
                        if (block != null) {
                            Item item = block.asItem();
                            log.warn("Attempting to bind item tag: " + item + " with " + item.getRegistryName());
                            tag(material.getItemTag(pattern)).add(item);
                            ITag.INamedTag<Item> groupTag = IG_PATTERN_GROUP_TAGS.get(pattern);
                            tag(groupTag).add(item);
                        } else {
                            log.error("Failed to get Block with singleton Pattern: " + pattern.getName());
                        }
                    }
                }
            }
        }

        for (ItemPattern pattern : ItemPattern.values()) {
            for (MaterialInterface<MaterialBaseMetal> metal : MetalEnum.values()) {
                if (metal.hasPattern(pattern)) {
                    switch (pattern) {
                        case dirty_crushed_ore: case ore_chunk: case ore_bit: {
                            for (MaterialInterface<MaterialBaseStone> stone : StoneEnum.values()) {
                                Item itm = metal.getItem(pattern, stone);
                                tag(metal.getItemTag(pattern, stone.instance())).add(itm);
                                tag(metal.getItemTag(pattern)).add(itm);
                                ITag.INamedTag<Item> groupTag = IG_PATTERN_GROUP_TAGS.get(pattern);
                                tag(groupTag).add(itm);
                            }
                        }
                        break;
                        default: {
                            Item item = metal.getItem(pattern);
                            tag(metal.getItemTag(pattern)).add(item);
                            ITag.INamedTag<Item> groupTag = IG_PATTERN_GROUP_TAGS.get(pattern);
                            tag(groupTag).add(item);
                        }
                    }
                }
            }

            for (MaterialInterface<MaterialBaseMineral> mineral : MineralEnum.values()) {
                if (mineral.hasPattern(pattern)) {
                    switch (pattern) {
                        case dirty_crushed_ore: case ore_chunk: case ore_bit: {
                            for (MaterialInterface<MaterialBaseStone> stone : StoneEnum.values()) {
                                Item item = mineral.getItem(pattern, stone);
                                tag(mineral.getItemTag(pattern)).add(item);
                                tag(mineral.getItemTag(pattern, stone.instance())).add(item);

                                ITag.INamedTag<Item> groupTag = IG_PATTERN_GROUP_TAGS.get(pattern);
                                tag(groupTag).add(item);
                            }
                        }
                        break;
                        default: {
                            Item item = mineral.getItem(pattern);
                            tag(mineral.getItemTag(pattern)).add(item);

                            ITag.INamedTag<Item> groupTag = IG_PATTERN_GROUP_TAGS.get(pattern);
                            tag(groupTag).add(item);
                        }
                        break;
                    }
                }
            }

            for (MaterialInterface<MaterialBaseStone> stone : StoneEnum.values()) {
                if (stone.hasPattern(pattern)) {
                    Item item = stone.getItem(pattern);
                    tag(stone.getItemTag(pattern)).add(item);

                    ITag.INamedTag<Item> groupTag = IG_PATTERN_GROUP_TAGS.get(pattern);
                    tag(groupTag).add(item);
                }
            }
        }
    }

    private TagsProvider.Builder<Item> tag(ITag.INamedTag<Item> tag) {
        return this.getOrCreateBuilder(tag);
    }
}
