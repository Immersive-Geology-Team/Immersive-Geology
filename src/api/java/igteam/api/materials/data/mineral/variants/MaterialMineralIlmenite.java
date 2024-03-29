package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.SlurryEnum;
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
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralIlmenite extends MaterialBaseMineral {

    public MaterialMineralIlmenite() {
        super("ilmenite");
        initializeColorMap((p) -> 0x4A3E3E);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }
    @Override
    public boolean hasDust() {
        return false;
    }

    @Override
    public boolean hasCrystal() { return false;}

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.refinement) {
            @Override
            protected void describe() {
                //Yes, this is oversimplification of becher process
                IRecipeBuilder.blasting(this).create(
                        "crushed_ore_" + getName() + "_to_ingot_and_oxide",
                        getItemTag(ItemPattern.crushed_ore),
                        MetalEnum.Iron.getStack(ItemPattern.ingot),
                        MetalEnum.Titanium.getStack(ItemPattern.metal_oxide));
            }
        };

        new IGProcessingStage(this, IGStageDesignation.purification) {
            @Override
            protected void describe() {
                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + MetalEnum.Titanium.getName() + "_to_slurry",
                        MetalEnum.Titanium.getItemTag(ItemPattern.metal_oxide), 1,
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 500),
                        new FluidTagInput(FluidTags.WATER, 125),
                        ItemStack.EMPTY,
                        SlurryEnum.TITANIUM.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200, 51200);

                IRecipeBuilder.chemical(this).create(
                        "slurry_"+getName() + "_to_dust",
                        MetalEnum.Sodium.getItemTag(ItemPattern.dust),4,
                        new FluidTagInput(SlurryEnum.TITANIUM.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Titanium.getStack(ItemPattern.dust, 1),
                        new FluidStack(FluidEnum.Brine.getFluid(FluidPattern.fluid), 500),
                        200, 51200);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TITANIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
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
        sources.add(MetalEnum.Iron);
        sources.add(MetalEnum.Titanium);

        return sources;
    }
}
