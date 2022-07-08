package igteam.immersive_geology.common.gui;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;

/**
 * @author TwistedGate © 2021
 * @implNote 'borrowed' from Immersive Petroleum, I swears I'll make my own in the next version ~Muddykat.
 */
public class MultiblockAwareGuiContainer<T extends MultiblockPartTileEntity<T>> extends IEBaseContainer<T> {
    static final Vector3i ONE = new Vector3i(1, 1, 1);

    protected BlockPos templateSize;
    public MultiblockAwareGuiContainer(PlayerInventory inv, T tile, int id, IETemplateMultiblock template){
        super(tile, id);

        this.templateSize = new BlockPos(template.getSize(this.tile.getWorldNonnull())).subtract(ONE);
    }

    /**
     * Returns the maximum distance in blocks to the multiblock befor the GUI
     * get's closed automaticly
     */
    public int getMaxDistance(){
        return 5;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player){
        if(inv != null){
            BlockPos min = this.tile.getBlockPosForPos(BlockPos.ZERO);
            BlockPos max = this.tile.getBlockPosForPos(this.templateSize);

            AxisAlignedBB box = new AxisAlignedBB(min, max).grow(getMaxDistance());

            return box.intersects(player.getBoundingBox());
        }

        return false;
    }
}
