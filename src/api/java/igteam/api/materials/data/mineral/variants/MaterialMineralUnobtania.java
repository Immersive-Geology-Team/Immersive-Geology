package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralUnobtania extends MaterialBaseMineral {
    public  MaterialMineralUnobtania() {
        super("unobtania");
        initializeColorMap((p) -> 0x999FAF);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TRICLINIC;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        //TODO Please dear god, this stage needs to be broken down
        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create( "slag_ore_" +getName() + "_to_dust",
                        getItemTag(ItemFamily.crushed_ore),
                        getStack(ItemFamily.dust), 3000, 200);

                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_" + MetalEnum.Vanadium.getName() + "_" + ItemFamily.compound_dust.getName(),
                        getItemTag(ItemFamily.dust), 1,
                        new FluidTagInput(FluidEnum.SodiumHydroxide.getFluidTag(FluidFamily.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        MetalEnum.Vanadium.getStack(ItemFamily.compound_dust, 2),
                        SlurryEnum.UNOBTANIUM.getType(FluidEnum.SodiumHydroxide).getFluidStack(FluidFamily.slurry, 250),
                        200,
                        51200
                );


                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Vanadium.getName() + "_to_metal_oxide",
                        MetalEnum.Vanadium.getStack(ItemFamily.metal_oxide),
                        MetalEnum.Vanadium.getItemTag(ItemFamily.compound_dust), 1,
                        300, 153600 );

                IRecipeBuilder.arcSmelting(this).create("metal_oxide_"+getName() +"_to_dust",
                                MetalEnum.Vanadium.getItemTag(ItemFamily.metal_oxide), 1,
                                MetalEnum.Vanadium.getStack(ItemFamily.ingot),null,
                                new IngredientWithSize(IETags.coalCokeDust, 1))
                        .setEnergyTime(102400, 200);

                IRecipeBuilder.chemical(this).create(
                        "slurry_" + getName() + "_to_" + MetalEnum.Unobtanium.getName() + "_" + ItemFamily.compound_dust.getName(),
                        null, 0,
                        new FluidTagInput(SlurryEnum.UNOBTANIUM.getType(FluidEnum.SodiumHydroxide).getFluidTag(FluidFamily.slurry), 250),
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidFamily.fluid), 250),
                        MetalEnum.Unobtanium.getStack(ItemFamily.compound_dust, 1),
                      FluidEnum.Brine.getFluidStack(FluidFamily.fluid, 250),
                        200,
                        51200
                );

                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Unobtanium.getName() + "_to_metal_oxide",
                        MetalEnum.Unobtanium.getStack(ItemFamily.metal_oxide),
                        MetalEnum.Unobtanium.getItemTag(ItemFamily.compound_dust), 1,
                        300, 153600 );

                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + MetalEnum.Unobtanium.getName() + "_to_slurry",
                        MetalEnum.Unobtanium.getItemTag(ItemFamily.metal_oxide), 1,
                        new FluidTagInput(FluidEnum.NitricAcid.getFluidTag(FluidFamily.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        ItemStack.EMPTY,
                        SlurryEnum.UNOBTANIUM.getType(FluidEnum.NitricAcid).getFluidStack(FluidFamily.slurry, 250),
                        200, 51200);

                IRecipeBuilder.crystalize(this).create(
                        "slurry" + SlurryEnum.UNOBTANIUM.getType(FluidEnum.NitricAcid).getName() + "_to_crystal",
                        MetalEnum.Unobtanium.getStack(ItemFamily.crystal),
                        SlurryEnum.UNOBTANIUM.getType(FluidEnum.NitricAcid).getFluidTag(FluidFamily.slurry), 250,
                        300, 38400);
            }

        };
    }


    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        // TODO Auto-generated method stub
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.UNOBTANIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.VANADIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
        ));
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.EPIC;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Unobtanium);
        sources.add(MetalEnum.Vanadium);

        return sources;
    }
}
