package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.SlurryEnum;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidFamily;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralScheelite extends MaterialBaseMineral {

    public MaterialMineralScheelite() {
        super("scheelite");
        initializeColorMap((p) -> 0x32332E);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }


    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CALCIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TUNGSTEN),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
        ));
    }
    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        //TODO Break Stage Down More
        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create("crushed_ore_" + getName() + "_to_dust",
                        getItemTag(ItemFamily.crushed_ore),
                        getStack(ItemFamily.dust), 6000, 200);

                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_slurry",
                        getItemTag(ItemFamily.dust), 1,
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidFamily.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        MetalEnum.Tungsten.getStack(ItemFamily.compound_dust),
                        SlurryEnum.CALCIUM.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidFamily.slurry, 250),
                        200, 51200);

                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Tungsten.getName() + "_to_metal_oxide",
                        MetalEnum.Tungsten.getStack(ItemFamily.metal_oxide),
                        MetalEnum.Tungsten.getItemTag(ItemFamily.compound_dust), 1,
                        300, 153600);

                //To be fair, it should be dust, but I'd like not to bother here
                IRecipeBuilder.blasting(this).create(
                        "metal_oxide_" + MetalEnum.Tungsten.getName() + "_to_ingot",
                        MetalEnum.Tungsten.getItemTag(ItemFamily.metal_oxide),
                        MetalEnum.Tungsten.getStack(ItemFamily.ingot));

                IRecipeBuilder.chemical(this).create(
                        "slurry_" + MineralEnum.Gypsum.getName() + "_to_dust",
                        null, 0,
                        new FluidTagInput(SlurryEnum.CALCIUM.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidFamily.slurry), 250),
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidFamily.fluid), 250),
                        MineralEnum.Gypsum.getStack(ItemFamily.dust),
                        FluidEnum.HydrochloricAcid.getFluidStack(FluidFamily.fluid, 250),
                        200, 51200);
            }
        };
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Tungsten);

        return sources;
    }
}
