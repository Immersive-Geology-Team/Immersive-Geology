package com.igteam.immersive_geology.common.gui;

import com.igteam.immersive_geology.common.crafting.recipes.recipe.ReverberationRecipe;
import com.igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import com.igteam.immersive_geology.common.gui.helper.IGSlot;
import com.igteam.immersive_geology.common.multiblocks.ReverberationFurnaceMultiblock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ReverberationContainer extends MultiblockAwareGuiContainer<ReverberationFurnaceTileEntity> {
    public ReverberationContainer(int id, PlayerInventory inv, ReverberationFurnaceTileEntity te) {
        super(inv, te, id, ReverberationFurnaceMultiblock.INSTANCE);
    //fuel
        this.addSlot(new IGSlot(this.inv, 0, 36, 53) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack itemStack) {
                //gonna figure out what is where later
                return ReverberationFurnaceTileEntity.fuelMap.containsKey(itemStack.getItem());
            }
        });

        this.addSlot(new IGSlot(this.inv, 1, 116, 53) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack itemStack) {
                //gonna figure out what is where later
                return ReverberationFurnaceTileEntity.fuelMap.containsKey(itemStack.getItem());
            }
        });
//inputs
        this.addSlot(new IGSlot(this.inv, 4,36 ,17 ) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack itemStack) {
                return (ReverberationRecipe.findRecipe(itemStack) != null);
            }
        });

        this.addSlot(new IGSlot(this.inv, 5,116 ,17 ) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack itemStack) {
                return (ReverberationRecipe.findRecipe(itemStack) != null);
            }
        });

        //outputs
        this.addSlot(new IGSlot(this.inv, 2, 36, 53));
        this.addSlot(new IGSlot(this.inv, 3, 116, 53));

        this.slotCount = 6;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inv, i, 8 + i * 18, 142));
    }
}
