package com.igteam.immersivegeology.common.data.generators;

import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.block.ore.IGCrystalBlock;
import com.igteam.immersivegeology.common.block.structural.IGFenceBlock;
import com.igteam.immersivegeology.common.block.structural.IGScaffoldingBlock;
import com.igteam.immersivegeology.common.data.TRSRModelBuilder;
import com.igteam.immersivegeology.common.item.*;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

import static com.igteam.immersivegeology.core.material.GeologyMaterial.EXISTING_HELPER;
import static net.minecraft.server.packs.PackType.CLIENT_RESOURCES;

public class IGItemModelProvider extends IGTRSRItemModelProvider
{
    private static ExistingFileHelper HELPER;
    private final Logger logger = IGLib.getNewLogger();
    public IGItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), existingFileHelper);
        HELPER = existingFileHelper;
    }

    @Override
    protected void registerModels() {

        IGLib.IG_LOGGER.info("-===== Starting Registration of Immersive Geology Simple Item Models =====-");
        List<? extends Item> itemList = IGRegistrationHolder.supplyDeferredItems().get();

        for (Item item : itemList) {
            if(item instanceof IGMultiblockSkinItem<?> skin)
            {
                generateGenericSkinItem(skin);
                continue;
            }
            if(item instanceof IGGenericOreItem i){
                generateGenericOreItem(i);
                continue;
            }
            if(item instanceof IGGenericBucketItem i){
                generateGenericBucketItem(i);
                continue;
            }
            if(item instanceof IGGenericBlockItem i){
                generateGenericBlockItem(i);
                continue;
            }
            if(item instanceof IGGenericDrillHead i)
            {
                generateDrillHead(i);
                continue;
            }
            if(item instanceof IGFlagItem i)
            {
                if(i instanceof IGMineralTestingItem||i instanceof IGMBFormationItem)
                {
                    generateToolItem(i);
                    continue;
                }
            }
            if(item instanceof IGGenericItem i){
                generateGenericItem(i);
            }
        }
        IGLib.IG_LOGGER.info("-===== Finished Registration of Immersive Geology Simple Item Models =====-");


        this.obj(MiscEnum.Cable.getBlock(BlockCategoryFlags.ENERGY_PIPE), IGLib.rl("block/obj/energy_cable_centre.obj")).transforms(IGLib.rl("item/block"));
    }

    private void generateDrillHead(IGGenericDrillHead item)
    {
        String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.base))).getPath();
        ResourceLocation coloredTexture = new ResourceLocation(IGLib.MODID, "item/colored/" + item.getMaterial(MaterialTexture.base).getName() + "/" + item.getFlag().name().toLowerCase());
        ResourceLocation texture = new ResourceLocation(IGLib.MODID, "palette/item/drill_head/drill_head_pristine_"+item.getMaterial(MaterialTexture.base).getName().toLowerCase());
        boolean colored = this.existingFileHelper.exists(new ResourceLocation(IGLib.MODID, "textures/" + coloredTexture.getPath()).withSuffix(".png"), PackType.CLIENT_RESOURCES);
        try {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_base_item");
            withExistingParent(itemLocation, parentLocation).texture("layer0", colored ? coloredTexture : texture);
        } catch (Exception ex) {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_base_item");
            withExistingParent(itemLocation, parentLocation).textures.put("layer0", texture.toString());
        }
    }

    private TRSRModelBuilder obj(ItemLike item, ResourceLocation model) {
        Preconditions.checkArgument(this.existingFileHelper.exists(model, PackType.CLIENT_RESOURCES, "", "models"));
        return (TRSRModelBuilder)((ObjModelBuilder)this.getBuilder(item).customLoader(ObjModelBuilder::begin)).flipV(true).modelLocation(new ResourceLocation(model.getNamespace(), "models/" + model.getPath())).end();
    }

    private TRSRModelBuilder getBuilder(ItemLike item) {
        return (TRSRModelBuilder)this.getBuilder(this.name(item));
    }

    private String name(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    private void generateGenericItem(IGFlagItem item){
        String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.base))).getPath();

        try {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_base_item");
            withExistingParent(itemLocation, parentLocation).texture("layer0", item.getMaterial(MaterialTexture.base).getTextureLocation(item.getFlag()));

            if(item.getMaterial(MaterialTexture.overlay) != null) {
                getBuilder(itemLocation).texture("layer1", item.getMaterial(MaterialTexture.overlay).getTextureLocation(item.getFlag()));
            }
        } catch (Exception ex) {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_base_item");
            withExistingParent(itemLocation, parentLocation).textures.put("layer0", item.getMaterial(MaterialTexture.base).getTextureLocation(item.getFlag()).toString());

            if(item.getMaterial(MaterialTexture.overlay) != null) {
                getBuilder(itemLocation).textures.put("layer1", item.getMaterial(MaterialTexture.overlay).getTextureLocation(item.getFlag()).toString());
            }
        }
    }

    private void generateGenericSkinItem(IGMultiblockSkinItem<?> item){
        String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + item.getRegistryName()).getPath();

        try {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_base_item");
            withExistingParent(itemLocation, parentLocation).texture("layer0", item.getMaterial(MaterialTexture.base).getTextureLocation(item.getFlag()));

            if(item.getMaterial(MaterialTexture.overlay) != null) {
                getBuilder(itemLocation).texture("layer1", item.getMaterial(MaterialTexture.overlay).getTextureLocation(item.getFlag()));
            }
        } catch (Exception ex) {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_base_item");
            withExistingParent(itemLocation, parentLocation).textures.put("layer0", item.getMaterial(MaterialTexture.base).getTextureLocation(item.getFlag()).toString());

            if(item.getMaterial(MaterialTexture.overlay) != null) {
                getBuilder(itemLocation).textures.put("layer1", item.getMaterial(MaterialTexture.overlay).getTextureLocation(item.getFlag()).toString());
            }
        }
    }

    private void generateToolItem(IGFlagItem item){
        String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.base))).getPath();

        try {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_tool_item");
            withExistingParent(itemLocation, parentLocation).texture("layer0", item.getMaterial(MaterialTexture.base).getTextureLocation(item.getFlag()));

            if(item.getMaterial(MaterialTexture.overlay) != null) {
                getBuilder(itemLocation).texture("layer1", item.getMaterial(MaterialTexture.overlay).getTextureLocation(item.getFlag()));
            }
        } catch (Exception ex) {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_tool_item");
            withExistingParent(itemLocation, parentLocation).textures.put("layer0", item.getMaterial(MaterialTexture.base).getTextureLocation(item.getFlag()).toString());

            if(item.getMaterial(MaterialTexture.overlay) != null) {
                getBuilder(itemLocation).textures.put("layer1", item.getMaterial(MaterialTexture.overlay).getTextureLocation(item.getFlag()).toString());
            }
        }
    }

    private void generateGenericOreItem(IGFlagItem item)
    {
        String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.base))).getPath();

        try {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_base_item");
            TRSRModelBuilder builder = withExistingParent(itemLocation, parentLocation);
            setItemTexture(builder, (IGGenericOreItem) item);
        } catch (Exception ex) {
            logger.error("Attempted to generate a texture for the ore item type '{}' with material '{}'", item.getFlag().getName(), item.getMaterial(MaterialTexture.base).getName());
            logger.error(ex.getMessage());
        }
    }

    private void setItemTexture(ModelBuilder<?> model, IGGenericOreItem item)
    {
        ResourceLocation coloredTexture = new ResourceLocation(IGLib.MODID, "item/colored/raw_ore/" + item.getMaterial(MaterialTexture.base).getName().toLowerCase() + "/" + item.getOreRichness().getSanitizedName());
        //IGLib.IG_LOGGER.info("Testing: {}", coloredTexture);
        if(EXISTING_HELPER.exists(new ResourceLocation(IGLib.MODID, "textures/" + coloredTexture.getPath() + ".png"), CLIENT_RESOURCES))
        {
            model.texture("layer0", coloredTexture);
            return;
        }
        model.texture("layer0", new ResourceLocation(IGLib.MODID, "item/greyscale/raw_ore/" + item.getOreRichness().getSanitizedName()));
    }

    private void generateGenericBucketItem(IGFlagItem item){
        if(!(item instanceof IGGenericBucketItem bucketItem)) return;
        MaterialInterface<?> baseMaterial = bucketItem.getMaterial(MaterialTexture.base);
        MaterialInterface<?> overlayMaterial = bucketItem.getMaterial(MaterialTexture.overlay);
        String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + bucketItem.getBucketType().getRegistryKey(baseMaterial, bucketItem.getFluidCategory())).getPath();
        boolean chemical = false;

        if(baseMaterial instanceof ChemicalEnum)
        {
           chemical = true;
           if(overlayMaterial != null)
           {
               itemLocation = new ResourceLocation(IGLib.MODID, "item/" + bucketItem.getBucketType().getRegistryKey(baseMaterial,overlayMaterial)).getPath();
           }
        }

        try {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_base_item");

            // TODO implement a better version of this, that supports 'colored' variants of the item textures, using the item flag system
            withExistingParent(itemLocation, parentLocation).texture("layer0", new ResourceLocation(IGLib.MODID, "item/greyscale/fluid/" + (chemical ? "compound_flask" : "bucket_base")));
            getBuilder(itemLocation).texture("layer1", new ResourceLocation(IGLib.MODID, "item/greyscale/fluid/" + (chemical ? "compound_flask_fluid" : "bucket_fluid")));
        } catch (Exception ex) {
            logger.error("Attempted to generate a texture for the item type '{}' with material '{}'", item.getFlag().getName(), item.getMaterial(MaterialTexture.base).getName());
            logger.error(ex.getMessage());
        }
    }

    private void generateGenericBlockItem(IGFlagItem item){
        try
        {
            if(item instanceof IGGenericBlockItem blockItem)
            {
                if(blockItem.cancelDatagen()) return;
                if(blockItem.getBlock() instanceof IOreBlock igOreBlock){
                    String prefix = "minecraft";
                    Set<IFlagType<?>> flags = igOreBlock.getMaterial(MaterialTexture.base).getFlags();
                    for(ModFlags mod : ModFlags.values())
                    {
                        if(flags.contains(mod))
                        {
                            prefix = mod.name().toLowerCase();
                        }
                    }

                    boolean isSedimentary = ((MaterialStone)igOreBlock.getMaterial(MaterialTexture.base).instance()).getStoneFormation().equals(StoneFormation.SEDIMENTARY) || igOreBlock.getMaterial(MaterialTexture.base).useSedimentaryTextures();
                    OreRichness richness = igOreBlock.getOreRichness();
                    String itemLocation = new ResourceLocation(IGLib.MODID, "item/"+item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.overlay), item.getMaterial(MaterialTexture.base), richness)).getPath();
                    ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "block/base/ore_block" + (isSedimentary ? "_sedimentary" : ""));
                    TRSRModelBuilder builder = withExistingParent(itemLocation, parentLocation);
                    IGBlockStateProvider.implementUnsafeOreTexture(builder, igOreBlock, igOreBlock.getStoneFormation(), 1);
                    return;
                }

                if(blockItem.getBlock() instanceof IGScaffoldingBlock scaffoldingBlock){
                    String itemLocation = new ResourceLocation(IGLib.MODID, "item/scaffolding_" + scaffoldingBlock.getMaterial(MaterialTexture.base).getName().toLowerCase() + "_" + scaffoldingBlock.getScaffoldingType().name().toLowerCase()).getPath();
                    ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "block/scaffolding/"+scaffoldingBlock.getMaterial(MaterialTexture.base).getName().toLowerCase()+"_scaffolding_"+scaffoldingBlock.getScaffoldingType().name().toLowerCase());

                    withExistingParent(itemLocation, parentLocation);
                    return;
                }

                if(blockItem.getBlock() instanceof IGCrystalBlock crystalBlock)
                {
                    boolean complexItem = blockItem.getMaterials().size() > 1;
                    String itemLocation = new ResourceLocation(IGLib.MODID, "item/"+(complexItem?item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.overlay), item.getMaterial(MaterialTexture.base)): item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.base)))).getPath();
                    ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "block/evaporate_crystal/"+item.getMaterial(MaterialTexture.base).getName()+"_stage_2");

                    withExistingParent(itemLocation, parentLocation);
                    return;
                }


                if(blockItem.getBlock() instanceof IGFenceBlock fence)
                {
                    withExistingParent(BuiltInRegistries.BLOCK.getKey(fence).getPath(), new ResourceLocation(IGLib.MODID,"block/base/fence_inventory"))
                                    .texture("texture", fence.getMaterial(MaterialTexture.base).getTextureLocation(BlockCategoryFlags.STORAGE_BLOCK));
                    return;
                }


                boolean complexItem = blockItem.getMaterials().size() > 1;

                String itemLocation = new ResourceLocation(IGLib.MODID, "item/"+(complexItem?item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.overlay), item.getMaterial(MaterialTexture.base)): item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.base)))).getPath();
                String overlayExtra = item.getMaterial(MaterialTexture.overlay)!=null?"_"+item.getMaterial(MaterialTexture.overlay).getName(): "";
                ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "block/"+item.getFlag().getName()+"/"+item.getFlag().getName()+overlayExtra+"_"+item.getMaterial(MaterialTexture.base).getName());

                withExistingParent(itemLocation, parentLocation);
                return;
            }
        } catch(Exception err)
        {
            if(item.getFlag() != null && item.getMaterial(MaterialTexture.base) != null) logger.error("Wrong input parse in generateGenericBlockItem, used normal item as input see: {} and {}", item.getFlag().getName(), item.getMaterial(MaterialTexture.base).getName());
            logger.error("Error {}", err.getMessage());
        }
    }

    @Override
    public String getName()
    {
        return "Item models";
    }
}
