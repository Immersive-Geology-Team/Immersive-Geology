package igteam.api.materials;

import igteam.api.IGApi;
import igteam.api.config.IGOreConfig;
import igteam.api.main.IGMultiblockProvider;
import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.data.misc.MaterialMiscBase;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public enum MiscEnum implements MaterialInterface<MaterialBase> {
    Glass(new MaterialMiscBase("glass"){
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
            return p == ItemPattern.ingot ? 0xD7C8A7 : super.getColor(p);
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
                    IRecipeBuilder.crafting(this).shaped(MiscEnum.Refractory.getBlock(BlockPattern.storage).asItem(), 1, "xx", "xx")
                            .setInputToCharacter('x', MiscEnum.Refractory.getItem(ItemPattern.ingot))
                            .finializeRecipe("general_crafting", "refractory", MiscEnum.Refractory.getItemTag(ItemPattern.ingot));

                    IRecipeBuilder.crafting(this).shaped(MiscEnum.Reinforced_refractory.getBlock(BlockPattern.storage).asItem(), 5, "crc", "rrr", "crc")
                            .setInputToCharacter('c', MetalEnum.Copper.getItem(ItemPattern.plate))
                            .setInputToCharacter('r', MiscEnum.Refractory.getBlock(BlockPattern.storage).asItem())
                            .finializeRecipe("general_crafting", "refractory", MiscEnum.Refractory.getItemTag(ItemPattern.ingot));

                    IRecipeBuilder.crafting(this).shaped(IGMultiblockProvider.bloomery.asItem(), 1, " r ", "r r", "rrr")
                            .setInputToCharacter('r', MiscEnum.Refractory.getBlock(BlockPattern.storage).asItem())
                            .finializeRecipe("general_crafting", "refractory", MiscEnum.Refractory.getItemTag(ItemPattern.ingot));

                }
            };
        }

        @Override
        public ResourceLocation getTextureLocation(MaterialPattern pattern) {
            return pattern == ItemPattern.ingot ? new ResourceLocation(IGApi.MODID, "item/greyscale/metal/ingot") : new ResourceLocation(IGApi.MODID, "block/static_block/refractory_brick");
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
    });

    private final MaterialBase material;

    MiscEnum(MaterialBase m){
        this.material = m;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern) {
        return material.getStack(pattern);
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        ItemStack i = material.getStack(pattern, secondaryMaterial);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material) : i;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        ItemStack i = material.getStack(pattern, secondaryMaterial);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material) : i;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, int amount) {
        return material.getStack(pattern, amount);
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount) {
        ItemStack i = material.getStack(pattern, secondaryMaterial, amount);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material, amount) : i;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial, int amount) {
        ItemStack i = material.getStack(pattern, secondaryMaterial, amount);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material, amount) : i;
    }

    @Override
    public Fluid getFluid(MaterialPattern pattern) {
        return material.getFluid(pattern);
    }

    @Override
    public Fluid getFluid(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        return material.getFluid(pattern, secondaryMaterial);
    }

    @Override
    public FluidStack getFluidStack(MaterialPattern pattern, int amount) {
        return material.getFluidStack(pattern, amount);
    }

    @Override
    public FluidStack getFluidStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount) {
        return material.getFluidStack(pattern, secondaryMaterial, amount);
    }

    @Override
    public Item getItem(MaterialPattern pattern) {
        return material.getItem(pattern);
    }

    @Override
    public Item getItem(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        Item i = material.getItem(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getItem(pattern, material) : i;
    }

    @Override
    public Item getItem(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        Item i = material.getItem(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getItem(pattern, material) : i;
    }

    @Override
    public Block getBlock(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        Block i = material.getBlock(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getBlock(pattern, material) : i;
    }

    @Override
    public Block getBlock(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        Block b = material.getBlock(pattern, secondaryMaterial);
        return b == null ? secondaryMaterial.getBlock(pattern, material) : b;
    }

    @Override
    public Block getBlock(MaterialPattern pattern) {
        return material.getBlock((BlockPattern) pattern);
    }

    @Override
    public ITag.INamedTag<?> getTag(MaterialPattern pattern) {
        return material.getTag(pattern);
    }

    @Override
    public boolean hasPattern(MaterialPattern pattern) {
        return material.hasPattern(pattern);
    }

    @Override
    public String getName() {
        return material.getName();
    }

    @Override
    public Set<IGProcessingStage> getStages() {
        return material.getStages();
    }

    @Override
    public void build() {
        material.build();
    }

    @Override
    public ITag.INamedTag<Item> getItemTag(MaterialPattern pattern) {
        return material.getItemTag(pattern);
    }

    @Override
    public ITag.INamedTag<Block> getBlockTag(BlockPattern pattern) {
        return material.getBlockTag(pattern);
    }

    @Override
    public ITag.INamedTag<Item> getItemTag(MaterialPattern pattern, MaterialBase... materials) {
        return material.getItemTag(pattern, materials);
    }

    @Override
    public ITag.INamedTag<Block> getBlockTag(BlockPattern pattern, MaterialBase... materials) {
        return material.getBlockTag(pattern, materials);
    }

    @Override
    public ITag.INamedTag<Fluid> getFluidTag(FluidPattern pattern) {
        return material.getFluidTag(pattern);
    }

    @Override
    public ITag.INamedTag<Fluid> getFluidTag(FluidPattern pattern, MaterialBase... materials) {
        return material.getFluidTag(pattern, materials);
    }

    @Override
    public int getColor(MaterialPattern p) {
        return material.getColor(p);
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        return material.getTextureLocation(pattern);
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern, int subtype) {
        return material.getTextureLocation(pattern, subtype);
    }

    @Override
    public boolean hasExistingImplementation() {
        return material.hasExistingImplementation();
    }

    @Override
    public boolean generateOreFor(MaterialInterface m) {
        return material.generateOreFor(m);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return material.getCrystalFamily();
    }

    @Override
    public MaterialBase get() {
        return material;
    }

    @Override
    public IGOreConfig getGenerationConfig() {
        return null;
    }

    @Override
    public boolean isFluidPortable(ItemPattern bucket) {
        return material.isFluidPortable(bucket);
    }

    @Override
    public MaterialSourceWorld getDimension() {
        return MaterialSourceWorld.overworld;
    }
}