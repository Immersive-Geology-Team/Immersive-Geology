package igteam.immersive_geology.item;

import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.menu.ItemSubGroup;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public interface IGItemType {
    default boolean hasCustomItemColours()
    {
        return false;
    }
    int getColourForIGItem(ItemStack stack, int pass);
    public ItemSubGroup getSubGroup();
    Collection<MaterialInterface> getMaterials();
    MaterialPattern getPattern();
}
