package igteam.api.materials;

import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.data.fluid.MaterialBaseFluid;
import igteam.api.materials.data.fluid.variants.*;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.fluids.FluidStack;
import igteam.api.processing.IGProcessingStage;

import java.util.Set;

public enum FluidEnum implements MaterialInterface<MaterialBaseFluid> {
    Brine(new MaterialFluidBrine()),
    SulfuricAcid(new MaterialFluidSulfuricAcid()),
    HydrochloricAcid(new MaterialFluidHydrochloricAcid()),
    HydrofluoricAcid(new MaterialFluidHydrofluoricAcid()),
    NitricAcid(new MaterialFluidNitricAcid()),
    SodiumHydroxide(new MaterialFluidSodiumHydroxide());

    private final MaterialBaseFluid material;

    FluidEnum(MaterialBaseFluid m){
        this.material = m;
    }

    @Override
    public MaterialBaseFluid instance() {
        return material;
    }

    @Override
    public IFeatureConfig getGenerationConfig() {
        return null;
    }

    @Override
    public boolean isFluidPortable(ItemPattern pattern){
        return material.isFluidPortable(pattern);
    }


}
