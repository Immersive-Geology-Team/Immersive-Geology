package com.igteam.immersivegeology.common.data.generators;

import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.IGScaffoldingBlock;
import com.igteam.immersivegeology.common.block.IGWeatheringOreBlock;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import com.igteam.immersivegeology.common.item.IGGenericBucketItem;
import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.common.item.IGGenericOreItem;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

import static com.igteam.immersivegeology.core.material.GeologyMaterial.EXISTING_HELPER;
import static net.minecraft.server.packs.PackType.CLIENT_RESOURCES;

public class IGItemModelProvider extends ItemModelProvider {


    private static ExistingFileHelper HELPER;
    private final Logger logger = IGLib.getNewLogger();
    public IGItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), IGLib.MODID, existingFileHelper);
        HELPER = existingFileHelper;
    }

    @Override
    protected void registerModels() {

        IGLib.IG_LOGGER.info("-===== Starting Registration of Immersive Geology Simple Item Models =====-");
        List<? extends Item> itemList = IGRegistrationHolder.supplyDeferredItems().get();

        for (Item item : itemList) {
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
            if(item instanceof IGGenericItem i){
                generateGenericItem(i);
            }
        }
        IGLib.IG_LOGGER.info("-===== Finished Registration of Immersive Geology Simple Item Models =====-");
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
            logger.error("Attempted to generate a texture for the item type '{}' with material '{}'", item.getFlag().getName(), item.getMaterial(MaterialTexture.base).getName());
            logger.error(ex.getMessage());
        }
    }

    private void generateGenericOreItem(IGFlagItem item)
    {
        String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.base))).getPath();

        try {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_base_item");
            ItemModelBuilder builder = withExistingParent(itemLocation, parentLocation);
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
                    ItemModelBuilder builder = withExistingParent(itemLocation, parentLocation);
                    IGBlockStateProvider.implementUnsafeOreTexture(builder, igOreBlock, igOreBlock.getStoneFormation(), 1);
                    return;
                }

                if(blockItem.getBlock() instanceof IGScaffoldingBlock scaffoldingBlock){
                    String itemLocation = new ResourceLocation(IGLib.MODID, "item/scaffolding_" + scaffoldingBlock.getMaterial(MaterialTexture.base).getName().toLowerCase() + "_" + scaffoldingBlock.getScaffoldingType().name().toLowerCase()).getPath();
                    ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "block/scaffolding/"+scaffoldingBlock.getMaterial(MaterialTexture.base).getName().toLowerCase()+"_scaffolding_"+scaffoldingBlock.getScaffoldingType().name().toLowerCase());

                    withExistingParent(itemLocation, parentLocation);
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
            logger.error("Wrong input parse in generateGenericBlockItem, used normal item as input see: {} and {}", item.getFlag().getName(), item.getMaterial(MaterialTexture.base).getName());
            logger.error("Error {}", err.getMessage());
        }

    }
}
