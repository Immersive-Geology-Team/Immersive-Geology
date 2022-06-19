package igteam.immersive_geology.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.MineralEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.helper.PeriodicTableElement.ElementProportion;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGStageDesignation;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralAlumina extends MaterialBaseMineral {

    public MaterialMineralAlumina() {
        super("alumina");
        initializeColorMap((p) -> 0x999FAF);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.purification) {
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create(
                        "crushed_ore_" +getName() + "_to_dust",
                        getItemTag(ItemPattern.crushed_ore),
                        getStack(ItemPattern.dust),
                        10000, 100);

                //Hall-Heroult proccess bitches!
                IRecipeBuilder.chemical(this).create(
                        "chemical_recipe_dust" + getName() + "_to_compound_dust" ,
                        getStack(ItemPattern.dust),
                        new FluidTagInput(FluidEnum.SodiumHydroxide.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Aluminum.getStack(ItemPattern.compound_dust, 1),
                        new FluidStack(Fluids.EMPTY, 250),
                        200,
                        10000);

                IRecipeBuilder.chemical(this).create(
                        "chemical_recipe_" + MetalEnum.Aluminum.getName() + "_to_cryolite_dust",
                        MetalEnum.Aluminum.getStack(ItemPattern.compound_dust, 1),
                        new FluidTagInput(FluidEnum.HydrofluoricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MineralEnum.Cryolite.getStack(ItemPattern.dust, 1),
                        new FluidStack(Fluids.EMPTY, 250),
                        200,
                        10000);


                //TODO in 1.18 -- chance based output for cryolite
                IRecipeBuilder.arcSmelting(this).create(
                        "dust_"+getName()+"_to_ingot",
                        new IngredientWithSize(getItemTag(ItemPattern.dust), 1),
                        MetalEnum.Aluminum.getStack(ItemPattern.ingot),
                        MineralEnum.Cryolite.getStack(ItemPattern.dust),
                        new IngredientWithSize(IETags.coalCokeDust, 1),
                        new IngredientWithSize(MineralEnum.Cryolite.getItemTag(ItemPattern.dust), 1))
                        .setEnergyTime(50000, 100);
            }
        };
    }

    @Override
    public LinkedHashSet<ElementProportion> getElements()
    {
        return new LinkedHashSet<>(
            Arrays.asList(
                    new ElementProportion(PeriodicTableElement.ALUMINIUM, 2),
                    new ElementProportion(PeriodicTableElement.OXYGEN, 3)
            )
        );
    }
}
