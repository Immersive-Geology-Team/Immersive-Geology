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
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralSmithsonite extends MaterialBaseMineral {

    public MaterialMineralSmithsonite() {
        super("smithsonite");
        initializeColorMap((p) -> 0x81D1DC);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this,"Extraction Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.decompose(this).create(
                        "crushed_ore_" + getName() + "_to_metal_oxide",
                        MetalEnum.Zinc.getStack(ItemPattern.metal_oxide),
                        getStack(ItemPattern.crushed_ore),
                        300,12000);

                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + MetalEnum.Zinc.getName() + "_to_slurry",
                        MetalEnum.Zinc.getStack(ItemPattern.metal_oxide),
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        ItemStack.EMPTY,
                        SlurryEnum.ZINC.getType(FluidEnum.SulfuricAcid).getFluidStack(FluidPattern.slurry, 250),
                        120, 10000);


                IRecipeBuilder.crystalize(this).create(
                        "slurry_" + SlurryEnum.ZINC.getType(FluidEnum.SulfuricAcid).getName() + "_to_crystal",
                        MetalEnum.Zinc.getStack(ItemPattern.crystal),
                        new FluidTagInput(SlurryEnum.ZINC.getType(FluidEnum.SulfuricAcid).getFluidTag(FluidPattern.slurry), 250),
                        300,12000);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ZINC),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CARBON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
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
        sources.add(MetalEnum.Zinc);

        return sources;
    }
}
