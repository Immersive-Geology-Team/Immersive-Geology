package igteam.api.materials.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.IGApi;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.BlockFamily;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.Set;

import static net.minecraft.server.packs.PackType.CLIENT_RESOURCES;

public abstract class MaterialBaseMineral extends MaterialBase {
    public MaterialBaseMineral(String name) {
        super(name);
        ArrayList<PeriodicTableElement.ElementProportion> elementProportions = new ArrayList<>(getElements());
        boolean hasSulphur = false;

        for (PeriodicTableElement.ElementProportion element : elementProportions) {
            if(element.getElement().equals(PeriodicTableElement.SULFUR)){
                hasSulphur = true;
                break;
            }
        }

        sourceWorld = hasSulphur ? MaterialSourceWorld.nether : MaterialSourceWorld.overworld;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    protected boolean hasStorageBlock() {
        return false;
    }

    @Override
    protected boolean hasStairs() {
        return false;
    }

    @Override
    protected boolean hasOreBlock() {
        return true;
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
        return false;
    }

    @Override
    protected boolean hasIngot() {
        return false;
    }

    @Override
    protected boolean hasWire() {
        return false;
    }

    @Override
    protected boolean hasGear() {
        return false;
    }

    @Override
    protected boolean hasRod() {
        return false;
    }

    @Override
    public boolean isSalt() { return false; }

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

    //If there's I enable it manually
    @Override
    protected boolean hasDust() {
        return true;
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
        return false;
    }

    @Override
    protected boolean hasNugget() {
        return false;
    }

    //There's no need
    @Override
    protected boolean hasCrystal() {
        return false;
    }

    @Override
    protected boolean hasOreBit() {
        return true;
    }

    @Override
    protected boolean hasOreChunk() {
        return true;
    }

    @Override
    protected boolean hasStoneBit() {
        return false;
    }

    @Override
    protected boolean hasCrushedOre() {
        return true;
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
        return true;
    }

    public static ExistingFileHelper EXISTING_HELPER;

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        ResourceLocation returnTexture = new ResourceLocation(IGApi.MODID, "null"); //This doesn't exist so if it's parsed it'll Trigger GreyScale Textures

        if (pattern instanceof BlockFamily) {
            returnTexture = new ResourceLocation(IGApi.MODID, "block/colored/" + this.name + "/" + pattern.getName());
        }

        if (pattern instanceof ItemFamily) {
            returnTexture = new ResourceLocation(IGApi.MODID, "item/colored/" + this.name + "/" + pattern.getName());
        }

        boolean exists = EXISTING_HELPER.exists(new ResourceLocation(IGApi.MODID, "textures/" + returnTexture.getPath() + ".png"), CLIENT_RESOURCES);
        return exists ? returnTexture : greyScaleTextures(pattern);
    }

