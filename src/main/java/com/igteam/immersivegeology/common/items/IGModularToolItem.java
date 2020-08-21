package com.igteam.immersivegeology.common.items;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.tool.ITool;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersivegeology.ImmersiveGeology;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraftforge.common.ToolType;

import java.util.Set;

public class IGModularToolItem extends IGBaseItem implements ITool
{
    private static final ToolType HAMMER_TOOL = ToolType.get(ImmersiveEngineering.MODID + "_hammer");
    private static final ToolType PICKAXE_TOOL = ToolType.get("pickaxe");
    private static final ToolType AXE_TOOL = ToolType.get("axe");
    private static final ToolType SHOVEL_TOOL = ToolType.get("shovel");

    public IGModularToolItem(String name)
    {
        super(name);
    }

    @Override
    public boolean isTool(ItemStack itemStack) {
        return !itemStack.getToolTypes().isEmpty();
    }

    @Override
    public Set<ToolType> getToolTypes(ItemStack stack)
    {
        ImmutableSet<ToolType> toolTypes = null;
        return toolTypes;
    }
}
