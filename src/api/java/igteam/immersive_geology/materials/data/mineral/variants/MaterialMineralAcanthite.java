package igteam.immersive_geology.materials.data.mineral.variants;

import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

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
                //FIXME blasting process
                //IRecipeBuilder.blast(this).create("slag_" + getName() + "_to_metal", getParentMaterial().getStack(ItemPattern.slag),
                //        MetalEnum.Silver.getStack(ItemPattern.ingot),1000, 1);
            }
        };
        new IGProcessingStage(this,"Leeching Stage") {
            @Override
            protected void describe() {
            //FIXME get info from Muddy how to create vat recipe with water
            //IRecipeBuilder.chemical(this).create("slag_" + getName() + "_to_slurry", getParentMaterial().getStack(ItemPattern.slag),
            //          FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), Fluids.WATER.getFluid(), MetalEnum.Platinum.getItemTag(ItemPattern.compound_dust),
            //                        120, 10000);

            // IRecipeBuilder.separating(this) PT compound dust to Os and Pt dust

                //IRecipeBuilder.crystalize(this) Ag sulfide to Ag crystals
            }
        };
    }

}