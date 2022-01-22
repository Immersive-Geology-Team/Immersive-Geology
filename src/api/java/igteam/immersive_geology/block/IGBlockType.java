package igteam.immersive_geology.block;

import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.menu.ItemSubGroup;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public interface IGBlockType {
    default boolean hasCustomBlockColours()
    {
        return false;
    }

    int getColourForIGBlock(int pass);

    Collection<MaterialInterface> getMaterials();
    MaterialPattern getPattern();
}
