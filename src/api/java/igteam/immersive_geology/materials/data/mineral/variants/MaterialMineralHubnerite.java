package igteam.immersive_geology.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.SlurryEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGStageDesignation;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralHubnerite extends MaterialBaseMineral {

    public MaterialMineralHubnerite() {
        super("hubnerite");
        initializeColorMap((p) -> 0x32332E);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this,"Extraction Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create( "crushed_ore_" +getName() + "_to_dust",
                        getItemTag(ItemPattern.crushed_ore),
                        getStack(ItemPattern.dust), 10000, 200);

                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_slurry",
                        getStack(ItemPattern.dust),
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        MetalEnum.Tungsten.getStack(ItemPattern.compound_dust),
                        SlurryEnum.MANGANESE.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200, 51200);

                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Tungsten.getName() + "_to_metal_oxide",
                        MetalEnum.Tungsten.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Tungsten.getStack(ItemPattern.compound_dust),
                        300, 153600);

                //To be fair, it should be dust, but I'd like not to bother here
                IRecipeBuilder.blasting(this).create(
                        "metal_oxide_" + MetalEnum.Tungsten.getName() + "_to_ingot",
                        MetalEnum.Tungsten.getItemTag(ItemPattern.metal_oxide),
                        MetalEnum.Tungsten.getStack(ItemPattern.ingot));
            }
        };

        new IGProcessingStage(this, IGStageDesignation.refinement) {
            @Override
            protected void describe() {

                IRecipeBuilder.chemical(this).create(
                        "slurry_" +  SlurryEnum.MANGANESE.getType(FluidEnum.HydrochloricAcid).getName() + "_to_slurry",
                        ItemStack.EMPTY,
                        new FluidTagInput(SlurryEnum.MANGANESE.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250),
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 250),
                        ItemStack.EMPTY,
                        SlurryEnum.MANGANESE.getType(FluidEnum.SulfuricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200, 51200);

                IRecipeBuilder.crystalize(this).create(
                        "slurry" + SlurryEnum.MANGANESE.getType(FluidEnum.SulfuricAcid).getName() + "_to_crystal",
                        MetalEnum.Manganese.getStack(ItemPattern.crystal),
                        new FluidTagInput(SlurryEnum.MANGANESE.getType(FluidEnum.SulfuricAcid).getFluidTag(FluidPattern.slurry), 250),
                        300, 38400);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.MANGANESE),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TUNGSTEN),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
        ));
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.RARE;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Manganese);
        sources.add(MetalEnum.Tungsten);

        return sources;
    }

}
