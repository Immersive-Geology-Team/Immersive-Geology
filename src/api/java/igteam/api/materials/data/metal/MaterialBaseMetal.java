package igteam.api.materials.data.metal;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.IGApi;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

public abstract class MaterialBaseMetal extends MaterialBase {

    public MaterialBaseMetal(String name) {
        super(name);
    }

    public boolean isNative() {
        return false;
    }

    @Override
    protected boolean hasDustBlock() {
        return hasDust();
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON; //setup default
    }

    protected void setupProcessingStages() {
        super.setupProcessingStages();
        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {
                if (isNative()) { //TODO Double check if this needs to be native?
                    for (MaterialInterface<?> stone : StoneEnum.values()) {
                        ItemStack dirty_crushed_ore = stone.getStack(ItemPattern.dirty_crushed_ore, getParentMaterial());
                        ITag<Item> ore_chunk = getParentMaterial().getItemTag(ItemPattern.ore_chunk, stone.instance());
                        ITag<Item> ore_bit = getParentMaterial().getItemTag(ItemPattern.ore_bit, stone.instance());

                        IRecipeBuilder.crushing(this).create("crush_chunk_" + getName() + "_to_dirty_crushed",
                                ore_chunk, dirty_crushed_ore, ore_bit, 6000, 200, 0.33f);

                        IRecipeBuilder.separating(this).create(
                                getParentMaterial().getItemTag(ItemPattern.dirty_crushed_ore),//input
                                getParentMaterial().getStack(ItemPattern.crushed_ore),
                                stone.getStack(ItemPattern.stone_bit)
                        );

                        IRecipeBuilder.crafting(this)
                                .shapeless(getParentMaterial().getItem(ItemPattern.crushed_ore), 1, getParentMaterial().getItemTag(ItemPattern.dirty_crushed_ore, stone.instance()), getParentMaterial().getItemTag(ItemPattern.dirty_crushed_ore, stone.instance()))
                                .finializeRecipe("wash_dirty_ore", "has_chunk", getItemTag(ItemPattern.ore_chunk, stone.instance()));

                        IRecipeBuilder.crafting(this)
                                .shapeless(stone.getItem(ItemPattern.dirty_crushed_ore, getParentMaterial()), 1, getParentMaterial().getItemTag(ItemPattern.ore_chunk, stone.instance()), getParentMaterial().getItemTag(ItemPattern.ore_chunk, stone.instance()))
                                .finializeRecipe("crush_ore_chunks", "has_chunk", getParentMaterial().getItemTag(ItemPattern.ore_chunk, stone.instance()));
                        if (hasOreBit())
                        {
                            IRecipeBuilder.crafting(this).shaped( stone.getItem(ItemPattern.ore_chunk, getParentMaterial()), 1, "ooo", "ooo", "ooo")
                                    .setInputToCharacter('o',  stone.getItem(ItemPattern.ore_bit, getParentMaterial()))
                                    .finializeRecipe("general_crafting", "has_ore_bit", stone.getItemTag(ItemPattern.ore_bit, getParentMaterial()));
                        }
                        if (!hasExistingImplementation()) {
                            IRecipeBuilder.crushing(this).create(getName() + "_oreblock_to_chunk",
                                    getItemTag(BlockPattern.ore, stone.instance()), stone.getStack(ItemPattern.ore_chunk, getParentMaterial(), 3), 6000, 200);

                            IRecipeBuilder.cutting(this).create(
                                    getItemTag(BlockPattern.ore, stone.instance()),
                                    new FluidTagInput(FluidTags.WATER, 80),
                                    stone.getStack(ItemPattern.ore_chunk, getParentMaterial(), 5)
                            );
                        }

                        IRecipeBuilder.crushing(this).create(getName() + "_orechunk_to_dirtycrush",
                                getItemTag(ItemPattern.ore_chunk, stone.instance()), stone.getStack(ItemPattern.dirty_crushed_ore, getParentMaterial()), 6000, 200);

                    }
                }
            }
        };

        //Also note Osmium is a Native metal, so yeah I don't think that'd work for it... May need to hardcode the Bloomery recipes, it may just be better to do so


