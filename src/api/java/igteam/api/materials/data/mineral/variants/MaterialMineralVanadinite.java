package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Rarity;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

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
                IRecipeBuilder.crushing(this).create( "slag_ore_" +getName() + "_to_dust",
                        getItemTag(ItemPattern.slag),
                        getStack(ItemPattern.dust), 3000, 200);

                IRecipeBuilder.chemical(this).create(
                    "dust_" + getName() + "_to_" + MetalEnum.Vanadium.getName() + "_" + ItemPattern.compound_dust.getName(),
                    getStack(ItemPattern.dust),
                    new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 250),
                    new FluidTagInput(FluidEnum.Brine.getFluidTag(FluidPattern.fluid), 250),
                    MetalEnum.Vanadium.getStack(ItemPattern.compound_dust, 2),
                    new FluidStack(Fluids.WATER, 250), //to be fair, there should be solution of Na compound dust, but I'm to lazy
                    200,
                    51200
                );
                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Vanadium.getName() + "_to_metal_oxide",
                        MetalEnum.Vanadium.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Vanadium.getStack(ItemPattern.compound_dust),
                        300, 153600 );

                IRecipeBuilder.arcSmelting(this).create("metal_oxide_"+getName() +"_to_dust",
                                new IngredientWithSize(MetalEnum.Vanadium.getItemTag(ItemPattern.metal_oxide), 1),
                                MetalEnum.Vanadium.getStack(ItemPattern.ingot),null,
                                new IngredientWithSize(IETags.coalCokeDust, 1))
                        .setEnergyTime(102400, 200);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
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

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Lead);
        sources.add(MetalEnum.Vanadium);

        return sources;
    }
}
