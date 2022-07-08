package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

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
                        51200);

                IRecipeBuilder.chemical(this).create(
                        "chemical_recipe_" + MetalEnum.Aluminum.getName() + "_to_cryolite_dust",
                        MetalEnum.Aluminum.getStack(ItemPattern.compound_dust, 1),
                        new FluidTagInput(FluidEnum.HydrofluoricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MineralEnum.Cryolite.getStack(ItemPattern.dust, 1),
                        new FluidStack(Fluids.EMPTY, 250),
                        200,
                        51200);


                //TODO in 1.18 -- chance based output for cryolite
                IRecipeBuilder.arcSmelting(this).create(
                        "dust_"+getName()+"_to_ingot",
                        new IngredientWithSize(getItemTag(ItemPattern.dust), 1),
                        MetalEnum.Aluminum.getStack(ItemPattern.ingot),
                        MineralEnum.Cryolite.getStack(ItemPattern.dust),
                        new IngredientWithSize(IETags.coalCokeDust, 1),
                        new IngredientWithSize(MineralEnum.Cryolite.getItemTag(ItemPattern.dust), 1))
                        .setEnergyTime(102400, 200);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(
            Arrays.asList(
                    new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM, 2),
                    new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
            )
        );
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Aluminum);

        return sources;
    }

    @Override
    public MaterialSourceWorld getDimension() {
        return MaterialSourceWorld.end;
    }
}
