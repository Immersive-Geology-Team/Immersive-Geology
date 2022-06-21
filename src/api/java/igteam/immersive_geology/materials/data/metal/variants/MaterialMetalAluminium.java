package igteam.immersive_geology.materials.data.metal.variants;

import blusunrize.immersiveengineering.api.EnumMetals;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalAluminium extends MaterialBaseMetal {

    public MaterialMetalAluminium() {
        super("aluminum"); //compat rename
        initializeColorMap((p) -> 0xd0d5db);
    }

    @Override
    public boolean hasCrystal() {return false;}

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
   public boolean hasCompoundDust () {return  true;}

    @Override
    public boolean hasMetalOxide () {return  true;}
    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM)
        ));
    }
}