        new IGProcessingStage(this, IGStageDesignation.preparation) {
            @Override
            protected void describe() {
                if (isNative()) { //TODO Double check if this needs to be Native
                    if (!hasExistingImplementation()) {
                    }

                    IRecipeBuilder.basicSmelting(this)
                            .create(getItemTag(ItemPattern.dust), getItem(ItemPattern.dust),
                                    getItem(ItemPattern.ingot));

                    IRecipeBuilder.basicSmelting(this).create(getItemTag(ItemPattern.crushed_ore),
                            getItem(ItemPattern.crushed_ore),
                            getItem(ItemPattern.ingot));
                }

                if (hasCrystal() && (hasDust() || hasExistingImplementation())) {
                    IRecipeBuilder.crushing(this).create(
                            "crystal_" + getName() + "_to_dust",
                            getItemTag(ItemPattern.crystal), getStack(ItemPattern.dust), 3000, 200);
                    IRecipeBuilder.basicSmelting(this).create(
                            getItemTag(ItemPattern.crystal),
                            getItem(ItemPattern.crystal),
                            getItem(ItemPattern.ingot));
                }
                if (hasDust() && hasIngot()) {
                    IRecipeBuilder.crushing(this).create(
                            "ingot_" + getName() + "_to_dust",
                            getItemTag(ItemPattern.ingot), getStack(ItemPattern.dust), 3000, 200);
                    IRecipeBuilder.basicSmelting(this).create(
                            getItemTag(ItemPattern.dust),
                            getItem(ItemPattern.dust),
                            getItem(ItemPattern.ingot));

                    IRecipeBuilder.arcSmelting(this).create("dust_" + getName() + "_to_ingot",
                                    new IngredientWithSize(getItemTag(ItemPattern.dust), 1),
                                    getStack(ItemPattern.ingot), null)
                            .setEnergyTime(51200, 100);
                }


            }
        };
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        ResourceLocation returnTexture = new ResourceLocation(IGApi.MODID, "block/colored/" + this.name + "/" + pattern.getName()); //This doesn't exist so if it's parsed it'll Trigger GreyScale Textures


        if (pattern instanceof BlockPattern) {
            //work around for slabs
            returnTexture = new ResourceLocation(IGApi.MODID, "block/colored/" + this.name + "/" + pattern.getName());
            if (pattern.get() == BlockPattern.slab) {
                returnTexture = new ResourceLocation(IGApi.MODID, "block/colored/" + this.name + "/" + BlockPattern.sheetmetal.getName());
            }
        }

        if (pattern instanceof ItemPattern) {
            returnTexture = new ResourceLocation(IGApi.MODID, "item/colored/" + this.name + "/" + pattern.getName());
        }

