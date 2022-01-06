package igteam.immersive_geology.menu.helper;

import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceSorter implements Comparator<ItemStack> {
    private final java.util.logging.Logger logger = Logger.getLogger(ResourceSorter.class.getName());

    @Override
    public int compare(ItemStack o1, ItemStack o2)
    {

        String namePartOne = "pierwszy", namePartTwo = "drugi";
        try
        {
            namePartOne = Objects.requireNonNull(o1.getItem().getRegistryName()).getPath();
            namePartTwo = Objects.requireNonNull(o2.getItem().getRegistryName()).getPath();
        } catch(NullPointerException e)
        {
            logger.log(Level.INFO, "An item has no name or is registered badly! How forge event managed to get this far is beyond me. Error Log: " + e.getMessage());
        }

        return namePartOne.compareTo(namePartTwo);
    }
}
