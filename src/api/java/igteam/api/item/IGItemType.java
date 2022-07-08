package igteam.api.item;

import igteam.api.menu.ItemSubGroup;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface IGItemType {
    default boolean hasCustomItemColours()
    {
        return false;
    }
    int getColourForIGItem(ItemStack stack, int pass);

    ItemSubGroup getSubGroup();
    Collection<MaterialInterface> getMaterials();
    MaterialPattern getPattern();

    String getHolderKey();
    BlockPattern getBlockPattern();
}
