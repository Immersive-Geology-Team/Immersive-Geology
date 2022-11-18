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
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralSphalerite extends MaterialBaseMineral {

    public MaterialMineralSphalerite() {
        super("sphalerite");
        initializeColorMap((p) -> 0x6F8070);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    @Override
    public boolean hasSlag(){
        return true;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.refinement) {
            @Override
            protected void describe() {
                IRecipeBuilder.roast(this).create("mineral_" + getName() + "_to_slag",
                        getParentMaterial().getItemTag(ItemFamily.crushed_ore), 1, getParentMaterial().getStack(ItemFamily.slag),1000, 1);
                IRecipeBuilder.crushing(this).create("slag_" + getName() + "_to_dust",
                        getItemTag(ItemFamily.slag),
                        getStack(ItemFamily.dust), 3000, 200);
                IRecipeBuilder.separating(this).create(
                        getItemTag(ItemFamily.dust),
                        MetalEnum.Iron.getStack(ItemFamily.metal_oxide),
                        MetalEnum.Zinc.getStack(ItemFamily.metal_oxide));

                IRecipeBuilder.blasting(this).create(
                        "oxide_" + MetalEnum.Iron.getName() + "_to_ingot",
                        MetalEnum.Iron.getItemTag(ItemFamily.metal_oxide),
                        MetalEnum.Iron.getStack(ItemFamily.ingot));
            }
        };

        new IGProcessingStage(this,IGStageDesignation.purification) {
            @Override
            protected void describe() {
                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + MetalEnum.Zinc.getName() + "_to_slurry",
                        MetalEnum.Zinc.getItemTag(ItemFamily.metal_oxide), 1,
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidFamily.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        ItemStack.EMPTY,
                        SlurryEnum.ZINC.getType(FluidEnum.SulfuricAcid).getFluidStack(FluidFamily.slurry, 250),
                        200, 51200);


                IRecipeBuilder.crystalize(this).create(
                        "slurry_" + SlurryEnum.ZINC.getType(FluidEnum.SulfuricAcid).getName() + "_to_crystal",
                        MetalEnum.Zinc.getStack(ItemFamily.crystal),
                        SlurryEnum.ZINC.getType(FluidEnum.SulfuricAcid).getFluidTag(FluidFamily.slurry), 250,
                        300,38400);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ZINC),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR)
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
        sources.add(MetalEnum.Zinc);
        sources.add(MetalEnum.Iron);

        return sources;
    }

}
