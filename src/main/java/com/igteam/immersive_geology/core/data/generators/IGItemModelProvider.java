package com.igteam.immersive_geology.core.data.generators;

import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.item.IGBlockItem;
import com.igteam.immersive_geology.common.item.IGOreItem;
import com.igteam.immersive_geology.common.item.ItemBase;
import com.igteam.immersive_geology.core.data.IGDataProvider;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.loaders.DynamicBucketModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class IGItemModelProvider extends ItemModelProvider {

    public IGItemModelProvider(DataGenerator gen, ExistingFileHelper exHelper){
        super(gen, IGLib.MODID, exHelper);
    }

    @Override
    public String getName(){
        return "Item Models";
    }

    @Override
    protected void registerModels() {

        for(Item item : IGRegistrationHolder.registeredIGItems.values()){
            try{
                if(item instanceof IGBlockItem){
                    IGBlockItem blockItem = (IGBlockItem) item;
                    if(blockItem.getBlock() instanceof IGOreBlock) {
                        IGOreBlock oreBlock = (IGOreBlock) blockItem.getBlock();
                        String builder_name = new ResourceLocation(IGLib.MODID, "item/"+oreBlock.getHolderName()).getPath();
                        String stone_name = oreBlock.getMaterial(BlockMaterialType.BASE_MATERIAL).getStoneType().getName().toLowerCase();
                        withExistingParent(builder_name, new ResourceLocation(IGLib.MODID, "block/base/rock_" + stone_name));
                        getBuilder(builder_name).texture("ore", IGLib.MODID + ":block/greyscale/rock/ore_bearing/" + stone_name + "/" + stone_name + "_normal");
                        getBuilder(builder_name).texture("base", IGLib.MODID + ":block/greyscale/rock/rock_" + stone_name);
                        getBuilder(builder_name).element().allFaces(((direction, faceBuilder) -> faceBuilder.texture("#base").tintindex(1).uvs(0, 0, 16, 16)));
                        getBuilder(builder_name).element().allFaces(((direction, faceBuilder) -> faceBuilder.texture("#ore").tintindex(0).uvs(0, 0, 16, 16)));
                    } else {
                        BlockBase baseBlock = (BlockBase) blockItem.getBlock();
                        String builder_name = new ResourceLocation(IGLib.MODID, "item/"+baseBlock.getHolderName()).getPath();
                        withExistingParent(builder_name, new ResourceLocation(IGLib.MODID, "block/base/" + baseBlock.getBlockUseType().getName()));
                    }
                } else if(item instanceof IGOreItem) {
                    genericOreItem(item);
                } else if (item instanceof ItemBase) {
                    genericItem(item);
                }
            } catch (Exception e){
                IGDataProvider.log.error("Failed to create Item Model: \n" + e);
            }
        }
    }

    private void genericItem(Item item){
        if(item == null){
            StackTraceElement where = new NullPointerException().getStackTrace()[1];
            IGDataProvider.log.warn("Skipping null item. ( {} -> {} )", where.getFileName(), where.getLineNumber());
            return;
        }
        ItemBase i = (ItemBase) item;
        withExistingParent(new ResourceLocation(IGLib.MODID, "item/" + i.getHoldingName()).getPath(), new ResourceLocation(IGLib.MODID, "item/base/" + i.getUseType().getName()));
    }

    private void genericOreItem(Item item){
        if(item == null){
            StackTraceElement where = new NullPointerException().getStackTrace()[1];
            IGDataProvider.log.warn("Skipping null item. ( {} -> {} )", where.getFileName(), where.getLineNumber());
            return;
        }
        IGOreItem i = (IGOreItem) item;
        withExistingParent(new ResourceLocation(IGLib.MODID, "item/" + i.getHoldingName()).getPath(), new ResourceLocation(IGLib.MODID, "item/base/" + i.getUseType().getName()));
    }

    private void createBucket(Fluid f){
        withExistingParent(f.getFilledBucket().asItem().getRegistryName().getPath(), forgeLoc("item/bucket"))
                .customLoader(DynamicBucketModelBuilder::begin)
                .fluid(f);
    }

    protected ResourceLocation forgeLoc(String str){
        return new ResourceLocation("forge", str);
    }

    private String name(IItemProvider item){
        return item.asItem().getRegistryName().getPath();
    }
}
