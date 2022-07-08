package igteam.api.block;

import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;

import javax.annotation.Nonnull;
import java.util.Collection;

public interface IGBlockType {
    default boolean hasCustomBlockColours()
    {
        return false;
    }

    int getColourForIGBlock(int pass);

    Collection<MaterialInterface> getMaterials();
    @Nonnull
    MaterialPattern getPattern();
    String getHolderKey();
    Block getBlock();
}
