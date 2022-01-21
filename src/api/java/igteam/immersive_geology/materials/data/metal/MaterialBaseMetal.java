package igteam.immersive_geology.materials.data.metal;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;

public class MaterialBaseMetal extends MaterialBase {

    public MaterialBaseMetal(String name) {
        super(name);
}

    @Override
    public int getColor() {
        return 0;
    }

    public boolean isNative() {return false;}

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON; //setup default
    }

    protected void setupProcessingStages(){
        super.setupProcessingStages();
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        if(pattern instanceof BlockPattern b){
            return switch(b) {
                case ore -> new ResourceLocation(IGApi.MODID, "block/greyscale/rock/ore_bearing/vanilla/vanilla_normal");
                case storage -> new ResourceLocation(IGApi.MODID, "block/greyscale/metal/storage");
                default ->   new ResourceLocation(IGApi.MODID, "block/greyscale/metal/dust_block");
            };
        }

        if(pattern instanceof ItemPattern i){
            return switch(i) {
                case ore_chunk, stone_chunk -> new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_chunk_vein");
                case ore_bit, stone_bit -> new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_bit_vein");
                case dirty_crushed_ore, crushed_ore -> new ResourceLocation(IGApi.MODID, "item/greyscale/rock/crushed_ore");
                case clay -> new ResourceLocation(IGApi.MODID, "item/greyscale/rock/clay");
                case dust, gear, ingot, nugget, plate, rod, wire, metal_oxide -> new ResourceLocation(IGApi.MODID, "item/greyscale/metal/" + i.getName());
                case crystal -> new ResourceLocation(IGApi.MODID, "item/greyscale/rock/raw_crystal_" + getCrystalFamily().getName());
                default -> new ResourceLocation(IGApi.MODID, "item/greyscale/" + i.getName());
            };
        }
        return null;
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
        return isNative() &! hasExistingImplementation();
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
        return true;
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
}