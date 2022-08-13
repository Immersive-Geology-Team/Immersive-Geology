package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.StoneEnum;
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
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
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
    public boolean hasOreBlock() {return false;}
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

        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {

                ITag<Item> t = ItemTags.makeWrapperTag("forge:ores/aluminum");

                IRecipeBuilder.cutting(this).create(
                        t,
                        new FluidTagInput(FluidTags.WATER, 80),
                        StoneEnum.Stone.getStack(ItemPattern.ore_chunk, getParentMaterial(), 5)

                );
            }
        };

        new IGProcessingStage(this, IGStageDesignation.purification) {
            @Override
            protected void describe() {
                //let's skip Bayer process and tell that we have pure gibbsite here?
                IRecipeBuilder.decompose(this).create(
                        "crushed_ore_" + getName() + "_to_oxide",
                        MetalEnum.Aluminum.getStack(ItemPattern.metal_oxide),
                        getItemTag(ItemPattern.crushed_ore), 1, 300, 153600);

                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + getName() + "_to_compound_dust" ,
                        MetalEnum.Aluminum.getItemTag(ItemPattern.metal_oxide), 1,
                        new FluidTagInput(FluidEnum.SodiumHydroxide.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Aluminum.getStack(ItemPattern.compound_dust, 1),
                        new FluidStack(Fluids.EMPTY, 250),
                        200,
                        51200);

                IRecipeBuilder.chemical(this).create(
                        "chemical_recipe_" + MetalEnum.Aluminum.getName() + "_to_cryolite_dust",
                        MetalEnum.Aluminum.getItemTag(ItemPattern.compound_dust), 1,
                        new FluidTagInput(FluidEnum.HydrofluoricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MineralEnum.Cryolite.getStack(ItemPattern.dust, 1),
                        new FluidStack(Fluids.EMPTY, 250),
                        200,
                        51200);


                //TODO in 1.18 -- chance based output for cryolite
                IRecipeBuilder.arcSmelting(this).create(
                                "metal_oxide_"+MetalEnum.Aluminum.getName()+"_to_ingot",
                                MetalEnum.Aluminum.getItemTag(ItemPattern.metal_oxide), 1,
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
