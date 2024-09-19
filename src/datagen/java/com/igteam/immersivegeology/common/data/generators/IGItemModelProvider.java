package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.data.models.IEOBJBuilder;
import blusunrize.immersiveengineering.data.models.TRSRModelBuilder;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import com.igteam.immersivegeology.common.item.IGGenericBucketItem;
import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.common.item.IGGenericOreItem;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class IGItemModelProvider extends ItemModelProvider {


    private final Logger logger = IGLib.getNewLogger();
    public IGItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), IGLib.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
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

            // TODO implement a better version of this, that supports 'colored' variants of the item textures, using the item flag system
            String flag_name = item.getFlag().getName();
            flag_name = flag_name.substring(0, flag_name.indexOf("_"));
            withExistingParent(itemLocation, parentLocation).texture("layer0", new ResourceLocation(IGLib.MODID, "item/greyscale/rock/" + flag_name + "_rock"));
            getBuilder(itemLocation).texture("layer1", new ResourceLocation(IGLib.MODID, "item/greyscale/rock/" +item.getFlag().getName()));
        } catch (Exception ex) {
            logger.error("Attempted to generate a texture for the item type '{}' with material '{}'", item.getFlag().getName(), item.getMaterial(MaterialTexture.base).getName());
            logger.error(ex.getMessage());
        }
    }

    private void generateGenericBucketItem(IGFlagItem item){
        if(!(item instanceof IGGenericBucketItem bucketItem)) return;
        MaterialInterface<?> baseMaterial = bucketItem.getMaterial(MaterialTexture.base);
        MaterialInterface<?> overlayMaterial = bucketItem.getMaterial(MaterialTexture.overlay);
        String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + ItemCategoryFlags.BUCKET.getRegistryKey(baseMaterial, bucketItem.getFluidCategory())).getPath();
        boolean chemical = false;

        if(baseMaterial instanceof ChemicalEnum)
        {
           chemical = true;
           if(overlayMaterial != null)
           {
               itemLocation = new ResourceLocation(IGLib.MODID, "item/" + ItemCategoryFlags.BUCKET.getRegistryKey(baseMaterial,overlayMaterial)).getPath();
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
                if(blockItem.getBlock() instanceof IGOreBlock igOreBlock){
                    OreRichness richness = igOreBlock.getOreRichness();
                    String itemLocation = new ResourceLocation(IGLib.MODID, "item/"+item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.overlay), item.getMaterial(MaterialTexture.base), richness)).getPath();
                    ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "block/ore_block/"+ igOreBlock.getOreRichness().name().toLowerCase() + "/"+igOreBlock.getFlag().getRegistryKey(igOreBlock.getMaterial(MaterialTexture.overlay), igOreBlock.getMaterial(MaterialTexture.base), richness));

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
