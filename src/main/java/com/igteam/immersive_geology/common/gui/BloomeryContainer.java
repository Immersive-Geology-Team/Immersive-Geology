package com.igteam.immersive_geology.common.gui;

import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import com.igteam.immersive_geology.common.block.blocks.multiblocks.BloomeryBlock;
import com.igteam.immersive_geology.common.block.tileentity.BloomeryTileEntity;
import com.igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import com.igteam.immersive_geology.common.gui.helper.IGSlot;
import igteam.immersive_geology.processing.recipe.BloomeryRecipe;
import igteam.immersive_geology.processing.recipe.ReverberationRecipe;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;


//FIXME Rewrite without internal IE classes
public class BloomeryContainer extends IEBaseContainer<BloomeryTileEntity> {

    public BloomeryContainer(int id, PlayerInventory inv, BloomeryTileEntity te) {

        super(te, id);
        //fuel
        this.addSlot(new IGSlot(this.inv, 2, 51, 53) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack itemStack) {
                return BloomeryTileEntity.fuelMap.containsKey(itemStack.getItem());
            }
        });

        this.addSlot(new IGSlot(this.inv, 0,51 ,17 ) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack itemStack) {
                return (BloomeryRecipe.findRecipe(itemStack) != null);
            }
        });

        this.addSlot(new IGSlot(this.inv, 1, 97, 17){
            @Override
            public boolean isItemValid(@Nonnull ItemStack itemStack) {
                return false;
            }
        });


        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inv, i, 8 + i * 18, 142));

        //TODO -- Add trackers

    }

}
