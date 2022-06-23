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
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;

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

        new IGProcessingStage(this,"Roasting Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.roast(this).create("mineral_" + getName() + "_to_slag",
                        getParentMaterial().getStack(ItemPattern.crushed_ore), getParentMaterial().getStack(ItemPattern.slag),1000, 1);
            }
        };
        new IGProcessingStage(this,"Blasting Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.blasting(this).create(
                        "slag_" + getName() + "_to_metal",
                        getParentMaterial().getItemTag(ItemPattern.slag),
                        MetalEnum.Silver.getStack(ItemPattern.ingot));
            }
        };
        new IGProcessingStage(this,"Leeching Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create( "slag_ore_" +getName() + "_to_dust",
                        getItemTag(ItemPattern.slag),
                        getStack(ItemPattern.dust), 10000, 200);

            IRecipeBuilder.chemical(this).create(
            "dust_" + getName() + "_to_slurry",
                    getStack(ItemPattern.dust),
                    new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 250),
                    new FluidTagInput(FluidTags.WATER, 250),
                    MetalEnum.Platinum.getStack(ItemPattern.compound_dust),
                    SlurryEnum.SILVER.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
            200, 51200);

                IRecipeBuilder.crystalize(this).create(
                        "slurry" + SlurryEnum.SILVER.getType(FluidEnum.HydrochloricAcid).getName() + "_to_crystal",
                        MetalEnum.Silver.getStack(ItemPattern.crystal),
                        new FluidTagInput(SlurryEnum.SILVER.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250),
                        120, 10000);
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
        sources.add(MetalEnum.Silver);
        sources.add(MetalEnum.Platinum);
        sources.add(MetalEnum.Osmium);

        return sources;
    }
}