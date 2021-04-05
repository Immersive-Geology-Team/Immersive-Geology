package com.igteam.immersive_geology.client.menu.helper;

import com.igteam.immersive_geology.ImmersiveGeology;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.Objects;

public class ResourceSorter implements Comparator<ItemStack>
{
    private Logger log = ImmersiveGeology.getNewLogger();

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
            log.error("An item has no name or is registered badly! How forge event managed to get this far is beyond me. Error Log: " + e.getMessage());
        }

        return namePartOne.compareTo(namePartTwo);
    }
}