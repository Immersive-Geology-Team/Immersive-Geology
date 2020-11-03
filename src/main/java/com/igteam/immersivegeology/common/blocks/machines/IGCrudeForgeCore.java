package com.igteam.immersivegeology.common.blocks.machines;

import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGTileBlock;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.tileentity.entities.CrudeForgeTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

public class IGCrudeForgeCore extends IGTileBlock {
    public IGCrudeForgeCore() {
        super("crude_forge", MaterialUseType.ROCK, EnumMaterials.Marble.material);
    }
}
