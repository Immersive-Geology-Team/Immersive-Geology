package igteam.immersivegeology.generators;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.data.models.NongeneratedModels;
import blusunrize.immersiveengineering.data.models.SplitModelBuilder;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.block.IGGenericBlock;
import com.igteam.immersivegeology.common.block.IGStairBlock;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.block.multiblock.crystallizer.CrystallizerMultiblock;
import com.igteam.immersivegeology.common.block.multiblock.gravityseparator.GravitySeparatorMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGMultiblockHolder;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IGBlockStateProvider extends BlockStateProvider {

    private Logger log = ImmersiveGeology.getNewLogger();
    private final NongeneratedModels nongeneratedModels;
    final ExistingFileHelper exFileHelper;
    private final BlockModelProvider customModels;
    public IGBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper)
    {
        super(gen, IGLib.MODID, exFileHelper);
        this.exFileHelper = exFileHelper;
        this.nongeneratedModels = new NongeneratedModels(gen, exFileHelper);
        this.customModels = new BlockModelProvider(gen, IGLib.MODID, exFileHelper) {

            protected void registerModels() {

            }
        };
    }
    @Override
    public @NotNull String getName(){
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

        crystallizer();
        gravityseparator();
    }

    private void crystallizer(){
        ResourceLocation texture = modLoc("multiblock/crystallizer");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/crystallizer/crystallizer.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/crystallizer/crystallizer_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(IGMultiblockHolder.CRYSTALLIZER.get(), modelNormal, texture, "", CrystallizerMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(IGMultiblockHolder.CRYSTALLIZER.get(), modelMirrored, texture, "_mirrored", CrystallizerMultiblock.INSTANCE, true);

        createMultiblock(IGMultiblockHolder.CRYSTALLIZER.get(), normal, mirrored, texture);
    }
    private void gravityseparator(){
        ResourceLocation texture = modLoc("multiblock/gravityseparator");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/gravityseparator/gravityseparator.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/gravityseparator/gravityseparator_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(IGMultiblockHolder.GRAVITY_SEPARATOR.get(), modelNormal, texture, "", GravitySeparatorMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(IGMultiblockHolder.GRAVITY_SEPARATOR.get(), modelMirrored, texture, "_mirrored", GravitySeparatorMultiblock.INSTANCE, true);

        createMultiblock(IGMultiblockHolder.GRAVITY_SEPARATOR.get(), normal, mirrored, texture);
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

    public BlockModelProvider customModels() {
        return this.customModels;
    }

    private void registerOreBlock(IGBlockType block){
        BlockModelBuilder baseModel = models().withExistingParent(
                new ResourceLocation(IGLib.MODID, "block/ore_block/" + block.getFlag().getName() + "_" + block.getMaterial(MaterialTexture.overlay).getName() + "_" + block.getMaterial(MaterialTexture.base).getName()).getPath(),
                new ResourceLocation(IGLib.MODID, "block/base/" + block.getFlag().getName()));

        try {
            baseModel.texture("ore", block.getMaterial(MaterialTexture.overlay).getTextureLocation(block.getFlag())).texture("base", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag()));

        } catch(IllegalArgumentException error) {
            // DO NOT DO THIS, I've used an access transformer to FORCE it to add the base texture for modded content that fails to load in a data gen environment
            // This is horrid, do not copy, please, it's bad practice, don't do as I do.
            // NOTE: only needed as a data gen implementation for some mods are unavailable, which can cause it to crash.
            baseModel.textures.put("base", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag()).toString());
        }


        getVariantBuilder(block.getBlock()).forAllStates(blockState -> ConfiguredModel.builder().modelFile(baseModel).build());
    }

    private BlockModelBuilder multiblockModel(Block block, ResourceLocation model, ResourceLocation texture, String add, TemplateMultiblock mb, boolean mirror){
        UnaryOperator<BlockPos> transform = UnaryOperator.identity();
        if(mirror){
            Vec3i size = mb.getSize(null);
            transform = p -> new BlockPos(size.getX() - p.getX() - 1, p.getY(), p.getZ());
        }
        final Vec3i offset = mb.getMasterFromOriginOffset();

        Stream<Vec3i> partsStream = mb.getStructure(null).stream()
                .filter(info -> !info.state.isAir())
                .map(info -> info.pos)
                .map(transform)
                .map(p -> p.subtract(offset));

        String name = getMultiblockPath(block) + add;
        NongeneratedModels.NongeneratedModel base = nongeneratedModels.withExistingParent(name, mcLoc("block"))
                .customLoader(OBJLoaderBuilder::begin).modelLocation(model).detectCullableFaces(false).flipV(true).end()
                .texture("texture", texture)
                .texture("particle", texture);

        BlockModelBuilder split = this.models().withExistingParent(name + "_split", mcLoc("block"))
                .customLoader(SplitModelBuilder::begin)
                .innerModel(base)
                .parts(partsStream.collect(Collectors.toList()))
                .dynamic(false).end();

        return split;
    }


    /** From {@link blusunrize.immersiveengineering.data.blockstates.BlockStates} */
    private void createMultiblock(Block b, ModelFile masterModel, ModelFile mirroredModel, ResourceLocation particleTexture){
        createMultiblock(b, masterModel, mirroredModel, IEProperties.MULTIBLOCKSLAVE, IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED, 180, particleTexture);
    }

    /** From {@link blusunrize.immersiveengineering.data.blockstates.BlockStates} */
    private void createMultiblock(Block b, ModelFile masterModel, @Nullable ModelFile mirroredModel, Property<Boolean> isSlave, EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState, int rotationOffset, ResourceLocation particleTex){
        Preconditions.checkArgument((mirroredModel == null) == (mirroredState == null));
        VariantBlockStateBuilder builder = getVariantBuilder(b);

        boolean[] possibleMirrorStates;
        if(mirroredState != null)
            possibleMirrorStates = new boolean[]{false, true};
        else
            possibleMirrorStates = new boolean[1];
        for(boolean mirrored:possibleMirrorStates)
            for(Direction dir:facing.getPossibleValues()){
                final int angleY;
                final int angleX;
                if(facing.getPossibleValues().contains(Direction.UP)){
                    angleX = -90 * dir.getStepY();
                    if(dir.getAxis() != Direction.Axis.Y)
                        angleY = getAngle(dir, rotationOffset);
                    else
                        angleY = 0;
                }else{
                    angleY = getAngle(dir, rotationOffset);
                    angleX = 0;
                }

                ModelFile model = mirrored ? mirroredModel : masterModel;
                VariantBlockStateBuilder.PartialBlockstate partialState = builder.partialState()
//						.with(isSlave, false)
                        .with(facing, dir);

                if(mirroredState != null)
                    partialState = partialState.with(mirroredState, mirrored);

                partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
            }
    }

    /** From {@link blusunrize.immersiveengineering.data.blockstates.BlockStates} */
    private int getAngle(Direction dir, int offset){
        return (int) ((dir.toYRot() + offset) % 360);
    }

    private String getMultiblockPath(Block b){
        return "multiblock/" + getPath(b);
    }

    private String getPath(Block b){
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(b)).getPath();
    }
}
