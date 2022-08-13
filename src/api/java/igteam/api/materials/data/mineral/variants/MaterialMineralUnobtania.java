package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.SlurryEnum;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;

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

        new IGProcessingStage(this,"Extraction Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create( "slag_ore_" +getName() + "_to_dust",
                        getItemTag(ItemPattern.crushed_ore),
                        getStack(ItemPattern.dust), 3000, 200);

                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_" + MetalEnum.Vanadium.getName() + "_" + ItemPattern.compound_dust.getName(),
                        getItemTag(ItemPattern.dust), 1,
                        new FluidTagInput(FluidEnum.SodiumHydroxide.getFluidTag(FluidPattern.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        MetalEnum.Vanadium.getStack(ItemPattern.compound_dust, 2),
                        SlurryEnum.UNOBTANIUM.getType(FluidEnum.SodiumHydroxide).getFluidStack(FluidPattern.slurry, 250),
                        200,
                        51200
                );


                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Vanadium.getName() + "_to_metal_oxide",
                        MetalEnum.Vanadium.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Vanadium.getItemTag(ItemPattern.compound_dust), 1,
                        300, 153600 );

                IRecipeBuilder.arcSmelting(this).create("metal_oxide_"+getName() +"_to_dust",
                                MetalEnum.Vanadium.getItemTag(ItemPattern.metal_oxide), 1,
                                MetalEnum.Vanadium.getStack(ItemPattern.ingot),null,
                                new IngredientWithSize(IETags.coalCokeDust, 1))
                        .setEnergyTime(102400, 200);

                IRecipeBuilder.chemical(this).create(
                        "slurry_" + getName() + "_to_" + MetalEnum.Unobtanium.getName() + "_" + ItemPattern.compound_dust.getName(),
                        null, 0,
                        new FluidTagInput(SlurryEnum.UNOBTANIUM.getType(FluidEnum.SodiumHydroxide).getFluidTag(FluidPattern.slurry), 250),
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 250),
                        MetalEnum.Unobtanium.getStack(ItemPattern.compound_dust, 1),
                      FluidEnum.Brine.getFluidStack(FluidPattern.fluid, 250),
                        200,
                        51200
                );

                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Unobtanium.getName() + "_to_metal_oxide",
                        MetalEnum.Unobtanium.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Unobtanium.getItemTag(ItemPattern.compound_dust), 1,
                        300, 153600 );

                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + MetalEnum.Unobtanium.getName() + "_to_slurry",
                        MetalEnum.Unobtanium.getItemTag(ItemPattern.metal_oxide), 1,
                        new FluidTagInput(FluidEnum.NitricAcid.getFluidTag(FluidPattern.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        ItemStack.EMPTY,
                        SlurryEnum.UNOBTANIUM.getType(FluidEnum.NitricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200, 51200);

                IRecipeBuilder.crystalize(this).create(
                        "slurry" + SlurryEnum.UNOBTANIUM.getType(FluidEnum.NitricAcid).getName() + "_to_crystal",
                        MetalEnum.Unobtanium.getStack(ItemPattern.crystal),
                        SlurryEnum.UNOBTANIUM.getType(FluidEnum.NitricAcid).getFluidTag(FluidPattern.slurry), 250,
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