    private ResourceLocation greyScaleTextures(MaterialPattern pattern) {
        if(pattern instanceof BlockFamily){
            BlockFamily b = (BlockFamily) pattern;
            switch(b) {
                case ore:
                    String ore_overlay = getCrystalFamily() != null ? getCrystalFamily().getName() : "vanilla_normal";
                    return new ResourceLocation(IGApi.MODID, "block/greyscale/rock/ore_bearing/vanilla/" + ore_overlay);
                case storage: return new ResourceLocation(IGApi.MODID, "block/greyscale/metal/storage");
                case dust_block: return new ResourceLocation(IGApi.MODID, "block/greyscale/metal/dust_block");
                case geode: return new ResourceLocation(IGApi.MODID, "block/greyscale/stone/geode");
                default: return new ResourceLocation(IGApi.MODID, "block/greyscale/stone/cobble");
            }
        }

        if(pattern instanceof ItemFamily){
            ItemFamily i = (ItemFamily) pattern;
            switch(i) {
                case ore_chunk:
                    String chunk_vein_name = "rock_chunk_vein" + (getCrystalFamily() != null ? "_" + getCrystalFamily().getName() : "");
                    ResourceLocation chunkLoc = new ResourceLocation(IGApi.MODID, "item/greyscale/rock/ore_chunk/" + chunk_vein_name);
                    return chunkLoc;
                case stone_chunk:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/ore_chunk/rock_chunk_vein");
                case ore_bit: case stone_bit:
                    String bit_vein_name = "rock_bit_vein" + (getCrystalFamily() != null ? "_" + getCrystalFamily().getName() : "");
                    ResourceLocation bitLoc = new ResourceLocation(IGApi.MODID, "item/greyscale/rock/ore_bit/" + bit_vein_name);
                    return bitLoc;
                case dirty_crushed_ore: case crushed_ore:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/crushed_ore");
                case clay: case slag:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/" + i.getName());
                case dust: case gear: case ingot: case nugget: case plate: case rod: case wire: case metal_oxide: case compound_dust:
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
        IGApi.getNewLogger().warn("Used a Subtype (" + String.valueOf(subtype) + ") for Material Pattern: " + pattern.getName() + " made of " + getName() + " | No Handling is setup for such.");
        return getTextureLocation(pattern);
    }

    @Override
    public boolean isFluidPortable(ItemFamily pattern){
        return false;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {
                for (MaterialInterface<?> stone : StoneEnum.values()) {
                    if(isSalt()) {
                        IRecipeBuilder.crushing(this).create(getName() + "_oreblock_to_salt",
                                getItemTag(BlockFamily.ore, stone.instance()), getParentMaterial().getStack(ItemFamily.dust, 5), getParentMaterial().getItemTag(ItemFamily.dust), 6000, 200, 0.3f);
                    }
                    if (hasOreChunk()) {
                        if (hasOreBlock()) {
                            IRecipeBuilder.cutting(this).create(
                                    getItemTag(BlockFamily.ore, stone.instance()),
                                    new FluidTagInput(FluidTags.WATER, 80),
                                    stone.getStack(ItemFamily.ore_chunk, getParentMaterial(), 5)
                            );

                            IRecipeBuilder.crushing(this).create(getName() + "_oreblock_to_chunk",
                                    getItemTag(BlockFamily.ore, stone.instance()), stone.getStack(ItemFamily.ore_chunk, getParentMaterial(), 3), 6000, 200);
                        }

                        IRecipeBuilder.crushing(this).create(getName() + "_orechunk_to_dirtycrush",
                                getItemTag(ItemFamily.ore_chunk, stone.instance()), stone.getStack(ItemFamily.dirty_crushed_ore, getParentMaterial()), 6000, 200);
                        if (hasCrushedOre()) {
                            IRecipeBuilder.crafting(this)
                                    .shapeless(stone.getItem(ItemFamily.dirty_crushed_ore, getParentMaterial()), 1, getItemTag(ItemFamily.ore_chunk, stone.instance()), getItemTag(ItemFamily.ore_chunk, stone.instance()))
                                    .finializeRecipe("crush_ore_chunks", "has_chunk", getItemTag(ItemFamily.ore_chunk, stone.instance()));

                            if (hasDirtyCrushedOre())
                                IRecipeBuilder.crafting(this)
                                        .shapeless(getParentMaterial().getItem(ItemFamily.crushed_ore), 1, getParentMaterial().getItemTag(ItemFamily.dirty_crushed_ore), getParentMaterial().getItemTag(ItemFamily.dirty_crushed_ore))
                                        .finializeRecipe("wash_dirty_ore", "has_chunk", getItemTag(ItemFamily.ore_chunk));
                        }
                        if (hasOreBit())
                        {
                            IRecipeBuilder.crafting(this).shaped( stone.getItem(ItemFamily.ore_chunk, getParentMaterial()), 1, "ooo", "ooo", "ooo")
                                    .setInputToCharacter('o',  stone.getItem(ItemFamily.ore_bit, getParentMaterial()))
                                    .finializeRecipe("general_crafting", "has_ore_bit", stone.getItemTag(ItemFamily.ore_bit, getParentMaterial()));
                        }
                    }

                    if (hasCrushedOre() && hasDirtyCrushedOre()) {
                        IRecipeBuilder.separating(this).create(
                                getParentMaterial().getItemTag(ItemFamily.dirty_crushed_ore, stone.instance()),//result
                                getParentMaterial().getStack(ItemFamily.crushed_ore), //input
                                stone.getStack(ItemFamily.stone_bit));
                    }
                }
            }
        };

        new IGProcessingStage(this, IGStageDesignation.preparation){
            @Override
            protected void describe() {
                if (hasDust() && hasCrystal()) {
                    IRecipeBuilder.crushing(this).create(getName() + "_crystal_to_grit",
                            getItemTag(ItemFamily.crystal),  getStack(ItemFamily.dust),1000, 500);
                }
            }
        };
    }

    public abstract Set<MaterialInterface<?>> getSourceMaterials();

    public String getSecondSourceMetals(){
        return "Chemical X and Chemical Y";
    }
}
