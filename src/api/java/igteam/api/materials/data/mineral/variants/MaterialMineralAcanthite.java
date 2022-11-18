package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
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
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralAcanthite  extends MaterialBaseMineral {

    public MaterialMineralAcanthite() {
        super("acanthite");
        initializeColorMap((p) -> 0x343432);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(
            Arrays.asList(
                    new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR),
                    new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILVER, 2)
            )
        );
    }

    @Override
    public boolean hasSlag() {return true;}

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.roasting) {
            @Override
            protected void describe() {
                IRecipeBuilder.roast(this).create("mineral_" + getName() + "_to_slag",
                        getParentMaterial().getItemTag(ItemFamily.crushed_ore), 1, getParentMaterial().getStack(ItemFamily.slag),1000, 1);
            }
        };
        new IGProcessingStage(this,IGStageDesignation.blasting) {
            @Override
            protected void describe() {
                IRecipeBuilder.blasting(this).create(
                        "slag_" + getName() + "_to_metal",
                        getParentMaterial().getItemTag(ItemFamily.slag),
                        MetalEnum.Silver.getStack(ItemFamily.ingot));
            }
        };
        new IGProcessingStage(this,IGStageDesignation.leeching) {
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create( "slag_ore_" +getName() + "_to_dust",
                        getItemTag(ItemFamily.slag),
                        getStack(ItemFamily.dust), 3000, 200);

            IRecipeBuilder.chemical(this).create(
            "dust_" + getName() + "_to_slurry",
                    getItemTag(ItemFamily.dust), 1,
                    new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidFamily.fluid), 250),
                    new FluidTagInput(FluidTags.WATER, 250),
                    MetalEnum.Platinum.getStack(ItemFamily.compound_dust),
                    SlurryEnum.SILVER.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidFamily.slurry, 250),
            200, 51200);

                IRecipeBuilder.crystalize(this).create(
                        "slurry" + SlurryEnum.SILVER.getType(FluidEnum.HydrochloricAcid).getName() + "_to_crystal",
                        MetalEnum.Silver.getStack(ItemFamily.crystal),
                        SlurryEnum.SILVER.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidFamily.slurry), 250,
                        300, 38400);
                IRecipeBuilder.separating(this).create(
                        MetalEnum.Platinum.getItemTag(ItemFamily.compound_dust),
                        MetalEnum.Platinum.getStack(ItemFamily.dust),
                        MetalEnum.Osmium.getStack(ItemFamily.dust));
            }
        };
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Silver);
        sources.add(MetalEnum.Platinum);
        sources.add(MetalEnum.Osmium);

        return sources;
    }
}