package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralRockSalt extends MaterialBaseMineral {

    public MaterialMineralRockSalt() {
        super("rocksalt");
        initializeColorMap((p) -> 0xffffff);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.synthesis, "Brine and Hydroxide Production")
        {
            @Override
            protected void describe(){
             /*   IRecipeBuilder.crushing(this).create(
                        "crushed_ore_to_dust",
                        getItemTag(ItemPattern.crushed_ore),
                        getStack(ItemPattern.dust),
                        3000, 200);*/

                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_brine",
                        getItemTag(ItemPattern.dust), 2,
                        new FluidTagInput(FluidTags.WATER, 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        ItemStack.EMPTY,
                        new FluidStack(FluidEnum.Brine.getFluid(FluidPattern.fluid), 250),
                        100, 12800);

                IRecipeBuilder.chemical(this).create(
                        "sodium_oxide_to_hydroxide",
                        MetalEnum.Sodium.getItemTag(ItemPattern.metal_oxide), 2,
                        new FluidTagInput(FluidTags.WATER, 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        ItemStack.EMPTY,
                        new FluidStack(FluidEnum.SodiumHydroxide.getFluid(FluidPattern.fluid), 250),
                        100, 12800);
            }
        };

        new IGProcessingStage(this,IGStageDesignation.synthesis, "Hydrochloric Production") {
            @Override
            protected void describe() {
                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_acid_and_salt",
                        getItemTag(ItemPattern.dust), 1,
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Sodium.getStack(ItemPattern.compound_dust),
                        new FluidStack(FluidEnum.HydrochloricAcid.getFluid(FluidPattern.fluid), 125),
                        100, 12800);

                IRecipeBuilder.chemical(this).create(
                        "brine_to_acid_and_salt",
                        null,0,
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidEnum.Brine.getFluidTag(FluidPattern.fluid), 125),
                        MetalEnum.Sodium.getStack(ItemPattern.compound_dust),
                        new FluidStack(FluidEnum.HydrochloricAcid.getFluid(FluidPattern.fluid), 125),
                        100, 12800);
            }
        };
        new IGProcessingStage(this,IGStageDesignation.extraction, "Sodium Extraction") {
            @Override
            protected void describe() {
                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Sodium.getName() + "_to_metal_oxide",
                        MetalEnum.Sodium.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Sodium.getItemTag(ItemPattern.compound_dust), 1,
                        300,153600);

                IRecipeBuilder.arcSmelting(this).create("metal_oxide_"+getName() +"_to_dust",
                                MetalEnum.Sodium.getItemTag(ItemPattern.metal_oxide), 1,
                                MetalEnum.Sodium.getStack(ItemPattern.dust),null,
                                new IngredientWithSize(IETags.coalCokeDust, 1))
                        .setEnergyTime(102400, 200);
            }
        };

    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SODIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CHLORINE)
        )
        );
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.COMMON;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Sodium);
        sources.add(FluidEnum.HydrochloricAcid);

        return sources;
    }

    @Override
    public boolean isSalt() { return true; }

    @Override
    protected boolean hasCrystal() { return true; }

    @Override
    protected boolean hasOreBit() { return false; }

    @Override
    protected boolean hasOreChunk() { return false; }

    @Override
    protected boolean hasDirtyCrushedOre() { return false; }

    @Override
    protected boolean hasCrushedOre() { return false; }

}
