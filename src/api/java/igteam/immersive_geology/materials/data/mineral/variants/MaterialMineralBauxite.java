package igteam.immersive_geology.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.common.IEContent;
import igteam.immersive_geology.main.IGRegistryProvider;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.MineralEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGStageDesignation;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralBauxite extends MaterialBaseMineral {
    public MaterialMineralBauxite() {
        super("bauxite");
        initializeColorMap((p) -> 0x999FAF);
    }

    @Override
    public boolean hasDust() {
        return false;
    }

    @Override
    public boolean hasCrystal() { return false;}

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.purification) {
            @Override
            protected void describe() {
                //let's skip Bayer process and tell that we have pure gibbsite here?
                IRecipeBuilder.decompose(this).create(
                        "crushed_ore_" + getName() + "_to_oxide",
                        MetalEnum.Aluminum.getStack(ItemPattern.metal_oxide),
                        getStack(ItemPattern.crushed_ore), 10000, 200);

                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + getName() + "_to_compound_dust" ,
                        MetalEnum.Aluminum.getStack(ItemPattern.metal_oxide),
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
                                new IngredientWithSize(MetalEnum.Aluminum.getItemTag(ItemPattern.metal_oxide), 1),
                                MetalEnum.Aluminum.getStack(ItemPattern.ingot),
                                MineralEnum.Cryolite.getStack(ItemPattern.dust),
                                new IngredientWithSize(IETags.coalCokeDust, 1),
                                new IngredientWithSize(MineralEnum.Cryolite.getItemTag(ItemPattern.dust), 1))
                        .setEnergyTime(50000, 100);

            }
        };
    }
    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(
                Arrays.asList(
                        new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM, 1),
                        new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3),
                        new PeriodicTableElement.ElementProportion(PeriodicTableElement.HYDROGEN, 3)
                )
        );
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Aluminum);

        return sources;
    }

}
