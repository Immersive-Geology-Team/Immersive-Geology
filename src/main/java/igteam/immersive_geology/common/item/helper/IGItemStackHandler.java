package igteam.immersive_geology.common.item.helper;

import blusunrize.immersiveengineering.api.utils.CapabilityUtils;
import blusunrize.immersiveengineering.common.items.InternalStorageItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Copy of IEItemStackHandler
 * It was written too well and not in public api, so I had to copy it for our needs
 * My apologies for reuse
 * ~Unschtalch
 **/

public class IGItemStackHandler extends ItemStackHandler implements ICapabilityProvider {
    @Nonnull
    private Runnable onChange = () -> {
    };
    private final LazyOptional<IItemHandler> thisOpt = CapabilityUtils.constantOptional(this);

    public IGItemStackHandler(ItemStack stack) {
        int idealSize = ((InternalStorageItem)stack.getItem()).getSlotCount();
        NonNullList<ItemStack> newList = NonNullList.withSize(idealSize, ItemStack.EMPTY);

        for(int i = 0; i < Math.min(this.stacks.size(), idealSize); ++i) {
            newList.set(i, (ItemStack)this.stacks.get(i));
        }

        this.stacks = newList;
    }

    public void setTile(TileEntity tile) {
        if (tile != null) {
            Objects.requireNonNull(tile);
            this.onChange = tile::markDirty;
        } else {
            this.onChange = () -> {
            };
        }

    }

    public void setInventoryForUpdate(IInventory inv) {
        if (inv != null) {
            Objects.requireNonNull(inv);
            this.onChange = inv::markDirty;
        } else {
            this.onChange = () -> {
            };
        }

    }

    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        this.onChange.run();
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, this.thisOpt);
    }

    public NonNullList<ItemStack> getContainedItems() {
        return this.stacks;
    }
}