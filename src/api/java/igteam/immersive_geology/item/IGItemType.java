package igteam.immersive_geology.item;

import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.menu.ItemSubGroup;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public interface IGItemType {
    default boolean hasCustomItemColours()
    {
        return false;
    }

    int getColourForIGItem(ItemStack stack, int pass);

    public ItemSubGroup getSubGroup();
    Set<MaterialInterface> getMaterials();
    MaterialPattern getPattern();
}