        boolean exists = StaticTemplateManager.EXISTING_HELPER.exists(new ResourceLocation(IGApi.MODID, "textures/" + returnTexture.getPath() + ".png"), ResourcePackType.CLIENT_RESOURCES);
        return exists ? returnTexture : greyScaleTextures(pattern);
    }

    private ResourceLocation greyScaleTextures(MaterialPattern pattern) {
        if (pattern instanceof BlockPattern) {
            BlockPattern b = (BlockPattern) pattern;
            switch (b) {
                case ore:
                    return new ResourceLocation(IGApi.MODID, "block/greyscale/rock/ore_bearing/vanilla/vanilla_normal");
                case storage:
                    return new ResourceLocation(IGApi.MODID, "block/greyscale/metal/storage");
                case slab:
                case sheetmetal:
                    return new ResourceLocation(IGApi.MODID, "block/greyscale/metal/sheetmetal");
                case dust_block:
                    return new ResourceLocation(IGApi.MODID, "block/greyscale/metal/dust_block");
                case geode:
                    return new ResourceLocation(IGApi.MODID, "block/greyscale/stone/geode");
                default:
                    return new ResourceLocation(IGApi.MODID, "block/greyscale/stone/cobble");
            }
        }

        if (pattern instanceof ItemPattern) {
            ItemPattern i = (ItemPattern) pattern;
            switch (i) {
                case ore_chunk:
                    String chunk_vein_name = "rock_chunk_vein" + (getCrystalFamily() != null ? "_" + getCrystalFamily().getName() : "");
                    ResourceLocation chunkLoc = new ResourceLocation(IGApi.MODID, "item/greyscale/rock/ore_chunk/" + chunk_vein_name);
                    return chunkLoc;
                case stone_chunk:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/ore_chunk/rock_chunk_vein");
                case ore_bit:
                case stone_bit:
                    String bit_vein_name = "rock_bit_vein" + (getCrystalFamily() != null ? "_" + getCrystalFamily().getName() : "");
                    ResourceLocation bitLoc = new ResourceLocation(IGApi.MODID, "item/greyscale/rock/ore_bit/" + bit_vein_name);
                    return bitLoc;
                case dirty_crushed_ore:
                case crushed_ore:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/crushed_ore");
                case clay:
                case slag:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/" + i.getName());
                case dust:
                case gear:
                case ingot:
                case nugget:
                case plate:
                case rod:
                case wire:
                case metal_oxide:
                case compound_dust:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/metal/" + i.getName());
                case crystal:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/crystal/raw_crystal_" + getCrystalFamily().getName());
                default:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/" + i.getName());
            }
        }
        return null;
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern, int subtype) {
        return null;
    }

    @Override
    protected boolean hasSheetmetalBlock() {
        return !hasExistingImplementation();
    }

    @Override
    protected boolean hasStorageBlock() {
        return !hasExistingImplementation();
    }

    @Override
    protected boolean hasStairs() {
        return false;
    }

    @Override
    protected boolean hasOreBlock() {
        return isNative();
    }

    @Override
    protected boolean hasGeodeBlock() {
        return false;
    }

    @Override
    protected boolean hasDefaultBlock() {
        return false;
    }

    @Override
    protected boolean hasSlab() {
        return !hasExistingImplementation();
    }

    @Override
    protected boolean hasIngot() {
        return !hasExistingImplementation();
    }

    @Override
    protected boolean hasWire() {
        return false;
    }

    @Override
    protected boolean hasGear() {
        return true;
    }

    @Override
    protected boolean hasRod() {
        return !hasExistingImplementation();
    }

    @Override
    protected boolean isMachine() {
        return false;
    }

    @Override
    protected boolean isSlurry() {
        return false;
    }

    @Override
    protected boolean isFluid() {
        return false;
    }

    @Override
    protected boolean hasClay() {
        return false;
    }

    @Override
    protected boolean hasDust() {
        return !hasExistingImplementation();
    }

    @Override
    protected boolean hasFuel() {
        return false;
    }

    @Override
    protected boolean hasSlag() {
        return false;
    }

    @Override
    protected boolean hasPlate() {
        return !hasExistingImplementation();
    }

    @Override
    protected boolean hasNugget() {
        return hasIngot();
    }

    @Override
    protected boolean hasCrystal() {
        return true;
    }

    @Override
    protected boolean hasOreBit() {
        return isNative();
    }

    @Override
    protected boolean hasOreChunk() {
        return isNative();
    }

    @Override
    protected boolean hasStoneBit() {
        return false;
    }

    @Override
    protected boolean hasCrushedOre() {
        return isNative();
    }

    @Override
    protected boolean hasMetalOxide() {
        return false;
    }

    @Override
    protected boolean hasStoneChunk() {
        return false;
    }

    @Override
    protected boolean hasCompoundDust() {
        return false;
    }

    @Override
    protected boolean hasDirtyCrushedOre() {
        return isNative();
    }

    @Override
    public boolean generateOreFor(MaterialInterface m) {
        if (m instanceof StoneEnum) {
            StoneEnum s = (StoneEnum) m;
            switch (s) {
                case Stone:
                case Netherrack:
                    return !hasExistingImplementation();
                case Granite:
                    return isNative();
            }
        }
        return false;
    }

    @Override
    public boolean isFluidPortable(ItemPattern pattern) {
        return false;
    }

    public abstract ArrayList<MaterialInterface<? extends MaterialBaseMineral>> getSourceMinerals();
}