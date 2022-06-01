package igteam.immersive_geology.materials.data.mineral;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.main.IGRegistryProvider;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.SlurryEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;

public abstract class MaterialBaseMineral extends MaterialBase {
    public MaterialBaseMineral(String name) {
        super(name);
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xFFFFFF;
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
        return false;
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

    @Override
    protected boolean hasCrystal() {
        return true;
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

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        if(pattern instanceof BlockPattern){
            BlockPattern b = (BlockPattern) pattern;
            switch(b) {
                case ore: return new ResourceLocation(IGApi.MODID, "block/greyscale/rock/ore_bearing/vanilla/vanilla_normal");
                case storage: return new ResourceLocation(IGApi.MODID, "block/greyscale/metal/storage");
                case geode: return new ResourceLocation(IGApi.MODID, "block/greyscale/stone/geode");
                default: return new ResourceLocation(IGApi.MODID, "block/greyscale/stone/cobble");
            }
        }

        if(pattern instanceof ItemPattern){
            ItemPattern i = (ItemPattern) pattern;
            switch(i) {
                case ore_chunk:
                case stone_chunk:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_chunk_vein");
                case ore_bit: case stone_bit:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_bit_vein");
                case dirty_crushed_ore: case crushed_ore:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/crushed_ore");
                case clay:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/clay");
                case dust: case gear: case ingot: case nugget: case plate: case rod: case wire: case metal_oxide:
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
    public boolean isFluidPortable(ItemPattern pattern){
        return false;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this,"Default Ore Processing Stage") {
            @Override
            protected void describe() {

                for (MaterialInterface<?> stone : StoneEnum.values()) {
                    IRecipeBuilder.separating(this).create(
                            getParentMaterial().getStack(ItemPattern.crushed_ore), //input
                            1, //amount out
                            getParentMaterial().getItemTag(ItemPattern.dirty_crushed_ore, stone.get()),//result
                            stone.getStack(ItemPattern.stone_bit));

                    IRecipeBuilder.crafting(this)
                            .shapeless(stone.getItem(ItemPattern.dirty_crushed_ore, getParentMaterial()), 1, getItemTag(ItemPattern.ore_chunk, stone.get()), getItemTag(ItemPattern.ore_chunk, stone.get()))
                            .finializeRecipe("crush_ore_chunks", "has_chunk", getItemTag(ItemPattern.ore_chunk, stone.get()));
                }

            }
        };
    }
}
