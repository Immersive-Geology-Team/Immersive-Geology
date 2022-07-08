package igteam.immersive_geology.common.gui;

import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import igteam.immersive_geology.common.block.tileentity.BloomeryTileEntity;
import igteam.immersive_geology.common.gui.helper.IGSlot;
import igteam.api.processing.recipe.BloomeryRecipe;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IntReferenceHolder;

import javax.annotation.Nonnull;


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

        this.addSlot(new IGSlot(this.inv, 0, 51, 17) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack itemStack) {
                return (BloomeryRecipe.findRecipe(itemStack) != null);
            }
        });

        this.addSlot(new IGSlot(this.inv, 1, 97, 17) {
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

        trackInt(new IntReferenceHolder() {
                     @Override
                     public void set(int value) {
                         ((BloomeryTileEntity) tile.master()).setProgress(value);
                     }

                     @Override
                     public int get() {
                         return getProgress();
                     }
                 }
        );

    }
    public int getProgress(){
        return ((BloomeryTileEntity) tile.master()).getProgress();
    }

    public boolean getBurningState()
    {
        return ((BloomeryTileEntity) tile.master()).isBurning();
    }

}
