package generators;

import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.common.block.IGGenericBlock;
import igteam.immersive_geology.common.block.blocks.IGSlabBlock;
import igteam.immersive_geology.common.block.blocks.IGStairsBlock;
import igteam.immersive_geology.common.item.IGGenericBlockItem;
import igteam.immersive_geology.common.item.IGGenericItem;
import igteam.immersive_geology.common.item.distinct.IGBucketItem;
import igteam.immersive_geology.core.lib.IGLib;
import igteam.api.block.IGBlockType;
import igteam.api.main.IGMultiblockProvider;
import igteam.api.main.IGRegistryProvider;
import igteam.api.materials.helper.MaterialTexture;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class IGItemModelProvider extends ItemModelProvider {

    private Logger log = ImmersiveGeology.getNewLogger();

    public IGItemModelProvider(DataGenerator gen, ExistingFileHelper exHelper){
        super(gen, IGLib.MODID, exHelper);
    }

    @Override
    public String getName(){
        return "Item Models";
    }

    @Override
    protected void registerModels() {

        IGRegistryProvider.IG_ITEM_REGISTRY.values().forEach((i) -> {
            if(i instanceof IGGenericItem) {
                IGGenericItem item = (IGGenericItem) i;
                ItemPattern pattern = item.getPattern();
                if (pattern != ItemPattern.block_item) {
                    generateGenericItem(item);
                }
            }
            if(i instanceof IGBucketItem) {
                IGBucketItem item = (IGBucketItem) i;
                ItemPattern pattern = (ItemPattern) item.getPattern();
                if (pattern == ItemPattern.flask) {
                    generateIGBucketItem(item);
                }
            }
            if(i instanceof IGGenericBlockItem){
                IGGenericBlockItem item = (IGGenericBlockItem) i;
                IGBlockType block = item.getIGBlockType();
                MaterialPattern pattern = block.getPattern();
                if(pattern instanceof BlockPattern) {
                    BlockPattern blockPattern = (BlockPattern) pattern;
                    switch (blockPattern) {
                        case ore:
                            generateOreItemBlock(block, item);
                            break;
                        case slab:
                            generateSlabItemBlock(block, item);
                        case stairs:
                            generateStairsItemBlock(block, item);
                        default:
                            generateGenericItemBlock(block, item);
                            break;
                    }
                }
            }
        });

        generateMultiblockItems();
        //generateManualPageSkeleton();

    }

    private void generateStairsItemBlock(IGBlockType type, IGGenericBlockItem item) {
        if (type.getBlock() instanceof IGStairsBlock) {
            IGStairsBlock stairs = (IGStairsBlock) type.getBlock();
            withExistingParent(new ResourceLocation(IGLib.MODID, "item/" + item.getHolderKey() + "_" + stairs.getPattern().getName()).getPath(),
                    new ResourceLocation(IGLib.MODID, "block/stairs/" + stairs.getPattern().getName() + "_" + stairs.getMaterial(MaterialTexture.base).getName() +
                            (stairs.getMaterial(MaterialTexture.overlay) != null ? "_" + stairs.getMaterial(MaterialTexture.overlay).getName() : "")));
        }
    }

    private void generateSlabItemBlock(IGBlockType type, IGGenericBlockItem item) {
        if(type.getBlock() instanceof IGSlabBlock) {
            IGSlabBlock slab = (IGSlabBlock) type.getBlock();
            withExistingParent(new ResourceLocation(IGLib.MODID, "item/" + item.getHolderKey() + "_" + slab.getPattern().getName()).getPath(),
                    new ResourceLocation(IGLib.MODID, "block/slab/" + slab.getPattern().getName() + "_" + slab.getMaterial(MaterialTexture.base).getName() +
                            (slab.getMaterial(MaterialTexture.overlay) != null ? "_" + slab.getMaterial(MaterialTexture.overlay).getName() : "")));
        }
    }

    private ItemModelBuilder obj(IItemProvider item, String model){
        return getBuilder(item.asItem().getRegistryName().toString())
                .customLoader(OBJLoaderBuilder::begin)
                .modelLocation(modLoc("models/" + model)).flipV(true).end();
    }

    private final Vector3f ZERO = new Vector3f(0, 0, 0);
    private void doTransform(ModelBuilder<?>.TransformsBuilder transform, Perspective type, Vector3f translation, @Nullable Vector3f rotationAngle, float scale){
        if(rotationAngle == null){
            rotationAngle = ZERO;
        }

        transform.transform(type)
                .translation(translation.getX(), translation.getY(), translation.getZ())
                .rotation(rotationAngle.getX(), rotationAngle.getY(), rotationAngle.getZ())
                .scale(scale)
                .end();
    }



    private void generateMultiblockItems(){
        rotarykilnItem();
        chemcialVatItem();
        gravitySeparatorItem();
        revfurnaceItem();
        crystalizerItem();
        hydrojetItem();
    }

    private void generateGenericItem(IGGenericItem item){
        String item_loc = new ResourceLocation(IGLib.MODID, "item/" + item.getHolderKey()).getPath();
        try {
            withExistingParent(item_loc,
                    new ResourceLocation(IGLib.MODID, "item/base/ig_base_item"))
                    .texture("layer0", item.getMaterial(MaterialTexture.base).getTextureLocation(item.getPattern()));
            if (item.getMaterial(MaterialTexture.overlay) != null) {
                log.debug("Attempting to set Texture for: " + item.getPattern().getName() + " " + item.getMaterial(MaterialTexture.overlay));
                getBuilder(item_loc).texture("layer1", item.getMaterial(MaterialTexture.overlay).getTextureLocation(item.getPattern()));
            }
        } catch (Exception ex){
            log.error("Attempted generation of textures for Item: " + item.getRegistryName().getPath() + " received error: " + ex.getMessage());

        }
    }

    private void generateIGBucketItem(IGBucketItem item){
        String item_loc = new ResourceLocation(IGLib.MODID, "item/" + item.getHolderKey()).getPath();
        withExistingParent(item_loc,
                new ResourceLocation(IGLib.MODID, "item/base/ig_base_item"))
                .texture("layer1", item.getMaterial(MaterialTexture.base).getTextureLocation(item.getPattern()));

        if(item.getMaterial(MaterialTexture.overlay) != null) {
            getBuilder(item_loc).texture("layer0", item.getMaterial(MaterialTexture.overlay).getTextureLocation(item.getPattern()));
        }
    }

    private void generateGenericItemBlock(IGBlockType type, IGGenericBlockItem item) {
        if(type.getBlock() instanceof IGGenericBlock) {
            IGGenericBlock block = (IGGenericBlock) type.getBlock();
            withExistingParent(new ResourceLocation(IGLib.MODID, "item/" + item.getHolderKey() + "_" + block.getPattern().getName()).getPath(),
                    new ResourceLocation(IGLib.MODID, "block/"+ block.getPattern().getName() + "/" + block.getPattern().getName() + "_" + block.getMaterial(MaterialTexture.base).getName() +
                            (block.getMaterial(MaterialTexture.overlay) != null ? "_" + block.getMaterial(MaterialTexture.overlay).getName() : "")));
        }
    }

    private void generateOreItemBlock(IGBlockType type, IGGenericBlockItem item) {
        if(type.getBlock() instanceof IGGenericBlock) {
            IGGenericBlock block = (IGGenericBlock) type.getBlock();
            String item_loc = new ResourceLocation(IGLib.MODID, "item/" + item.getHolderKey() + "_" + block.getPattern().getName()).getPath();
            withExistingParent(item_loc, new ResourceLocation(IGLib.MODID, "block/base/" + block.getPattern().getName()));
            getBuilder(item_loc).texture("base", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getPattern()));
            getBuilder(item_loc).texture("ore", block.getMaterial(MaterialTexture.overlay).getTextureLocation(block.getPattern()));
            getBuilder(item_loc).element().allFaces(((direction, faceBuilder) -> faceBuilder.texture("#base").tintindex(0).uvs(0, 0, 16, 16)));
            getBuilder(item_loc).element().allFaces(((direction, faceBuilder) -> faceBuilder.texture("#ore").tintindex(1).uvs(0, 0, 16, 16)));
        }
    }

    protected ResourceLocation forgeLoc(String str){
        return new ResourceLocation("forge", str);
    }

    private String name(IItemProvider item){
        return item.asItem().getRegistryName().getPath();
    }

    public static ResourceLocation rl(String path)
    {
        return new ResourceLocation(IGLib.MODID, path);
    }

    private void rotarykilnItem(){
        ItemModelBuilder model = obj(IGMultiblockProvider.rotarykiln, "multiblock/obj/rotarykiln/rotarykiln.obj")
                .texture("texture", modLoc("multiblock/rotarykiln"));

        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, Perspective.FIRSTPERSON_LEFT, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 0, 0), 0.03125F);
        doTransform(trans, Perspective.FIRSTPERSON_RIGHT, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 0, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_LEFT, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 0, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_RIGHT, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 0, 0), 0.03125F);
        doTransform(trans, Perspective.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
        doTransform(trans, Perspective.GUI, new Vector3f(3.5F, 0, 0), new Vector3f(30, 225, 0), 0.125F);
        doTransform(trans, Perspective.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
        doTransform(trans, Perspective.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
    }


    private void chemcialVatItem(){
        ItemModelBuilder model = obj(IGMultiblockProvider.chemicalvat, "multiblock/obj/chemicalvat/chemicalvat.obj")
                .texture("texture", modLoc("multiblock/chemicalvat_base"));
        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, Perspective.FIRSTPERSON_LEFT, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
        doTransform(trans, Perspective.FIRSTPERSON_RIGHT, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_LEFT, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_RIGHT, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
        doTransform(trans, Perspective.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
        doTransform(trans, Perspective.GUI, new Vector3f(0, -6, 0), new Vector3f(30, 225, 0), 0.1875F);
        doTransform(trans, Perspective.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
        doTransform(trans, Perspective.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
    }

    private void gravitySeparatorItem(){
        ItemModelBuilder model = obj(IGMultiblockProvider.gravityseparator, "multiblock/obj/gravityseparator/gravityseparator.obj")
                .texture("texture", modLoc("multiblock/gravityseparator_base"));
        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, Perspective.FIRSTPERSON_LEFT, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
        doTransform(trans, Perspective.FIRSTPERSON_RIGHT, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_LEFT, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_RIGHT, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
        doTransform(trans, Perspective.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
        doTransform(trans, Perspective.GUI, new Vector3f(0, -6, 0), new Vector3f(30, 225, 0), 0.15F);
        doTransform(trans, Perspective.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
        doTransform(trans, Perspective.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
    }

    private void revfurnaceItem(){
        ItemModelBuilder model = obj(IGMultiblockProvider.reverberation_furnace, "multiblock/obj/revfurnace/reverberation_furnace.obj")
                .texture("texture", modLoc("multiblock/reverberation_furnace"));
        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, Perspective.FIRSTPERSON_LEFT, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
        doTransform(trans, Perspective.FIRSTPERSON_RIGHT, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_LEFT, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_RIGHT, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
        doTransform(trans, Perspective.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
        doTransform(trans, Perspective.GUI, new Vector3f(0, -7, 0), new Vector3f(30, 120, 0), 0.08F);
        doTransform(trans, Perspective.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.1F);
        doTransform(trans, Perspective.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
    }

    private void crystalizerItem() {
        ItemModelBuilder model = obj(IGMultiblockProvider.crystallizer, "multiblock/obj/crystallizer/crystallizer.obj")
                .texture("texture", modLoc("multiblock/crystallizer"));
        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, Perspective.FIRSTPERSON_LEFT, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
        doTransform(trans, Perspective.FIRSTPERSON_RIGHT, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_LEFT, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_RIGHT, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
        doTransform(trans, Perspective.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
        doTransform(trans, Perspective.GUI, new Vector3f(0F, -3F, 0), new Vector3f(30, 225, 0), 0.16F);
        doTransform(trans, Perspective.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
        doTransform(trans, Perspective.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
    }

    private void hydrojetItem() {
        ItemModelBuilder model = obj(IGMultiblockProvider.hydrojet_cutter, "multiblock/obj/hydrojet/hydrojet.obj")
                .texture("texture", modLoc("multiblock/hydrojet"));
        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, Perspective.FIRSTPERSON_LEFT, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
        doTransform(trans, Perspective.FIRSTPERSON_RIGHT, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_LEFT, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
        doTransform(trans, Perspective.THIRDPERSON_RIGHT, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
        doTransform(trans, Perspective.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
        doTransform(trans, Perspective.GUI, new Vector3f(0F, -3F, 0), new Vector3f(30, 225, 0), 0.16F);
        doTransform(trans, Perspective.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
        doTransform(trans, Perspective.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
    }
}
