package igteam.immersive_geology.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.SlurryEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class  MaterialMineralVanadinite extends MaterialBaseMineral {

    public  MaterialMineralVanadinite() {
        super("vanadinite");
        initializeColorMap((p) -> 0xEF2161);
    }

    @Override
    protected boolean hasSlag() {
        return true;
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xEF2161;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Reduction stage")
        {
            @Override
            protected void describe()
            {
                IRecipeBuilder.blasting(this).create(
                        "crushed_ore_" +getName() + "_to_slag_and_lead",
                        getParentMaterial().getItemTag(ItemPattern.crushed_ore),
                        MetalEnum.Lead.getStack(ItemPattern.ingot),
                        getParentMaterial().getStack(ItemPattern.slag)
                );
            }
        };
        new IGProcessingStage(this,"Chemical Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.chemical(this).create(
                    "chemical_recipe_" + getName() + "_to_" + MetalEnum.Vanadium.getName() + "_" + ItemPattern.compound_dust.getName(),
                    getStack(ItemPattern.slag),
                    new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 250),
                    new FluidTagInput(FluidEnum.Brine.getFluidTag(FluidPattern.fluid), 250),
                    MetalEnum.Vanadium.getStack(ItemPattern.compound_dust, 2),
                    new FluidStack(Fluids.WATER, 250), //to be fair, there should be solution of Na compound dust, but I'm to lazy
                    200,
                    10000
                );
                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Vanadium.getName() + "_to_metal_oxide",
                        MetalEnum.Vanadium.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Vanadium.getStack(ItemPattern.compound_dust),
                        200, 10000 );
                //TODO AF smelting with coal coke dust to get pure
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        // TODO Auto-generated method stub
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.LEAD, 5),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.VANADIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CHLORINE)
        ));
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.COMMON;
    }

}
