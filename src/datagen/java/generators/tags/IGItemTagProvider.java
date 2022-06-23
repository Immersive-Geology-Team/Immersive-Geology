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
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.tags.IGTags;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import static igteam.immersive_geology.tags.IGTags.IG_PATTERN_GROUP_TAGS;

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
                            ITag.INamedTag<Item> itag = genMaterial.getItemTag(pattern, stone.get());
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
                                tag(metal.getItemTag(pattern, stone.get())).add(itm);
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
                                tag(mineral.getItemTag(pattern, stone.get())).add(item);

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
