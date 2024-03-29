package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.SlurryEnum;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralMonazite extends MaterialBaseMineral {

    public MaterialMineralMonazite() {
        super("monazite");
        initializeColorMap((p) -> 0xC21E56);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.refinement) {
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create( "crushed_ore_" +getName() + "_to_dust",
                        getItemTag(ItemPattern.crushed_ore),
                        getStack(ItemPattern.dust), 6000, 200);

                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_slurry",
                        getItemTag(ItemPattern.dust), 1,
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        MetalEnum.Thorium.getStack(ItemPattern.compound_dust),
                        SlurryEnum.NEODYMIUM.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
                        120, 10000);

                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" +MetalEnum.Thorium.getName() + "_to_metal_oxide",
                        MetalEnum.Thorium.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Thorium.getItemTag(ItemPattern.compound_dust), 1,
                        300, 153600);

                IRecipeBuilder.chemical(this).create(
                        "slurry_" + MetalEnum.Neodymium.getName()+ "_to_compound_dust",
                        null, 0,
                        new FluidTagInput(SlurryEnum.NEODYMIUM.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250),
                        new FluidTagInput(FluidEnum.SodiumHydroxide.getFluidTag(FluidPattern.fluid), 250),
                        MetalEnum.Neodymium.getStack(ItemPattern.compound_dust),
                        FluidEnum.Brine.getFluidStack(FluidPattern.fluid, 250),
                        200, 51200);

                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" +MetalEnum.Neodymium.getName() + "_to_metal_oxide",
                        MetalEnum.Neodymium.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Neodymium.getItemTag(ItemPattern.compound_dust), 1,
                        300, 153600);

            }
        };

        new IGProcessingStage(this,IGStageDesignation.purification) {
            @Override
            protected void describe() {
                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + MetalEnum.Neodymium.getName()+ "_slurry",
                        MetalEnum.Neodymium.getItemTag(ItemPattern.metal_oxide), 1,
                        new FluidTagInput(FluidTags.WATER, 250),
                        new FluidTagInput(FluidEnum.HydrofluoricAcid.getFluidTag(FluidPattern.fluid), 250),
                        ItemStack.EMPTY,
                        SlurryEnum.NEODYMIUM.getType(FluidEnum.HydrofluoricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200, 51200);

                IRecipeBuilder.crystalize(this).create(
                        "slurry_" + SlurryEnum.NEODYMIUM.getType(FluidEnum.HydrofluoricAcid).getName() + "_to_crystal",
                        MetalEnum.Neodymium.getStack(ItemPattern.crystal),
                        SlurryEnum.NEODYMIUM.getType(FluidEnum.HydrofluoricAcid).getFluidTag(FluidPattern.slurry), 250,
                        300, 38400);


                IRecipeBuilder.arcSmelting(this).create("metal_oxide_"+getName() +"_to_dust",
                                MetalEnum.Thorium.getItemTag(ItemPattern.metal_oxide), 1,
                                MetalEnum.Thorium.getStack(ItemPattern.dust),null,
                                new IngredientWithSize( MetalEnum.Zinc.getItemTag(ItemPattern.dust), 1))
                        .setEnergyTime(102400, 200);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(
                Arrays.asList(
                        new PeriodicTableElement.ElementProportion(PeriodicTableElement.THORIUM),
                        new PeriodicTableElement.ElementProportion(PeriodicTableElement.NEODYMIUM),
                        new PeriodicTableElement.ElementProportion(PeriodicTableElement.PHOSPHORUS),
                        new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
                )
        );
    }

    @Override
    public Rarity getRarity()
    {
        // TODO Auto-generated method stub
        return Rarity.COMMON;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Neodymium);
        sources.add(MetalEnum.Thorium);
        return sources;
    }

    @Override
    public MaterialSourceWorld getDimension() {
        return MaterialSourceWorld.end;
    }
}
