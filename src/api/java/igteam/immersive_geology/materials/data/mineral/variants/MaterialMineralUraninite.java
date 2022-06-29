package igteam.immersive_geology.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.SlurryEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralUraninite extends MaterialBaseMineral {

    public  MaterialMineralUraninite() {
        super("uraninite");
        initializeColorMap((p) -> 0xB2BEB5);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this,"Extraction Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.decompose(this).create(
                        "crushed_ore" + getName() + "_to_compound_dust",
                        MetalEnum.Uranium.getStack(ItemPattern.compound_dust),
                        getStack(ItemPattern.crushed_ore),
                        300,153600);

                IRecipeBuilder.chemical(this).create(
                        "compound_dust_" + getName() + "_to_slury",
                        MetalEnum.Uranium.getStack(ItemPattern.compound_dust),
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidEnum.Brine.getFluidTag(FluidPattern.fluid), 125),
                        MetalEnum.Sodium.getStack(ItemPattern.compound_dust),
                        SlurryEnum.URANIUM.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 125),
                        100, 12800);

                IRecipeBuilder.chemical(this).create(
                        "slurry_" + getName() + "_to_" + MetalEnum.Uranium.getName() + "_" + ItemPattern.compound_dust.getName(),
                        ItemStack.EMPTY,
                        new FluidTagInput(SlurryEnum.URANIUM.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250),
                        new FluidTagInput(FluidEnum.SodiumHydroxide.getFluidTag(FluidPattern.fluid), 250),
                        MetalEnum.Uranium.getStack(ItemPattern.metal_oxide, 1),
                        FluidEnum.Brine.getFluidStack(FluidPattern.fluid, 250),
                        200,
                        51200
                );

                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + getName() + "_to_slury",
                        MetalEnum.Uranium.getStack(ItemPattern.metal_oxide),
                        new FluidTagInput(FluidEnum.HydrofluoricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 250),
                        ItemStack.EMPTY,
                        SlurryEnum.URANIUM.getType(FluidEnum.HydrofluoricAcid).getFluidStack(FluidPattern.slurry, 125),
                        100, 12800);

                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + getName() + "_to_slury",
                        new ItemStack(Items.BONE_MEAL, 1),
                        new FluidTagInput( SlurryEnum.URANIUM.getType(FluidEnum.HydrofluoricAcid).getFluidTag(FluidPattern.slurry), 125),
                        new FluidTagInput(FluidTags.WATER, 250),
                        MetalEnum.Uranium.getStack(ItemPattern.dust),
                        FluidStack.EMPTY, //just dump the waste
                        100, 12800);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        // TODO Auto-generated method stub
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.URANIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2)
        ));
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.UNCOMMON;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Uranium);

        return sources;
    }
}