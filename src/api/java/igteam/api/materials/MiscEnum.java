package igteam.api.materials;

import igteam.api.IGApi;
import igteam.api.main.IGMultiblockProvider;
import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.data.misc.MaterialMiscBase;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockFamily;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;


public enum MiscEnum implements MaterialInterface<MaterialBase> {
    Glass(new MaterialMiscBase("glass") {
        @Override
        public boolean hasFlask(){
            return true;
        }

        @Override
        public ResourceLocation getTextureLocation(MaterialPattern pattern) {
            return new ResourceLocation(IGApi.MODID, "item/greyscale/fluid/empty_flask");
        }
    }),

    Refractory(new MaterialMiscBase("refractory_brick"){

        @Override
        public int getColor(MaterialPattern p) {
            return p == ItemFamily.ingot ? 0xD7C8A7 : super.getColor(p);
        }

        @Override
        protected boolean hasStorageBlock() {
            return true;
        }

        @Override
        protected boolean hasSlab() {
            return true;
        }

        @Override
        protected boolean hasStairs() {
            return true;
        }

        @Override
        protected boolean hasIngot() {
            return true;
        }

        @Override
        protected void setupProcessingStages() {
            super.setupProcessingStages();

            new IGProcessingStage(this, IGStageDesignation.preparation) {
                @Override
                protected void describe() {
                    IRecipeBuilder.crafting(this).shaped(MiscEnum.Refractory.getBlock(BlockFamily.storage).asItem(), 1, "xx", "xx")
                            .setInputToCharacter('x', MiscEnum.Refractory.getItem(ItemFamily.ingot))
                            .finializeRecipe("general_crafting", "refractory", MiscEnum.Refractory.getItemTag(ItemFamily.ingot));

                    IRecipeBuilder.crafting(this).shaped(MiscEnum.Reinforced_refractory.getBlock(BlockFamily.storage).asItem(), 5, "crc", "rrr", "crc")
                            .setInputToCharacter('c', MetalEnum.Copper.getItem(ItemFamily.plate))
                            .setInputToCharacter('r', MiscEnum.Refractory.getBlock(BlockFamily.storage).asItem())
                            .finializeRecipe("general_crafting", "refractory", MiscEnum.Refractory.getItemTag(ItemFamily.ingot));

                    IRecipeBuilder.crafting(this).shaped(IGMultiblockProvider.bloomery.asItem(), 1, " r ", "r r", "rrr")
                            .setInputToCharacter('r', MiscEnum.Refractory.getBlock(BlockFamily.storage).asItem())
                            .finializeRecipe("general_crafting", "refractory", MiscEnum.Refractory.getItemTag(ItemFamily.ingot));

                    IRecipeBuilder.crafting(this).shaped(MiscEnum.Refractory.getBlock(BlockFamily.slab).asItem(), 6, "   ", "   ", "rrr")
                            .setInputToCharacter('r', MiscEnum.Refractory.getBlock(BlockFamily.storage).asItem())
                            .finializeRecipe("general_crafting", "refractory", MiscEnum.Refractory.getItemTag(ItemFamily.ingot));

                    IRecipeBuilder.crafting(this).shaped(MiscEnum.Refractory.getBlock(BlockFamily.stairs).asItem(), 4, "r  ", "rr ", "rrr")
                            .setInputToCharacter('r', MiscEnum.Refractory.getBlock(BlockFamily.storage).asItem())
                            .finializeRecipe("general_crafting", "refractory", MiscEnum.Refractory.getItemTag(ItemFamily.ingot));

                }
            };
        }

        @Override
        public ResourceLocation getTextureLocation(MaterialPattern pattern) {
            return pattern == ItemFamily.ingot ? new ResourceLocation(IGApi.MODID, "item/greyscale/metal/ingot") : new ResourceLocation(IGApi.MODID, "block/static_block/refractory_brick");
        }
    }),
    Reinforced_refractory(new MaterialMiscBase("reinforced_refractory_brick"){
        @Override
        protected boolean hasStorageBlock() {
            return true;
        }
        @Override
        protected boolean hasSlab() {
            return true;
        }

        @Override
        protected boolean hasStairs() {
            return true;
        }
        @Override
        public ResourceLocation getTextureLocation(MaterialPattern pattern) {
            return new ResourceLocation(IGApi.MODID, "block/static_block/reinforced_refractory_brick");
        }

        @Override
        protected void setupProcessingStages() {
            super.setupProcessingStages();

            new IGProcessingStage(this, IGStageDesignation.preparation) {
                @Override
            protected void describe() {
                    IRecipeBuilder.crafting(this).shaped(MiscEnum.Reinforced_refractory.getBlock(BlockFamily.slab).asItem(), 6, "   ", "   ", "rrr")
                            .setInputToCharacter('r', MiscEnum.Reinforced_refractory.getBlock(BlockFamily.storage).asItem())
                            .finializeRecipe("general_crafting", "refractory", MiscEnum.Reinforced_refractory.getItemTag(ItemFamily.ingot));

                    IRecipeBuilder.crafting(this).shaped(MiscEnum.Reinforced_refractory.getBlock(BlockFamily.stairs).asItem(), 4, "r  ", "rr ", "rrr")
                            .setInputToCharacter('r', MiscEnum.Reinforced_refractory.getBlock(BlockFamily.storage).asItem())
                            .finializeRecipe("general_crafting", "refractory", MiscEnum.Reinforced_refractory.getItemTag(ItemFamily.ingot));
                }
            };
        }
    }),
    Coal(new MaterialMiscBase("coal"){
        @Override
        protected boolean hasDust() { return true; };

        @Override
        public int getColor(MaterialPattern p) {
            return 0x212121;
        }

        @Override
        public ResourceLocation getTextureLocation(MaterialPattern pattern) {
            return new ResourceLocation(IGApi.MODID, "item/greyscale/metal/dust");
        }

        @Override
        protected void setupProcessingStages() {
            super.setupProcessingStages();
            new IGProcessingStage(this, IGStageDesignation.preparation) {
                @Override
                protected void describe() {
                    TagKey<Item> t = ItemTags.create(new ResourceLocation("coals"));
                    IRecipeBuilder.crushing(this).create( getName() + "_to_dust",
                            t,
                            getStack(ItemFamily.dust), 3000, 200);

                }
            };
        }
    });

    private final MaterialBase material;

    MiscEnum(MaterialBase m){
        this.material = m;
    }

    @Override
    public MaterialBase instance() {
        return material;
    }

    @Override
    public FeatureConfiguration getGenerationConfig() {
        return null;
    }

    @Override
    public boolean isFluidPortable(ItemFamily bucket) {
        return material.isFluidPortable(bucket);
    }
}
