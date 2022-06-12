package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.helper.PeriodicTableElement.ElementProportion;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import igteam.immersive_geology.processing.methods.IGBlastingMethod;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.function.Function;

public class MaterialMetalChromium extends MaterialBaseMetal {

    public MaterialMetalChromium() {
        super("chromium");
        initializeColorMap((p) -> 0xD7B4F3);
    }

    @Override
    protected boolean hasDust() {
        return true;
    }

    @Override
    protected boolean hasCrystal() {
        return true;
    }

    @Override
    public LinkedHashSet<ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new ElementProportion(PeriodicTableElement.CHROMIUM)
        ));
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Crystal Processing"){
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create(getName() + "_crystal_to_grit", getItemTag(ItemPattern.crystal), getStack(ItemPattern.dust), 1000, 1000);
                IRecipeBuilder.blasting(this).create(getName() + "_grit_to_ingot", getItemTag(ItemPattern.dust), getStack(ItemPattern.ingot));
            }
        };
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }
}
