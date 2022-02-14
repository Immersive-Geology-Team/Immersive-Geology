package generators.tags;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.legacy_api.materials.Material;
import com.igteam.immersive_geology.legacy_api.materials.MaterialEnum;
import com.igteam.immersive_geology.legacy_api.materials.MaterialUseType;
import com.igteam.immersive_geology.legacy_api.tags.TagsIG;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
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
        for(MaterialUseType useType : MaterialUseType.values()) {
            for(MaterialEnum container : MaterialEnum.values()) {
                if(container.getMaterial().hasSubtype(useType)) {
                    switch (useType) {
                        case STONE:
                            for (MaterialEnum ore : MaterialEnum.values()) {
                                Material oreMat = ore.getMaterial();
                                if (oreMat.hasSubtype(MaterialUseType.ORE_STONE)) {
                                    Block oreBlock = IGRegistrationHolder.getBlockByMaterial(container.getMaterial(), oreMat, MaterialUseType.ORE_STONE);
                                    this.tag(requestOreUsetypeTag()).add(oreBlock);
                                    this.tag(requestOreUsetypeTag(ore.getMaterial())).add(oreBlock);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private TagsProvider.Builder<Block> tag(ITag.INamedTag<Block> tag) {
        return this.getOrCreateBuilder(tag);
    }

    private ITag.INamedTag<Block> requestUsetypeTag(MaterialUseType useType, Material material) {
        switch (useType){
            default:
                return TagsIG.blockTagForge(useType.getName().toLowerCase() + "s/" + material.getName().toLowerCase());
        }
    }
    private ITag.INamedTag<Block> requestUsetypeTag(MaterialUseType useType) {
        switch (useType){
            default:
                return TagsIG.blockTagForge(useType.getName().toLowerCase() + "s");
        }
    }

    private ITag.INamedTag<Block> requestOreUsetypeTag(Material material) {
        return TagsIG.blockTagForge("ores/" + material.getName().toLowerCase());
    }
    private ITag.INamedTag<Block> requestOreUsetypeTag() {
        return TagsIG.blockTagForge("ores");
    }
}
