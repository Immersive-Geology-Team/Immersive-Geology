package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.SlurryEnum;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.processing.helper.IRecipeBuilder;
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
    public boolean hasDust() {
        return false;
    }

    @Override
    public boolean hasCrystal() { return false;}

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {
                IRecipeBuilder.decompose(this).create(
                        "crushed_ore_" + getName() + "_to_metal_oxide",
                        MetalEnum.Zinc.getStack(ItemPattern.metal_oxide),
                        getItemTag(ItemPattern.crushed_ore), 1,
                        300,153600);

                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + MetalEnum.Zinc.getName() + "_to_slurry",
                        MetalEnum.Zinc.getItemTag(ItemPattern.metal_oxide), 1,
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        ItemStack.EMPTY,
                        SlurryEnum.ZINC.getType(FluidEnum.SulfuricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200, 51200);


                IRecipeBuilder.crystalize(this).create(
                        "slurry_" + SlurryEnum.ZINC.getType(FluidEnum.SulfuricAcid).getName() + "_to_crystal",
                        MetalEnum.Zinc.getStack(ItemPattern.crystal),
                        SlurryEnum.ZINC.getType(FluidEnum.SulfuricAcid).getFluidTag(FluidPattern.slurry), 250,
                        300,38400);
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
