package igteam.immersive_geology.generators;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.IGGenericBlock;
import com.igteam.immersive_geology.common.block.IGStairBlock;
import com.igteam.immersive_geology.common.block.helper.IGBlockType;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.material.MaterialTexture;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

import java.util.List;

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
        List<Block> igBlocks = IGRegistrationHolder.supplyDeferredBlocks().get();
        for (Block block : igBlocks) {
            if(block instanceof IGBlockType igBlock) {
                BlockCategoryFlags flag = (BlockCategoryFlags) igBlock.getFlag();
                switch(flag){
                    case STAIRS -> registerStairsBlock(igBlock);
                    case DEFAULT_BLOCK, GEODE_BLOCK, RAW_ORE_BLOCK, DUST_BLOCK, SHEETMETAL_BLOCK, STORAGE_BLOCK -> registerGenericBlock(igBlock, flag);
                    case ORE_BLOCK -> registerOreBlock(igBlock);
                }
            }
        }
    }

    private void registerGenericBlock(IGBlockType type, IFlagType<?> pattern){
        IGGenericBlock block = (IGGenericBlock) type;

        log.info("Attempting Check for Texture Location: " + block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag()).getPath().toLowerCase());

        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(models().withExistingParent(
                                new ResourceLocation(IGLib.MODID, "block/" + pattern.toString().toLowerCase() + "/" + pattern.getRegistryKey(block.getMaterial(MaterialTexture.base))).getPath(),
                                new ResourceLocation(IGLib.MODID, "block/base/block"))
                        .texture("all", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag()))
                        .texture("particle", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag())))
                .build());
    }

    private void registerStairsBlock(IGBlockType blockType)
    {
        IGStairBlock stairsBlock = (IGStairBlock) blockType;
        VariantBlockStateBuilder builder = getVariantBuilder(stairsBlock);
        String materialName = stairsBlock.getMaterials().stream().findAny().get().instance().getName();
        BlockModelBuilder baseModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/stairs/stairs_" + materialName).getPath(),
                new ResourceLocation(IGLib.MODID, "block/base/stairs"));

        BlockModelBuilder innerModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/stairs/stairs_inner_" + materialName).getPath(),
                new ResourceLocation(IGLib.MODID, "block/base/stairs_inner"));

        BlockModelBuilder outerModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/stairs/stairs_outer_" +materialName).getPath(),
                new ResourceLocation(IGLib.MODID, "block/base/stairs_outer"));

        baseModel.texture("all", stairsBlock.getMaterial(MaterialTexture.base).getTextureLocation(BlockCategoryFlags.STORAGE_BLOCK));
        innerModel.texture("all", stairsBlock.getMaterial(MaterialTexture.base).getTextureLocation(BlockCategoryFlags.STORAGE_BLOCK));
        outerModel.texture("all", stairsBlock.getMaterial(MaterialTexture.base).getTextureLocation(BlockCategoryFlags.STORAGE_BLOCK));

        builder.forAllStates(blockState ->
                blockState.getValue(stairsBlock.SHAPE) == StairsShape.INNER_LEFT ?
                        (blockState.getValue(stairsBlock.HALF) == Half.BOTTOM ?
                                (blockState.getValue(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationY(180).uvLock(true).build() :
                                        blockState.getValue(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(90).uvLock(true).build() :
                                                blockState.getValue(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(270).uvLock(true).build() :
                                                        ConfiguredModel.builder().modelFile(innerModel).uvLock(true).build()) :

                                (blockState.getValue(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                                        blockState.getValue(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(180).uvLock(true).build() :
                                                blockState.getValue(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                                                        ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(90).uvLock(true).build())) :

                        blockState.getValue(stairsBlock.SHAPE) == StairsShape.INNER_RIGHT ?
                                (blockState.getValue(stairsBlock.HALF) == Half.BOTTOM ?
                                        (blockState.getValue(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationY(270).uvLock(true).build() :
                                                blockState.getValue(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(180).uvLock(true).build() :
                                                        blockState.getValue(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(0).uvLock(true).build() :
                                                                ConfiguredModel.builder().modelFile(innerModel).rotationY(90).uvLock(true).build()) :

                                        (blockState.getValue(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                                                blockState.getValue(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                                                        blockState.getValue(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(90).uvLock(true).build() :
                                                                ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(180).uvLock(true).build())) :

                                blockState.getValue(stairsBlock.SHAPE) == StairsShape.OUTER_LEFT ?
                                        (blockState.getValue(stairsBlock.HALF) == Half.BOTTOM ?
                                                (blockState.getValue(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationY(180).uvLock(true).build() :
                                                        blockState.getValue(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(90).uvLock(true).build() :
                                                                blockState.getValue(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(270).uvLock(true).build() :
                                                                        ConfiguredModel.builder().modelFile(outerModel).rotationY(0).uvLock(true).build()) :

                                                (blockState.getValue(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                                                        blockState.getValue(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(180).uvLock(true).build() :
                                                                blockState.getValue(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                                                                        ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(90).uvLock(true).build())) :

                                        blockState.getValue(stairsBlock.SHAPE) == StairsShape.OUTER_RIGHT ?
                                                (blockState.getValue(stairsBlock.HALF) == Half.BOTTOM ?
                                                        (blockState.getValue(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationY(270).uvLock(true).build() :
                                                                blockState.getValue(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(180).uvLock(true).build() :
                                                                        blockState.getValue(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(0).uvLock(true).build() :
                                                                                ConfiguredModel.builder().modelFile(outerModel).rotationY(90).build()) :

                                                        (blockState.getValue(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                                                                blockState.getValue(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                                                                        blockState.getValue(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(90).uvLock(true).build() :
                                                                                ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(180).uvLock(true).build())) :

                                                (blockState.getValue(stairsBlock.HALF) == Half.BOTTOM ?
                                                        (blockState.getValue(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(baseModel).rotationY(270).uvLock(true).build() :
                                                                blockState.getValue(stairsBlock.FACING) == Direction.SOUTH ? ConfiguredModel.builder().modelFile(baseModel).rotationY(90).uvLock(true).build() :
                                                                        blockState.getValue(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(baseModel).rotationY(0).uvLock(true).build() :
                                                                                ConfiguredModel.builder().modelFile(baseModel).rotationY(180).build()) :

                                                        (blockState.getValue(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(270).uvLock(true).build() :
                                                                blockState.getValue(stairsBlock.FACING) == Direction.SOUTH ? ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(90).uvLock(true).build() :
                                                                        blockState.getValue(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(0).uvLock(true).build() :
                                                                                ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(180).uvLock(true).build()))
        );
    }

    private void registerOreBlock(IGBlockType block){
        BlockModelBuilder baseModel;
        baseModel = models().withExistingParent(
                        new ResourceLocation(IGLib.MODID, "block/ore_block/" + block.getFlag().getName() + "_" + block.getMaterial(MaterialTexture.overlay).getName() + "_" + block.getMaterial(MaterialTexture.base).getName()).getPath(),
                        new ResourceLocation(IGLib.MODID, "block/base/" + block.getFlag().getName()))
                .texture("ore", block.getMaterial(MaterialTexture.overlay).getTextureLocation(block.getFlag()))
                .texture("base", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag()));

        getVariantBuilder(block.getBlock()).forAllStates(blockState -> ConfiguredModel.builder().modelFile(baseModel).build());
    }
}
