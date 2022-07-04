package generators;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.data.models.SplitModelBuilder;
import com.google.common.base.Preconditions;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.IGGenericBlock;
import com.igteam.immersive_geology.common.block.blocks.IGSlabBlock;
import com.igteam.immersive_geology.common.block.blocks.IGStairsBlock;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.common.multiblocks.*;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import igteam.immersive_geology.block.IGBlockType;
import igteam.immersive_geology.main.IGMultiblockProvider;
import igteam.immersive_geology.main.IGRegistryProvider;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            if(i instanceof IGBlockType){
                IGBlockType type = (IGBlockType) i;
                if(type.getPattern() instanceof BlockPattern) {
                    BlockPattern pattern = (BlockPattern) type.getPattern();
                    switch (pattern) {
                        case ore:
                            registerOreBlock(type);
                            break;
                        case stairs :
                            registerStairsBlock(type);
                            break;
                        case slab:
                            registerSlabBlock(type);
                            break;
                        case block:
                        case geode:
                        case storage:
                        case sheetmetal:
                        case dust_block:
                            registerGenericBlock(type, pattern);
                            break;
                    }
                } else {
                    String name = Objects.requireNonNull(type.getBlock().getRegistryName()).toString();
                    log.warn("Found Non Block Pattern in Block Registry for: " + name);
                }
            }
        });

        chemicalvat();
        gravityseparator();
        reverberation_furnace();
        crystallizer();
        rotarykiln();

        //Used to assign a multiblock it's Model and Texture. - Unsure but I also think this is influenced somewhat by the NBT structure file but unable to confirm.
        hydrojet_cutter();

        registerFluidBlocks();
    }

    private void gravityseparator() {
        ResourceLocation texture = modLoc("multiblock/gravityseparator_base");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/gravityseparator/gravityseparator.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/gravityseparator/gravityseparator_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(IGMultiblockProvider.gravityseparator, modelNormal, texture, "", GravitySeparatorMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(IGMultiblockProvider.gravityseparator, modelMirrored, texture, "_mirrored", GravitySeparatorMultiblock.INSTANCE, true);

        createMultiblock(IGMultiblockProvider.gravityseparator, normal, mirrored, texture);
    }

    private void rotarykiln() {
        ResourceLocation texture = modLoc("multiblock/rotarykiln");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/rotarykiln/rotarykiln.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/rotarykiln/rotarykiln.obj");

        BlockModelBuilder normal = multiblockModel(IGMultiblockProvider.rotarykiln, modelNormal, texture, "", RotaryKilnMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(IGMultiblockProvider.rotarykiln, modelMirrored, texture, "_mirrored", RotaryKilnMultiblock.INSTANCE, true);

        createMultiblock(IGMultiblockProvider.rotarykiln, normal, mirrored, texture);
    }

    private void crystallizer() {
        ResourceLocation texture = modLoc("multiblock/crystallizer");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/crystallizer/crystallizer.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/crystallizer/crystallizer_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(IGMultiblockProvider.crystallizer, modelNormal, texture, "", CrystallizerMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(IGMultiblockProvider.crystallizer, modelMirrored, texture, "_mirrored", CrystallizerMultiblock.INSTANCE, true);

        createMultiblock(IGMultiblockProvider.crystallizer, normal, mirrored, texture);
    }

    private void chemicalvat(){
        ResourceLocation texture = modLoc("multiblock/chemicalvat_base");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/chemicalvat/chemicalvat.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/chemicalvat/chemicalvat_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(IGMultiblockProvider.chemicalvat, modelNormal, texture, "", ChemicalVatMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(IGMultiblockProvider.chemicalvat, modelMirrored, texture, "_mirrored", ChemicalVatMultiblock.INSTANCE, true);

        createMultiblock(IGMultiblockProvider.chemicalvat, normal, mirrored, texture);
    }

    private void reverberation_furnace(){
        ResourceLocation texture = modLoc("multiblock/reverberation_furnace");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/revfurnace/reverberation_furnace.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/revfurnace/reverberation_furnace.obj");

        BlockModelBuilder normal = multiblockModel(IGMultiblockProvider.reverberation_furnace, modelNormal, texture, "", ReverberationFurnaceMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(IGMultiblockProvider.reverberation_furnace, modelMirrored, texture, "_mirrored", ReverberationFurnaceMultiblock.INSTANCE, true);

        createMultiblock(IGMultiblockProvider.reverberation_furnace, normal, mirrored, texture);
    }

    private void hydrojet_cutter(){
        ResourceLocation texture = modLoc("multiblock/hydrojet");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/hydrojet/hydrojet.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/hydrojet/hydrojet.obj");

        BlockModelBuilder normal = multiblockModel(IGMultiblockProvider.hydrojet_cutter, modelNormal, texture, "", HydroJetCutterMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(IGMultiblockProvider.hydrojet_cutter, modelMirrored, texture, "_mirrored", HydroJetCutterMultiblock.INSTANCE, true);

        createMultiblock(IGMultiblockProvider.hydrojet_cutter, normal, mirrored, texture);
    }


    private BlockModelBuilder multiblockModel(Block block, ResourceLocation model, ResourceLocation texture, String add, TemplateMultiblock mb, boolean mirror){
        log.info("Starting Generation of Multiblock Models: " + model.toString());

        UnaryOperator<BlockPos> transform = UnaryOperator.identity();
        if(mirror){
            Vector3i size = mb.getSize(null);
            transform = p -> new BlockPos(size.getX() - p.getX() - 1, p.getY(), p.getZ());
        }
        final Vector3i offset = mb.getMasterFromOriginOffset();
        @SuppressWarnings("deprecation")
        Stream<Vector3i> partsStream = mb.getStructure(null).stream()
                .filter(info -> !info.state.isAir())
                .map(info -> info.pos)
                .map(transform)
                .map(p -> p.subtract(offset));

        String name = getMultiblockPath(block) + add;
        BlockModelBuilder base = this.models().withExistingParent(name, mcLoc("block"))
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

    private void registerFluidBlocks(){
        for(IGFluid f : IGFluid.IG_FLUIDS){
            ResourceLocation stillTexture = f.getAttributes().getStillTexture();
            ModelFile model = models().getBuilder("block/fluid/"+f.getRegistryName().getPath())
                    .texture("particle", stillTexture);
            getVariantBuilder(f.block).partialState().setModels(new ConfiguredModel(model));
        }
    }

    private void registerGenericBlock(IGBlockType type, MaterialPattern pattern){
        IGGenericBlock block = (IGGenericBlock) type;
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(models().withExistingParent(
                                new ResourceLocation(IGLib.MODID, "block/" + pattern.getName() + "/" + block.getHolderKey()).getPath(),
                                new ResourceLocation(IGLib.MODID, "block/base/block"))
                        .texture("all", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getPattern()))
                        .texture("particle", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getPattern())))
                .build());
    }

    private void registerOreBlock(IGBlockType type){
        BlockModelBuilder baseModel;
        IGGenericBlock block = (IGGenericBlock) type;
        baseModel = models().withExistingParent(
                        new ResourceLocation(IGLib.MODID, "block/ore/" + block.getPattern().getName() + "_" + block.getMaterial(MaterialTexture.base).getName() + "_" + block.getMaterial(MaterialTexture.overlay).getName()).getPath(),
                        new ResourceLocation(IGLib.MODID, "block/base/" + block.getPattern().getName()))
                .texture("ore", block.getMaterial(MaterialTexture.overlay).getTextureLocation(block.getPattern()))
                .texture("base", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getPattern()));

        getVariantBuilder(block).forAllStates(blockState -> ConfiguredModel.builder().modelFile(baseModel).build());
    }

    private void registerSlabBlock(IGBlockType igBlockType)
    {
        IGSlabBlock slabBlock = (IGSlabBlock) igBlockType;
        VariantBlockStateBuilder builder = getVariantBuilder(slabBlock);
        BlockModelBuilder baseModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/slab/" + slabBlock.getPattern().getName() + "_" + slabBlock.getMaterial(MaterialTexture.base).getName()).getPath(),
                new ResourceLocation(IGLib.MODID, "block/base/slab/" + slabBlock.getPattern().getName()));

        BlockModelBuilder topModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/slab/" + slabBlock.getPattern().getName() + "_top_" + slabBlock.getMaterial(MaterialTexture.base).getName()).getPath(),
                new ResourceLocation(IGLib.MODID, "block/base/slab/" + slabBlock.getPattern().getName()+ "_top"));

        BlockModelBuilder doubleModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/slab/" + slabBlock.getPattern().getName() + "_double_" + slabBlock.getMaterial(MaterialTexture.base).getName()).getPath(),
                new ResourceLocation(IGLib.MODID, "block/base/slab/" + slabBlock.getPattern().getName()+ "_double"));

        ResourceLocation rTextureLocBase = slabBlock.getMaterial(MaterialTexture.base).getTextureLocation(slabBlock.getPattern());
        ResourceLocation rTextureLocSide = slabBlock.getMaterial(MaterialTexture.base).getTextureLocation(slabBlock.getPattern());

        baseModel.texture("particle", rTextureLocBase);
        topModel.texture("particle", rTextureLocBase);
        doubleModel.texture("particle", rTextureLocBase);

        doubleModel.texture("all", rTextureLocBase);
        topModel.texture("all", rTextureLocBase);
        baseModel.texture("all", rTextureLocBase);


        doubleModel.texture("side", rTextureLocSide);
        doubleModel.texture("cover", rTextureLocBase);

        topModel.texture("side", rTextureLocSide);
        topModel.texture("cover", rTextureLocBase);

        baseModel.texture("side", rTextureLocSide);
        baseModel.texture("cover", rTextureLocBase);

        builder.forAllStates(blockState ->
                blockState.get(slabBlock.TYPE) == SlabType.BOTTOM ?
                        (ConfiguredModel.builder().modelFile(baseModel).uvLock(true).build()):
                blockState.get(slabBlock.TYPE) == SlabType.TOP ?
                        (ConfiguredModel.builder().modelFile(topModel).uvLock(true).build()):
                        (ConfiguredModel.builder().modelFile(doubleModel).uvLock(true).build()));

    }

    private void registerStairsBlock(IGBlockType blockType)
    {
            IGStairsBlock stairsBlock = (IGStairsBlock) blockType;
            VariantBlockStateBuilder builder = getVariantBuilder(stairsBlock);
            String materialName = stairsBlock.getMaterials().stream().findAny().get().get().getName();
            BlockModelBuilder baseModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/stairs/" + stairsBlock.getPattern().getName() + "_" + materialName).getPath(),
                    new ResourceLocation(IGLib.MODID, "block/base/" + stairsBlock.getPattern().getName()));

            BlockModelBuilder innerModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/stairs/" + stairsBlock.getPattern().getName() + "_inner_" + materialName).getPath(),
                    new ResourceLocation(IGLib.MODID, "block/base/" + stairsBlock.getPattern().getName()+ "_inner"));

            BlockModelBuilder outerModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/stairs/" + stairsBlock.getPattern().getName() + "_outer_" +materialName).getPath(),
                    new ResourceLocation(IGLib.MODID, "block/base/" + stairsBlock.getPattern().getName()+ "_outer"));

            baseModel.texture("all", stairsBlock.getMaterial(MaterialTexture.base).getTextureLocation(BlockPattern.storage));
            innerModel.texture("all", stairsBlock.getMaterial(MaterialTexture.base).getTextureLocation(BlockPattern.storage));
            outerModel.texture("all", stairsBlock.getMaterial(MaterialTexture.base).getTextureLocation(BlockPattern.storage));

            builder.forAllStates(blockState ->
                    blockState.get(stairsBlock.SHAPE) == StairsShape.INNER_LEFT ?
                        (blockState.get(stairsBlock.HALF) == Half.BOTTOM ?
                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(90).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(270).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(innerModel).uvLock(true).build()) :

                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(90).uvLock(true).build())) :

                    blockState.get(stairsBlock.SHAPE) == StairsShape.INNER_RIGHT ?
                        (blockState.get(stairsBlock.HALF) == Half.BOTTOM ?
                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationY(270).uvLock(true).build() :
                             blockState.get(StairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(0).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(innerModel).rotationY(90).uvLock(true).build()) :

                           (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                            blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                            blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(90).uvLock(true).build() :
                            ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(180).uvLock(true).build())) :

                    blockState.get(stairsBlock.SHAPE) == StairsShape.OUTER_LEFT ?
                         (blockState.get(stairsBlock.HALF) == Half.BOTTOM ?
                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(90).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(270).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(outerModel).rotationY(0).uvLock(true).build()) :

                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(90).uvLock(true).build())) :

                    blockState.get(stairsBlock.SHAPE) == StairsShape.OUTER_RIGHT ?
                        (blockState.get(stairsBlock.HALF) == Half.BOTTOM ?
                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationY(270).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(0).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(outerModel).rotationY(90).build()) :

                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(90).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(180).uvLock(true).build())) :

                    (blockState.get(stairsBlock.HALF) == Half.BOTTOM ?
                       (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(baseModel).rotationY(270).uvLock(true).build() :
                        blockState.get(stairsBlock.FACING) == Direction.SOUTH ? ConfiguredModel.builder().modelFile(baseModel).rotationY(90).uvLock(true).build() :
                        blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(baseModel).rotationY(0).uvLock(true).build() :
                        ConfiguredModel.builder().modelFile(baseModel).rotationY(180).build()) :

                       (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(270).uvLock(true).build() :
                        blockState.get(stairsBlock.FACING) == Direction.SOUTH ? ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(90).uvLock(true).build() :
                        blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(0).uvLock(true).build() :
                        ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(180).uvLock(true).build()))
        );
    }
    private void createMultiblock(Block b, ModelFile masterModel, ModelFile mirroredModel, ResourceLocation particleTexture){
        createMultiblock(b, masterModel, mirroredModel, IEProperties.MULTIBLOCKSLAVE, IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED, 180, particleTexture);
    }

    private void createMultiblock(Block b, ModelFile masterModel, @Nullable ModelFile mirroredModel, Property<Boolean> isSlave, EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState, int rotationOffset, ResourceLocation particleTex){
        Preconditions.checkArgument((mirroredModel == null) == (mirroredState == null));
        VariantBlockStateBuilder builder = getVariantBuilder(b);

        boolean[] possibleMirrorStates;
        if(mirroredState != null)
            possibleMirrorStates = new boolean[]{false, true};
        else
            possibleMirrorStates = new boolean[1];
        for(boolean mirrored:possibleMirrorStates)
            for(Direction dir:facing.getAllowedValues()){
                final int angleY;
                final int angleX;
                if(facing.getAllowedValues().contains(Direction.UP)){
                    angleX = -90 * dir.getYOffset();
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

    private int getAngle(Direction dir, int offset){
        return (int) ((dir.getHorizontalAngle() + offset) % 360);
    }

    private String getMultiblockPath(Block b){
        return "multiblock/" + getPath(b);
    }

    private String getPath(Block b){
        return b.getRegistryName().getPath();
    }

    private void itemModelWithParent(Block block, ModelFile parent){
        getItemBuilder(block).parent(parent)
                .texture("particle", modLoc("block/" + getPath(block)));
    }

    private void simpleBlockWithItem(Block block){
        ModelFile file = cubeAll(block);

        getVariantBuilder(block).partialState()
                .setModels(new ConfiguredModel(file));
        itemModelWithParent(block, file);
    }

    private ItemModelBuilder getItemBuilder(Block block){
        return itemModels().getBuilder(modLoc("item/" + getPath(block)).toString());
    }

}
