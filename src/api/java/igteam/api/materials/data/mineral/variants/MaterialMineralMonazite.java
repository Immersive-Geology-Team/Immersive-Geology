package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.SlurryEnum;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidFamily;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Rarity;

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
                        getItemTag(ItemFamily.crushed_ore),
                        getStack(ItemFamily.dust), 6000, 200);

                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_slurry",
                        getItemTag(ItemFamily.dust), 1,
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidFamily.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        MetalEnum.Thorium.getStack(ItemFamily.compound_dust),
                        SlurryEnum.NEODYMIUM.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidFamily.slurry, 250),
                        120, 10000);

                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" +MetalEnum.Thorium.getName() + "_to_metal_oxide",
                        MetalEnum.Thorium.getStack(ItemFamily.metal_oxide),
                        MetalEnum.Thorium.getItemTag(ItemFamily.compound_dust), 1,
                        300, 153600);

                IRecipeBuilder.chemical(this).create(
                        "slurry_" + MetalEnum.Neodymium.getName()+ "_to_compound_dust",
                        null, 0,
                        new FluidTagInput(SlurryEnum.NEODYMIUM.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidFamily.slurry), 250),
                        new FluidTagInput(FluidEnum.SodiumHydroxide.getFluidTag(FluidFamily.fluid), 250),
                        MetalEnum.Neodymium.getStack(ItemFamily.compound_dust),
                        FluidEnum.Brine.getFluidStack(FluidFamily.fluid, 250),
                        200, 51200);

                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" +MetalEnum.Neodymium.getName() + "_to_metal_oxide",
                        MetalEnum.Neodymium.getStack(ItemFamily.metal_oxide),
                        MetalEnum.Neodymium.getItemTag(ItemFamily.compound_dust), 1,
                        300, 153600);

            }
        };

        new IGProcessingStage(this,IGStageDesignation.purification) {
            @Override
            protected void describe() {
                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + MetalEnum.Neodymium.getName()+ "_slurry",
                        MetalEnum.Neodymium.getItemTag(ItemFamily.metal_oxide), 1,
                        new FluidTagInput(FluidTags.WATER, 250),
                        new FluidTagInput(FluidEnum.HydrofluoricAcid.getFluidTag(FluidFamily.fluid), 250),
                        ItemStack.EMPTY,
                        SlurryEnum.NEODYMIUM.getType(FluidEnum.HydrofluoricAcid).getFluidStack(FluidFamily.slurry, 250),
                        200, 51200);

                IRecipeBuilder.crystalize(this).create(
                        "slurry_" + SlurryEnum.NEODYMIUM.getType(FluidEnum.HydrofluoricAcid).getName() + "_to_crystal",
                        MetalEnum.Neodymium.getStack(ItemFamily.crystal),
                        SlurryEnum.NEODYMIUM.getType(FluidEnum.HydrofluoricAcid).getFluidTag(FluidFamily.slurry), 250,
                        300, 38400);


                IRecipeBuilder.arcSmelting(this).create("metal_oxide_"+getName() +"_to_dust",
                                MetalEnum.Thorium.getItemTag(ItemFamily.metal_oxide), 1,
                                MetalEnum.Thorium.getStack(ItemFamily.dust),null,
                                new IngredientWithSize( MetalEnum.Zinc.getItemTag(ItemFamily.dust), 1))
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
