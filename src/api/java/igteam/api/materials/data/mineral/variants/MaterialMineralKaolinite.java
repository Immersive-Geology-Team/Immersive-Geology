package igteam.api.materials.data.mineral.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.materials.MiscEnum;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockFamily;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralKaolinite extends MaterialBaseMineral {

    public MaterialMineralKaolinite() {
        super("kaolinite");
        initializeColorMap((p) -> 0xE5DFD1);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return null;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.preparation) {

            @Override
            protected void describe() {
                IRecipeBuilder.crafting(this).shaped(getBlock(BlockFamily.storage).asItem(), 1, "xx", "xx")
                        .setInputToCharacter('x', getItem(ItemFamily.clay))
                        .finializeRecipe("general_crafting", "has_clay", getItemTag(ItemFamily.clay));

                IRecipeBuilder.basicSmelting(this).create(getItemTag(ItemFamily.clay), getItem(ItemFamily.clay), MiscEnum.Refractory.getItem(ItemFamily.ingot));

            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        // TODO Auto-generated method stub
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 5)
        ));
    }

    @Override
    public Rarity getRarity() {
        // TODO Auto-generated method stub
        return Rarity.COMMON;
    }

    @Override
    protected boolean hasCrystal() {
        return false;
    }

    @Override
    protected boolean hasClay() {
        return true;
    }

    @Override
    protected boolean hasStorageBlock() {
        return true;
    }

    @Override
    protected boolean hasOreBlock() {
        return false;
    }

    @Override
    protected boolean hasOreBit() {
        return false;
    }

    @Override
    protected boolean hasOreChunk() {
        return false;
    }

    @Override
    protected boolean hasCrushedOre() {
        return false;
    }

    @Override
    protected boolean hasDirtyCrushedOre() {
        return false;
    }
    @Override
    protected boolean hasDust() {
        return false;
    }

    @Override
    public FeatureConfiguration getGenerationConfig() {
        return super.getGenerationConfig();
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        return Collections.emptySet();
    }

    public boolean generateForBlockPattern(BlockFamily p) {
        return p.equals(BlockFamily.storage);
    }
}
