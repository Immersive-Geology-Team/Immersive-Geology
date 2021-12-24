package com.igteam.immersive_geology.client.gui;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import com.igteam.immersive_geology.common.gui.ReverberationContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ReverberationScreen extends IEContainerScreen<ReverberationContainer> {


    public ReverberationScreen(ReverberationContainer inventorySlotsIn, PlayerInventory inv, ITextComponent title) {
        super(inventorySlotsIn, inv, title);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

    }
}
