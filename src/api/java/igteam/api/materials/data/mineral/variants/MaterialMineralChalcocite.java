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
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralChalcocite extends MaterialBaseMineral {

    public MaterialMineralChalcocite() {
        super("chalcocite");
        initializeColorMap((p) -> 0x3D5E67);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(
                Arrays.asList(
                        new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR),
                        new PeriodicTableElement.ElementProportion(PeriodicTableElement.COPPER, 2)
                )
        );
    }

    @Override
    public boolean hasSlag() {
        return true;
    }



    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Roasting Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.roast(this).create("mineral_" + getName() + "_to_slag",
                        getParentMaterial().getItemTag(ItemPattern.crushed_ore), 1, getParentMaterial().getStack(ItemPattern.slag), 1000, 1);

            }
        };
        new IGProcessingStage(this, "Blasting Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.blasting(this).create(
                        "slag_" + getName() + "_to_metal",
                        getParentMaterial().getItemTag(ItemPattern.slag),
                        MetalEnum.Copper.getStack(ItemPattern.ingot, 1));
            }
        };

        new IGProcessingStage(this, "Leeching Stage") {
            @Override
            protected void describe() {

                IRecipeBuilder.crushing(this).create( "slag_ore_" +getName() + "_to_dust",
                        getItemTag(ItemPattern.slag),
                        getStack(ItemPattern.dust), 3000, 200);

                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_slurry",
                        getItemTag(ItemPattern.dust), 1,
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        MetalEnum.Platinum.getStack(ItemPattern.compound_dust),
                        SlurryEnum.COPPER.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200, 51200);

                IRecipeBuilder.crystalize(this).create(
                        "slurry" + SlurryEnum.COPPER.getType(FluidEnum.HydrochloricAcid).getName() + "_to_crystal",
                        MetalEnum.Copper.getStack(ItemPattern.crystal),
                        SlurryEnum.COPPER.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250,
                        300, 38400);

                IRecipeBuilder.separating(this).create(
                        MetalEnum.Platinum.getItemTag(ItemPattern.compound_dust),
                        MetalEnum.Platinum.getStack(ItemPattern.dust),
                        MetalEnum.Osmium.getStack(ItemPattern.dust));
            }
        };
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Copper);
        sources.add(MetalEnum.Platinum);
        sources.add(MetalEnum.Osmium);

        return sources;
    }
}