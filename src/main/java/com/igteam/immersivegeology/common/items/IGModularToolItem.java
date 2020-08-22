package com.igteam.immersivegeology.common.items;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.tool.ITool;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.Set;

public class IGModularToolItem extends IGBaseItem implements ITool
{
    protected static final ToolType HAMMER_TOOL = ToolType.get(ImmersiveEngineering.MODID + "_hammer");
    protected static final ToolType PICKAXE_TOOL = ToolType.PICKAXE;
    protected static final ToolType AXE_TOOL = ToolType.AXE;
    protected static final ToolType SHOVEL_TOOL = ToolType.SHOVEL;

    protected Material[] partMaterials;

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

    public Material getHead()
    {
        return this.partMaterials[0];
    }

    public Material getBinding()
    {
        return this.partMaterials[1];
    }

    public Material getHandle()
    {
        return this.partMaterials[2];
    }

    public Material getTip()
    {
        return this.partMaterials.length > 3 ? this.partMaterials[3] : EnumMaterials.Empty.material;
    }

    @Override
    public int getItemEnchantability() {
        return super.getItemEnchantability();
    }
}
