package generators.tags;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.legacy_api.materials.Material;
import com.igteam.immersive_geology.legacy_api.materials.MaterialEnum;
import com.igteam.immersive_geology.legacy_api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.block.legacy.IGOreBlock;
import com.igteam.immersive_geology.legacy_api.tags.IGTags;
import com.igteam.immersive_geology.legacy_api.tags.TagsIG;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class IGItemTagProvider extends ItemTagsProvider {
    public IGItemTagProvider(DataGenerator generator, BlockTagsProvider blockProvider, ExistingFileHelper fileHelper) {
        super(generator, blockProvider, IGLib.MODID, fileHelper);
    }

    private Logger log = ImmersiveGeology.getNewLogger();

    @Override
    protected void registerTags() {
        for(MaterialEnum wrapper : MaterialEnum.values()) {
            Material material = wrapper.getMaterial();
            IGTags.MaterialTags tags = IGTags.getTagsFor(material);

            Item crushed_ore = MaterialUseType.CRUSHED_ORE.getItem(material);

            Item ingot = MaterialUseType.INGOT.getItem(material);

            Item nugget = MaterialUseType.NUGGET.getItem(material);

            Item brick = MaterialUseType.BRICK.getItem(material);

            if (nugget != null) {
                assert tags.nugget != null;
                getOrCreateBuilder(tags.nugget).addItemEntry(nugget);
                log.debug("Addding " + tags.nugget.getName() + " to tags");
            }

            if(crushed_ore != null){
                assert tags.ore_crushed != null;
                getOrCreateBuilder(tags.ore_crushed).addItemEntry(crushed_ore);
                log.debug("Adding " + tags.ore_crushed.getName() + " to tags");
            }

            if (ingot != null) {
                assert tags.ingot != null;
                getOrCreateBuilder(tags.ingot).addItemEntry(ingot);
                log.debug("Addding " + tags.ingot.getName() + " to tags");
            }

            if (brick != null) {
                assert tags.ingot != null;
                assert tags.brick != null;
                getOrCreateBuilder(tags.ingot).addItemEntry(brick);
                getOrCreateBuilder(tags.brick).addItemEntry(brick);
                log.debug("Addding " + tags.brick.getName() + " to tags");
            }

            //dual material items
            for(MaterialEnum stone_base : MaterialEnum.stoneValues()) {
                Item dirty_crushed_ore = MaterialUseType.DIRTY_CRUSHED_ORE.getItem(stone_base.getMaterial(), material);

                if (dirty_crushed_ore != null) {
                    assert tags.dirty_ore_crushed != null;
                    getOrCreateBuilder(tags.dirty_ore_crushed).addItemEntry(dirty_crushed_ore);
                    log.debug("Adding " + tags.dirty_ore_crushed.getName() + " to tags");
                }
            }
        }

        for(MaterialUseType useType : MaterialUseType.values()) {
            for(MaterialEnum container : MaterialEnum.values()) {
                if(container.getMaterial().hasSubtype(useType)) {
                    switch (useType) {
                        case BUCKET:
                        case FLUIDS:
                        case ORE_BIT:
                        case ORE_CHUNK:
                        case ORE_STONE:
                            break;
                        case RAW_CRYSTAL:
                            if (container.getMaterial().hasSubtype(useType)) {
                                this.tag(requestUsetypeTag(useType)).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                                this.tag(requestUsetypeTag(useType, container.getMaterial())).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                            }
                            break;
                        case CUT_CRYSTAL:
                            if (container.getMaterial().hasSubtype(useType)) {
                                this.tag(Tags.Items.GEMS).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                                this.tag(requestCustomTag("crystals", container.getMaterial())).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));

                                this.tag(requestUsetypeTag(useType)).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                                this.tag(requestUsetypeTag(useType, container.getMaterial())).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                            }
                            break;
                        case ROCK_BIT:
                            //Add ore variants
                            for (MaterialEnum ore : MaterialEnum.values()) {
                                Material oreMat = ore.getMaterial();
                                if (oreMat.hasSubtype(MaterialUseType.ORE_BIT)) {
                                    Item chunk = IGRegistrationHolder.getItemByMaterial(container.getMaterial(), oreMat, MaterialUseType.ORE_BIT);
                                    this.tag(requestUsetypeTag(MaterialUseType.ORE_BIT)).add(chunk);
                                    this.tag(requestUsetypeTag(MaterialUseType.ORE_BIT, ore.getMaterial())).add(chunk);
                                }
                            }
                            //And normal variations
                            if (container.getMaterial().hasSubtype(useType)) {
                                this.tag(requestUsetypeTag(useType)).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                                this.tag(requestUsetypeTag(useType, container.getMaterial())).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                            }
                            break;
                        case CHUNK:
                            //Add ore variants
                            for (MaterialEnum ore : MaterialEnum.values()) {
                                Material oreMat = ore.getMaterial();
                                if (oreMat.hasSubtype(MaterialUseType.ORE_CHUNK)) {
                                    Item chunk = IGRegistrationHolder.getItemByMaterial(container.getMaterial(), oreMat, MaterialUseType.ORE_CHUNK);
                                    this.tag(requestUsetypeTag(MaterialUseType.ORE_CHUNK)).add(chunk);
                                    this.tag(requestUsetypeTag(MaterialUseType.ORE_CHUNK, ore.getMaterial())).add(chunk);
                                }
                            }
                            //And normal variations
                            if (container.getMaterial().hasSubtype(useType)) {
                                this.tag(requestUsetypeTag(useType)).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                                this.tag(requestUsetypeTag(useType, container.getMaterial())).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                            }
                            break;
                        case STONE:
                            for (MaterialEnum ore : MaterialEnum.values()) {
                                Material oreMat = ore.getMaterial();
                                if (oreMat.hasSubtype(MaterialUseType.ORE_STONE)) {
                                    Block block = IGRegistrationHolder.getBlockByMaterial(container.getMaterial(), oreMat, MaterialUseType.ORE_STONE);
                                    if(block instanceof IGOreBlock){
                                        IGOreBlock oreBlock = (IGOreBlock) block;
                                        this.tag(requestOreUsetypeTag()).add(oreBlock.asItem());
                                        this.tag(requestOreUsetypeTag(ore.getMaterial())).add(oreBlock.asItem());
                                        log.debug("Addding " + oreBlock.asItem().getRegistryName() + " to tags");
                                    }
                                }
                            }
                            //And normal variations
                            if (container.getMaterial().hasSubtype(useType)) {
                                this.tag(requestUsetypeTag(useType)).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                                this.tag(requestUsetypeTag(useType, container.getMaterial())).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                            }
                            break;
                        default:
                            if (container.getMaterial().hasSubtype(useType)) {
                                this.tag(requestUsetypeTag(useType)).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                                this.tag(requestUsetypeTag(useType, container.getMaterial())).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                                if(container.getMaterial().hasAdditionalTags()){
                                    ArrayList<String> tagList = container.getMaterial().getTagList();
                                    for(String tag : tagList){
                                        this.tag(requestCustomTag(tag)).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    private ITag.INamedTag<Item> requestUsetypeTag(MaterialUseType useType, Material material) {
        switch (useType){
            default:
                return TagsIG.itemTagForge(useType.getName().toLowerCase() +
                        (useType.getName().endsWith("s") ? "/" : "s/") + material.getName().toLowerCase());
        }
    }
    private ITag.INamedTag<Item> requestUsetypeTag(MaterialUseType useType) {
        switch (useType){
            default:
                return TagsIG.itemTagForge(useType.getName().toLowerCase() +
                        (useType.getName().endsWith("s") ? "" : "s"));
        }
    }

    private ITag.INamedTag<Item> requestCustomTag(String base, Material material){
        return TagsIG.itemTagForge(base + "/" + material.getName().toLowerCase());
    }

    private ITag.INamedTag<Item> requestCustomTag(String base){
        return TagsIG.itemTagForge(base);
    }

    private ITag.INamedTag<Item> requestOreUsetypeTag(Material material) {
        return TagsIG.itemTagForge("ore_bearing/" + material.getName().toLowerCase());
    }
    private ITag.INamedTag<Item> requestOreUsetypeTag() {
        return TagsIG.itemTagForge("ore_bearing");
    }

    private TagsProvider.Builder<Item> tag(ITag.INamedTag<Item> tag) {
        return this.getOrCreateBuilder(tag);
    }
}
