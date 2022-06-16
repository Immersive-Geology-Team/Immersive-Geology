package igteam.immersive_geology.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralRockSalt extends MaterialBaseMineral {

    public MaterialMineralRockSalt() {
        super("rocksalt");
        initializeColorMap((p) -> 0xffffff);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Brine and hydroxide production stage")
        {
            @Override
            protected void describe(){
                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_brine",
                        getStack(ItemPattern.dust, 2),
                        new FluidTagInput(FluidTags.WATER, 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        ItemStack.EMPTY,
                        new FluidStack(FluidEnum.Brine.getFluid(FluidPattern.fluid), 250),
                        200, 10000);

                IRecipeBuilder.chemical(this).create(
                        "sodium_oxide_to_hydroxide",
                        MetalEnum.Sodium.getStack(ItemPattern.metal_oxide, 2),
                        new FluidTagInput(FluidTags.WATER, 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        ItemStack.EMPTY,
                        new FluidStack(FluidEnum.SodiumHydroxide.getFluid(FluidPattern.fluid), 250),
                        240, 12000);

            }
        };

        new IGProcessingStage(this,"Hydrochloric production stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_acid_and_salt",
                        getItemTag(ItemPattern.dust), 1,
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Sodium.getStack(ItemPattern.compound_dust),
                        new FluidStack(FluidEnum.HydrochloricAcid.getFluid(FluidPattern.fluid), 125),
                        240, 12000);

                IRecipeBuilder.chemical(this).create(
                        "brine_to_acid_and_salt",
                        ItemStack.EMPTY,
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidEnum.Brine.getFluidTag(FluidPattern.fluid), 125),
                        MetalEnum.Sodium.getStack(ItemPattern.compound_dust),
                        new FluidStack(FluidEnum.HydrochloricAcid.getFluid(FluidPattern.fluid), 125),
                        240, 12000);
            }
        };
        new IGProcessingStage(this,"Sodium extraction stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Sodium.getName() + "_to_metal_oxide",
                        MetalEnum.Sodium.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Sodium.getStack(ItemPattern.compound_dust),
                        300,12000);

                IRecipeBuilder.arcSmelting(this).create("metal_oxide_"+getName() +"_to_dust",
                                new IngredientWithSize(MetalEnum.Sodium.getItemTag(ItemPattern.metal_oxide), 1),
                                MetalEnum.Sodium.getStack(ItemPattern.dust),null,
                                new IngredientWithSize(IETags.coalCokeDust, 1))
                        .setEnergyTime(120000, 100);
            }
        };

    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SODIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CHLORINE)
        )
        );
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.COMMON;
    }

}
