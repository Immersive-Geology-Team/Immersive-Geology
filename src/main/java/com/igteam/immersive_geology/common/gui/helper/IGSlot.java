package com.igteam.immersive_geology.common.gui.helper;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class IGSlot extends Slot {

    public IGSlot(IInventory inventoryIn, int index, int xPosition, int yPosition){
        super(inventoryIn, index, xPosition, yPosition);
    }

    public static class ItemOutput extends IGSlot{
        public ItemOutput(IInventory inventoryIn, int index, int xPosition, int yPosition){
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack){
            return true;
        }
    }

    public static class FluidContainer extends IGSlot{
        FluidFilter filter;
        public FluidContainer(IInventory inv, int id, int x, int y, FluidFilter filter){
            super(inv, id, x, y);
            this.filter = filter;
        }

        @Override
        public boolean isItemValid(ItemStack itemStack){
            LazyOptional<IFluidHandlerItem> handlerCap = FluidUtil.getFluidHandler(itemStack);
            return handlerCap.map(handler -> {
                if(handler.getTanks() <= 0)
                    return false;

                switch(filter){
                    case FULL:
                        return !handler.getFluidInTank(0).isEmpty();
                    case EMPTY:
                        return handler.getFluidInTank(0).isEmpty();
                    case ANY:
                    default:
                        return true;
                }
            }).orElse(false);
        }

        public static enum FluidFilter{
            ANY, EMPTY, FULL;
        }
    }
}
