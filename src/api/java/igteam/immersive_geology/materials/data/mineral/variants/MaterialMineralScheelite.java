package igteam.immersive_geology.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.MineralEnum;
import igteam.immersive_geology.materials.SlurryEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
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

        new IGProcessingStage(this, "Extraction Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create("crushed_ore_" + getName() + "_to_dust",
                        getItemTag(ItemPattern.crushed_ore),
                        getStack(ItemPattern.dust), 10000, 200);

                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_slurry",
                        getStack(ItemPattern.dust),
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        MetalEnum.Tungsten.getStack(ItemPattern.compound_dust),
                        SlurryEnum.CALCIUM.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
                        120, 10000);

                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Tungsten.getName() + "_to_metal_oxide",
                        MetalEnum.Tungsten.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Tungsten.getStack(ItemPattern.compound_dust),
                        200, 10000);

                //To be fair, it should be dust, but I'd like not to bother here
                IRecipeBuilder.blasting(this).create(
                        "metal_oxide_" + MetalEnum.Tungsten.getName() + "_to_ingot",
                        MetalEnum.Tungsten.getItemTag(ItemPattern.metal_oxide),
                        MetalEnum.Tungsten.getStack(ItemPattern.ingot));

                IRecipeBuilder.chemical(this).create(
                        "slurry_" + MineralEnum.Gypsum.getName() + "_to_dust",
                        ItemStack.EMPTY,
                        new FluidTagInput(SlurryEnum.CALCIUM.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250),
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 250),
                        MineralEnum.Gypsum.getStack(ItemPattern.dust),
                        FluidEnum.HydrochloricAcid.getFluidStack(FluidPattern.fluid, 250),
                        120, 10000);
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
