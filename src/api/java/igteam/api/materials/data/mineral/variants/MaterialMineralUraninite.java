package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
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
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Rarity;
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

        //TODO Break down this stage
        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {
                IRecipeBuilder.decompose(this).create(
                        "crushed_ore" + getName() + "_to_compound_dust",
                        MetalEnum.Uranium.getStack(ItemFamily.compound_dust),
                        getItemTag(ItemFamily.crushed_ore), 1,
                        300,153600);

                IRecipeBuilder.chemical(this).create(
                        "compound_dust_" + getName() + "_to_slury",
                        MetalEnum.Uranium.getStack(ItemFamily.compound_dust),
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidFamily.fluid), 125),
                        new FluidTagInput(FluidEnum.Brine.getFluidTag(FluidFamily.fluid), 125),
                        MetalEnum.Sodium.getStack(ItemFamily.compound_dust),
                        SlurryEnum.URANIUM.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidFamily.slurry, 125),
                        100, 12800);

                IRecipeBuilder.chemical(this).create(
                        "slurry_" + getName() + "_to_" + MetalEnum.Uranium.getName() + "_" + ItemFamily.compound_dust.getName(),
                        ItemStack.EMPTY,
                        new FluidTagInput(SlurryEnum.URANIUM.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidFamily.slurry), 250),
                        new FluidTagInput(FluidEnum.SodiumHydroxide.getFluidTag(FluidFamily.fluid), 250),
                        MetalEnum.Uranium.getStack(ItemFamily.metal_oxide, 1),
                        FluidEnum.Brine.getFluidStack(FluidFamily.fluid, 250),
                        200,
                        51200
                );

                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + getName() + "_to_slury",
                        MetalEnum.Uranium.getItemTag(ItemFamily.metal_oxide), 1,
                        new FluidTagInput(FluidEnum.HydrofluoricAcid.getFluidTag(FluidFamily.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 250),
                        ItemStack.EMPTY,
                        SlurryEnum.URANIUM.getType(FluidEnum.HydrofluoricAcid).getFluidStack(FluidFamily.slurry, 125),
                        100, 12800);

                IRecipeBuilder.chemical(this).create(
                        "slurry" + getName() + "_to_metal_dust",
                        new ItemStack(Items.BONE_MEAL, 1),
                        new FluidTagInput( SlurryEnum.URANIUM.getType(FluidEnum.HydrofluoricAcid).getFluidTag(FluidFamily.slurry), 125),
                        new FluidTagInput(FluidTags.WATER, 250),
                        MetalEnum.Uranium.getStack(ItemFamily.dust),
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