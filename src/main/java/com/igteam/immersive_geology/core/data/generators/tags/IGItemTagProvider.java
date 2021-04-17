package com.igteam.immersive_geology.core.data.generators.tags;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.common.item.IGOreItem;
import com.igteam.immersive_geology.core.data.generators.helpers.TagsIG;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

public class IGItemTagProvider extends ItemTagsProvider {
    public IGItemTagProvider(DataGenerator generator, BlockTagsProvider blockProvider, ExistingFileHelper fileHelper) {
        super(generator, blockProvider, IGLib.MODID, fileHelper);
    }

    private Logger log = ImmersiveGeology.getNewLogger();

    @Override
    protected void registerTags() {
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
                        case CRYSTAL:
                        case CUT_CRYSTAL:
                            if (container.getMaterial().hasSubtype(useType)) {
                                this.tag(Tags.Items.GEMS).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));
                                this.tag(requestCustomTag("gems", container.getMaterial())).add(IGRegistrationHolder.getItemByMaterial(container.getMaterial(), useType));

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
                                        log.info("Addding " + oreBlock.asItem().getRegistryName() + " to tags");
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
                            }
                    }
                }
            }
        }
    }

    private ITag.INamedTag<Item> requestUsetypeTag(MaterialUseType useType, Material material) {
        switch (useType){
            default:
                return TagsIG.itemTagForge(useType.getName().toLowerCase() + "s/" + material.getName().toLowerCase());
        }
    }
    private ITag.INamedTag<Item> requestUsetypeTag(MaterialUseType useType) {
        switch (useType){
            default:
                return TagsIG.itemTagForge(useType.getName().toLowerCase() + "s");
        }
    }

    private ITag.INamedTag<Item> requestCustomTag(String base, Material material){
        return TagsIG.itemTagForge(base + "/" + material.getName().toLowerCase());
    }

    private ITag.INamedTag<Item> requestOreUsetypeTag(Material material) {
        return TagsIG.itemTagForge("ores/" + material.getName().toLowerCase());
    }
    private ITag.INamedTag<Item> requestOreUsetypeTag() {
        return TagsIG.itemTagForge("ores");
    }

    private TagsProvider.Builder<Item> tag(ITag.INamedTag<Item> tag) {
        return this.getOrCreateBuilder(tag);
    }
}
