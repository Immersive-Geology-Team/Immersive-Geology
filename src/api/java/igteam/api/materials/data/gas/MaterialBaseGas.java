package igteam.api.materials.data.gas;

import igteam.api.materials.data.fluid.MaterialBaseFluid;
import igteam.api.materials.pattern.FluidFamily;
import igteam.api.IGApi;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;

public abstract class MaterialBaseGas extends MaterialBaseFluid {
    //Gas is a 'special' type of fluid, technically it'd be way better if we had some kind of ability to work with Mekanism Gas, but eh. This will do. ~Muddyakt.
    public MaterialBaseGas(String name) {
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
        return false;
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
        return false;
    }

    @Override
    protected boolean hasOreBit() {
        return false;
    }

    @Override
    protected boolean hasOreChunk() {
        return false;
    }

    @Override
    protected boolean hasStoneBit() {
        return false;
    }

    @Override
    protected boolean hasCrushedOre() {
        return false;
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
        return false;
    }

    @Override
    protected boolean isGas() {
        return true;
    }

    @Override
    public boolean hasFlask() {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        if(pattern instanceof FluidFamily){
            FluidFamily m = (FluidFamily) pattern;
            switch (m){
                case fluid:
                    return new ResourceLocation(IGApi.MODID, "block/fluid/default_still");
            }
        }
        if(pattern instanceof ItemFamily){
            ItemFamily i = (ItemFamily) pattern;
            switch (i){
                case flask:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/fluid/compound_flask_fluid");
            }
        }

        return new ResourceLocation(IGApi.MODID, "block/fluid/default_still");
    }

    protected void setupProcessingStages() {
        super.setupProcessingStages();
    }

        @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern, int subtype) {
        if(pattern instanceof ItemFamily){
            ItemFamily i = (ItemFamily) pattern;
            switch (i){
                case flask:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/fluid/compound_flask_fluid");
            }
        }

        return subtype == 0 ? new ResourceLocation(IGApi.MODID, "block/fluid/default_still") : new ResourceLocation(IGApi.MODID, "block/fluid/default_flowing");
    }
}
