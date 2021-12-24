package com.igteam.immersive_geology.common.gui;

import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import com.igteam.immersive_geology.common.gui.helper.IGSlot;
import com.igteam.immersive_geology.common.multiblocks.ReverberationFurnaceMultiblock;
import net.minecraft.entity.player.PlayerInventory;

public class ReverberationContainer extends MultiblockAwareGuiContainer<ReverberationFurnaceTileEntity> {
    public ReverberationContainer(PlayerInventory inv, ReverberationFurnaceTileEntity te, int id) {
        super(inv, te, id, ReverberationFurnaceMultiblock.INSTANCE);

        addSlot(new IGSlot(this.inv, 0, 12, 17){

        });
    }
}
