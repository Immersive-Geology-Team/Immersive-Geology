package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.data.DataGenUtils;
import blusunrize.immersiveengineering.data.models.*;
import blusunrize.immersiveengineering.data.models.NongeneratedModels.NongeneratedModel;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.igteam.immersivegeology.common.blocks.IGGenericBlock;
import com.igteam.immersivegeology.common.blocks.IGStairBlock;
import com.igteam.immersivegeology.common.blocks.helper.IGBlockType;
import com.igteam.immersivegeology.common.blocks.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.common.data.helper.CompatBlockModelProvider;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGMultiblocks;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IGBlockStateProvider extends BlockStateProvider {
    protected static final List<Vec3i> COLUMN_THREE = ImmutableList.of(BlockPos.ZERO, BlockPos.ZERO.above(), BlockPos.ZERO.above(2));

    public final Map<Block, ModelFile> unsplitModels = new HashMap<>();
    protected static final Map<ResourceLocation, String> generatedParticleTextures = new HashMap<>();
    protected final ExistingFileHelper existingFileHelper;
    protected final NongeneratedModels innerModels;
    protected final CompatBlockModelProvider igBlockModels;

    protected Logger logger = IGLib.getNewLogger();

    public IGBlockStateProvider(DataGenerator generator, ExistingFileHelper helper){
        super(generator.getPackOutput(), IGLib.MODID, helper);
        this.existingFileHelper = helper;
        this.innerModels = new NongeneratedModels(generator.getPackOutput(), existingFileHelper);
        this.igBlockModels = new CompatBlockModelProvider(generator.getPackOutput(), existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        List<? extends Block> igBlocks = IGRegistrationHolder.supplyDeferredBlocks().get();
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
    }

    private void crystallizer() {
        IGLib.IG_LOGGER.info("Generating Crystallizer Multiblock Model Data");
        createMultiblock(innerObj("block/multiblock/obj/crystallizer/crystallizer.obj"), IGMultiblocks.CRYSTALLIZER);
    }

    private void createMultiblock(NongeneratedModel unsplitModel, IGTemplateMultiblock multiblock)
    {
        createMultiblock(unsplitModel, multiblock, false);
    }

    private void createDynamicMultiblock(NongeneratedModel unsplitModel, IGTemplateMultiblock multiblock)
    {
        createMultiblock(unsplitModel, multiblock, true);
    }

    private void createMultiblock(NongeneratedModel unsplitModel, IGTemplateMultiblock multiblock, boolean dynamic)
    {
        final ModelFile mainModel = split(unsplitModel, multiblock, false, dynamic);
        if(multiblock.getBlock().getStateDefinition().getProperties().contains(IEProperties.MIRRORED))
            createMultiblock(
                    multiblock::getBlock,
                    mainModel,
                    split(mirror(unsplitModel, innerModels), multiblock, true, dynamic),
                    IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED
            );
        else
            createMultiblock(multiblock::getBlock, mainModel, null, IEProperties.FACING_HORIZONTAL, null);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel)
    {
        createMultiblock(b, masterModel, null, IEProperties.FACING_HORIZONTAL, null);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel,
                                  @Nullable Property<Boolean> mirroredState)
    {
        createMultiblock(b, masterModel, mirroredModel, IEProperties.FACING_HORIZONTAL, mirroredState);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel,
                                  EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState)
    {
        unsplitModels.put(b.get(), masterModel);
        Preconditions.checkArgument((mirroredModel==null)==(mirroredState==null));
        VariantBlockStateBuilder builder = getVariantBuilder(b.get());
        boolean[] possibleMirrorStates;
        if(mirroredState!=null)
            possibleMirrorStates = new boolean[]{false, true};
        else
            possibleMirrorStates = new boolean[1];
        for(boolean mirrored : possibleMirrorStates)
            for(Direction dir : facing.getPossibleValues())
            {
                final int angleY;
                final int angleX;
                if(facing.getPossibleValues().contains(Direction.UP))
                {
                    angleX = -90*dir.getStepY();
                    if(dir.getAxis()!= Direction.Axis.Y)
                        angleY = getAngle(dir, 180);
                    else
                        angleY = 0;
                }
                else
                {
                    angleY = getAngle(dir, 180);
                    angleX = 0;
                }
                ModelFile model = mirrored?mirroredModel: masterModel;
                PartialBlockstate partialState = builder.partialState()
                        .with(facing, dir);
                if(mirroredState!=null)
                    partialState = partialState.with(mirroredState, mirrored);
                partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
            }
    }

    private void registerGenericBlock(IGBlockType type, IFlagType<?> pattern){
        IGGenericBlock block = (IGGenericBlock) type;

        logger.info("Attempting Check for Texture Location: " + "["+ pattern.getName() + " | " + type.getMaterial(MaterialTexture.base).getName() + "] " + block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag()).getPath().toLowerCase());

        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(models().withExistingParent(
                                new ResourceLocation(IGLib.MODID, "block/" + pattern.toString().toLowerCase() + "/" + pattern.getRegistryKey(block.getMaterial(MaterialTexture.base))).getPath(),
                                new ResourceLocation(IGLib.MODID, "block/base/block"))
                        .texture("all", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag()))
                        .texture("particle", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag())))
                .build());
    }

    private void registerOreBlock(IGBlockType type){
        IGGenericBlock block = (IGGenericBlock) type;
        IFlagType<?> pattern = block.getFlag();

        logger.info("Attempting Check for Texture Location: " + "["+ pattern.getName() + " | " + type.getMaterial(MaterialTexture.base).getName() + "] " + block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag()).getPath().toLowerCase());

        BlockModelBuilder baseModel = models().withExistingParent(
                new ResourceLocation(IGLib.MODID, "block/ore_block/" + block.getFlag().getName() + "_" + block.getMaterial(MaterialTexture.overlay).getName() + "_" + block.getMaterial(MaterialTexture.base).getName()).getPath(),
                new ResourceLocation(IGLib.MODID, "block/base/" + block.getFlag().getName()));
        try {
            baseModel.texture("ore", block.getMaterial(MaterialTexture.overlay).getTextureLocation(block.getFlag())).texture("base", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag()));
        } catch(IllegalArgumentException error) {
            // Extended the normal Block Model Builder to include an unsafe method to add unknown texture locations.
            // NOTE: only needed as a data gen implementation if some mods are unavailable in the data generation, which would prevent the safe method from running.
            logger.error("Error: " + error.getMessage());
            //baseModel.unsafeTexture("base", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getFlag()).toString());
        }

        VariantBlockStateBuilder builder = getVariantBuilder(block.getBlock()).forAllStates(blockState -> ConfiguredModel.builder().modelFile(baseModel).build());
        //IGLib.IG_LOGGER.info(builder.toJson().toString());
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


    private ModelFile split(NongeneratedModel loc, IGTemplateMultiblock mb)
    {
        return split(loc, mb, false);
    }

    private ModelFile split(NongeneratedModel loc, IGTemplateMultiblock mb, boolean mirror)
    {
        return split(loc, mb, mirror, false);
    }

    private ModelFile split(NongeneratedModel loc, IGTemplateMultiblock mb, boolean mirror, boolean dynamic)
    {
        UnaryOperator<BlockPos> transform = UnaryOperator.identity();
        if(mirror)
        {
            loadTemplateFor(mb);
            Vec3i size = mb.getSize(null);
            transform = p -> new BlockPos(size.getX()-p.getX()-1, p.getY(), p.getZ());
        }
        return split(loc, mb, transform, dynamic);
    }

    private ModelFile split(
            NongeneratedModel name, IGTemplateMultiblock multiblock, UnaryOperator<BlockPos> transform, boolean dynamic
    )
    {
        loadTemplateFor(multiblock);
        final Vec3i offset = multiblock.getMasterFromOriginOffset();
        Stream<Vec3i> partsStream = multiblock.getTemplate(null).blocksWithoutAir()
                .stream()
                .map(info -> info.pos())
                .map(transform)
                .map(p -> p.subtract(offset));
        return split(name, partsStream.collect(Collectors.toList()), dynamic);
    }

    private void loadTemplateFor(IGTemplateMultiblock multiblock)
    {
        final ResourceLocation name = multiblock.getUniqueName();
        if(IGTemplateMultiblock.SYNCED_CLIENT_TEMPLATES.containsKey(name))
            return;
        final String filePath = "structures/"+name.getPath()+".nbt";
        int slash = filePath.indexOf('/');
        String prefix = filePath.substring(0, slash);
        ResourceLocation shortLoc = new ResourceLocation(
                name.getNamespace(),
                filePath.substring(slash+1)
        );
        try
        {
            final Resource resource = existingFileHelper.getResource(shortLoc, PackType.SERVER_DATA, "", prefix);
            try(final InputStream input = resource.open())
            {
                final CompoundTag nbt = NbtIo.readCompressed(input, NbtAccounter.unlimitedHeap());
                final StructureTemplate template = new StructureTemplate();
                template.load(BuiltInRegistries.BLOCK.asLookup(), nbt);
                IGTemplateMultiblock.SYNCED_CLIENT_TEMPLATES.put(name, template);
            }
        } catch(IOException e)
        {
            throw new RuntimeException("Failed on "+name, e);
        }
    }

    protected NongeneratedModel innerObj(String loc, @Nullable RenderType layer)
    {
        Preconditions.checkArgument(loc.endsWith(".obj"));
        final var result = obj(loc.substring(0, loc.length()-4), modLoc(loc), innerModels);
        setRenderType(layer, result);
        return result;
    }

    protected NongeneratedModel innerObj(String loc)
    {
        return innerObj(loc, null);
    }

    protected BlockModelBuilder obj(String loc)
    {
        return obj(loc, (RenderType)null);
    }

    protected BlockModelBuilder obj(String loc, @Nullable RenderType layer)
    {
        final var model = obj(loc, models());
        setRenderType(layer, model);
        return model;
    }

    protected <T extends ModelBuilder<T>>
    T obj(String loc, ModelProvider<T> modelProvider)
    {
        Preconditions.checkArgument(loc.endsWith(".obj"));
        return obj(loc.substring(0, loc.length()-4), modLoc(loc), modelProvider);
    }

    protected <T extends ModelBuilder<T>>
    T obj(String name, ResourceLocation model, ModelProvider<T> provider)
    {
        return obj(name, model, ImmutableMap.of(), provider);
    }

    protected <T extends ModelBuilder<T>>
    T obj(String name, ResourceLocation model, Map<String, ResourceLocation> textures, ModelProvider<T> provider)
    {
        return obj(provider.withExistingParent(name, mcLoc("block")), model, textures);
    }

    protected <T extends ModelBuilder<T>>
    T obj(T base, ResourceLocation model, Map<String, ResourceLocation> textures)
    {
        assertModelExists(model);
        T ret = base
                .customLoader(ObjModelBuilder::begin)
                .automaticCulling(false)
                .modelLocation(addModelsPrefix(model))
                .flipV(true)
                .end();
        String particleTex = DataGenUtils.getTextureFromObj(model, existingFileHelper);
        if(particleTex.charAt(0)=='#')
            particleTex = textures.get(particleTex.substring(1)).toString();
        ret.texture("particle", particleTex);
        generatedParticleTextures.put(ret.getLocation(), particleTex);
        for(Entry<String, ResourceLocation> e : textures.entrySet())
            ret.texture(e.getKey(), e.getValue());
        return ret;
    }

    protected BlockModelBuilder splitModel(String name, NongeneratedModel model, List<Vec3i> parts, boolean dynamic)
    {
        BlockModelBuilder result = models().withExistingParent(name, mcLoc("block"))
                .customLoader(SplitModelBuilder::begin)
                .innerModel(model)
                .parts(parts)
                .dynamic(dynamic)
                .end();
        addParticleTextureFrom(result, model);
        return result;
    }

    protected ModelFile split(NongeneratedModel baseModel, List<Vec3i> parts, boolean dynamic)
    {
        return splitModel(baseModel.getLocation().getPath()+"_split", baseModel, parts, dynamic);
    }

    protected ModelFile split(NongeneratedModel baseModel, List<Vec3i> parts)
    {
        return split(baseModel, parts, false);
    }

    protected ModelFile splitDynamic(NongeneratedModel baseModel, List<Vec3i> parts)
    {
        return split(baseModel, parts, true);
    }

    protected void addParticleTextureFrom(BlockModelBuilder result, ModelFile model)
    {
        String particles = generatedParticleTextures.get(model.getLocation());
        if(particles!=null)
        {
            result.texture("particle", particles);
            generatedParticleTextures.put(result.getLocation(), particles);
        }
    }

    protected ConfiguredModel emptyWithParticles(String name, String particleTexture)
    {
        ModelFile model = models().withExistingParent(name, modLoc("block/ie_empty"))
                .texture("particle", particleTexture);
        generatedParticleTextures.put(modLoc(name), particleTexture);
        return new ConfiguredModel(model);
    }

    public void assertModelExists(ResourceLocation name)
    {
        String suffix = name.getPath().contains(".")?"": ".json";
        Preconditions.checkState(
                existingFileHelper.exists(name, PackType.CLIENT_RESOURCES, suffix, "models"),
                "Model \""+name+"\" does not exist");
    }

    protected IEOBJBuilder<BlockModelBuilder> ieObjBuilder(String loc)
    {
        return ieObjBuilder(getAutoNameIEOBJ(loc), modLoc(loc));
    }

    protected IEOBJBuilder<BlockModelBuilder> ieObjBuilder(String name, ResourceLocation model)
    {
        return ieObjBuilder(name, model, models());
    }

    protected <T extends ModelBuilder<T>>
    IEOBJBuilder<T> ieObjBuilder(String loc, ModelProvider<T> modelProvider)
    {
        return ieObjBuilder(getAutoNameIEOBJ(loc), modLoc(loc), modelProvider);
    }

    private static String getAutoNameIEOBJ(String loc)
    {
        Preconditions.checkArgument(loc.endsWith(".obj.ie"));
        return loc.substring(0, loc.length()-7);
    }

    protected CompatBlockModelProvider compatmodels()
    {
        return this.igBlockModels;
    }

    protected <T extends ModelBuilder<T>>
    IEOBJBuilder<T> ieObjBuilder(String name, ResourceLocation model, ModelProvider<T> modelProvider)
    {
        final String particle = DataGenUtils.getTextureFromObj(model, existingFileHelper);
        generatedParticleTextures.put(modLoc(name), particle);
        return modelProvider.withExistingParent(name, mcLoc("block"))
                .texture("particle", particle)
                .customLoader(IEOBJBuilder::begin)
                .modelLocation(addModelsPrefix(model));
    }

    protected <T extends ModelBuilder<T>> T mirror(NongeneratedModel inner, ModelProvider<T> provider)
    {
        return provider.getBuilder(inner.getLocation().getPath()+"_mirrored")
                .customLoader(MirroredModelBuilder::begin)
                .inner(inner)
                .end();
    }

    protected int getAngle(Direction dir, int offset)
    {
        return (int)((dir.toYRot()+offset)%360);
    }

    protected void createHorizontalRotatedBlock(Supplier<? extends Block> block, ModelFile model)
    {
        createHorizontalRotatedBlock(block, $ -> model, List.of());
    }

    protected void createHorizontalRotatedBlock(Supplier<? extends Block> block, ModelFile model, int offsetRotY) {
        createRotatedBlock(block, $ -> model, IEProperties.FACING_HORIZONTAL, List.of(), 0, offsetRotY);
    }

    protected void createHorizontalRotatedBlock(Supplier<? extends Block> block, Function<PartialBlockstate, ModelFile> model, List<Property<?>> additionalProps)
    {
        createRotatedBlock(block, model, IEProperties.FACING_HORIZONTAL, additionalProps, 0, 180);
    }

    protected void createAllRotatedBlock(Supplier<? extends Block> block, ModelFile model)
    {
        createAllRotatedBlock(block, $ -> model, List.of());
    }

    protected void createAllRotatedBlock(Supplier<? extends Block> block, Function<PartialBlockstate, ModelFile> model, List<Property<?>> additionalProps)
    {
        createRotatedBlock(block, model, IEProperties.FACING_ALL, additionalProps, 90, 0);
    }

    protected void createRotatedBlock(Supplier<? extends Block> block, ModelFile model, Property<Direction> facing,
                                      List<Property<?>> additionalProps, int offsetRotX, int offsetRotY)
    {
        createRotatedBlock(block, $ -> model, facing, additionalProps, offsetRotX, offsetRotY);
    }

    protected void createRotatedBlock(Supplier<? extends Block> block, Function<PartialBlockstate, ModelFile> model, Property<Direction> facing,
                                      List<Property<?>> additionalProps, int offsetRotX, int offsetRotY)
    {
        VariantBlockStateBuilder stateBuilder = getVariantBuilder(block.get());
        forEachState(stateBuilder.partialState(), additionalProps, state -> {
            ModelFile modelLoc = model.apply(state);
            for(Direction d : facing.getPossibleValues())
            {
                int x;
                int y;
                switch(d)
                {
                    case UP -> {
                        x = 90;
                        y = 0;
                    }
                    case DOWN -> {
                        x = -90;
                        y = 0;
                    }
                    default -> {
                        y = getAngle(d, offsetRotY);
                        x = 0;
                    }
                }
                state.with(facing, d).setModels(new ConfiguredModel(modelLoc, x+offsetRotX, y, false));
            }
        });
    }

    protected static String getName(RenderStateShard state)
    {
        //TODO clean up/speed up
        try
        {
            // Datagen should only ever run in a deobf environment, so no need to use unreadable SRG names here
            // This is a workaround for the fact that client-side Mixins are not applied in datagen
            Field f = RenderStateShard.class.getDeclaredField("name");
            f.setAccessible(true);
            return (String)f.get(state);
        } catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Comparable<T>> void forEach(PartialBlockstate base, Property<T> prop,
                                                         List<Property<?>> remaining, Consumer<PartialBlockstate> out)
    {
        for(T value : prop.getPossibleValues())
            forEachState(base, remaining, map -> {
                map = map.with(prop, value);
                out.accept(map);
            });
    }

    public static void forEachState(PartialBlockstate base, List<Property<?>> props, Consumer<PartialBlockstate> out)
    {
        if(props.size() > 0)
        {
            List<Property<?>> remaining = props.subList(1, props.size());
            Property<?> main = props.get(0);
            forEach(base, main, remaining, out);
        }
        else
            out.accept(base);
    }

    protected ResourceLocation addModelsPrefix(ResourceLocation in)
    {
        return new ResourceLocation(in.getNamespace(), "models/"+in.getPath());
    }

    protected void setRenderType(@Nullable RenderType type, ModelBuilder<?>... builders)
    {
        if(type!=null)
        {
            final String typeName = ModelProviderUtils.getName(type);
            for(final ModelBuilder<?> model : builders)
                model.renderType(typeName);
        }
    }
}
