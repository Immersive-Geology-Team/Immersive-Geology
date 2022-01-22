package igteam.immersive_geology.generators;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.IGGenericBlock;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.helper.IGRegistryProvider;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

public class IGBlockStateProvider extends BlockStateProvider {

    private Logger log = ImmersiveGeology.getNewLogger();

    public IGBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper)
    {
        super(gen, IGLib.MODID, exFileHelper);
    }

    @Override
    public String getName(){
        return "Block Model/States";
    }

    @Override
    protected void registerStatesAndModels() {
        IGRegistryProvider.IG_BLOCK_REGISTRY.values().forEach((i) -> {
            if(i instanceof IGGenericBlock block){
                BlockPattern pattern = block.getPattern();
                switch(pattern){
                    case ore -> registerOreBlock(block);
//                    case stairs -> registerStairBlock(block);
//                    case slab -> registerSlabBlock(block);
                    case block, geode, storage -> registerGenericBlock(block);
                }
            }
        });
    }

    private void registerGenericBlock(IGGenericBlock block){
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(models().withExistingParent(
                new ResourceLocation(IGLib.MODID, "block/" + block.getHolderKey()).getPath(),
                new ResourceLocation(IGLib.MODID, "block/base/block"))
                        .texture("all", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getPattern()))
                        .texture("particle", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getPattern())))
                .build());
    }

    private void registerOreBlock(IGGenericBlock block){
        BlockModelBuilder baseModel;

        baseModel = models().withExistingParent(
                new ResourceLocation(IGLib.MODID, "block/" + block.getPattern().getName() + "_" + block.getMaterial(MaterialTexture.base).getName() + "_" + block.getMaterial(MaterialTexture.overlay).getName()).getPath(),
                        new ResourceLocation(IGLib.MODID, "block/base/" + block.getPattern().getName()))
                .texture("ore", block.getMaterial(MaterialTexture.overlay).getTextureLocation(block.getPattern()))
                .texture("base", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getPattern()));

        getVariantBuilder(block).forAllStates(blockState -> ConfiguredModel.builder().modelFile(baseModel).build());
    }

}
