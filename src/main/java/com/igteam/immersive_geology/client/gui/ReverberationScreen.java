package com.igteam.immersive_geology.client.gui;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import com.igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import com.igteam.immersive_geology.common.gui.ReverberationContainer;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReverberationScreen extends IEContainerScreen<ReverberationContainer> {

    private static final ResourceLocation guiTexture = new ResourceLocation(IGLib.MODID, "textures/gui/reverberation_furnace.png");
    private ReverberationFurnaceTileEntity tileEntity;

    public ReverberationScreen(ReverberationContainer inventorySlotsIn, PlayerInventory inv, ITextComponent title) {
        super(inventorySlotsIn, inv, title);
        this.tileEntity = inventorySlotsIn.tile;

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mx, int my) {
        ClientUtils.bindTexture(guiTexture);
        this.blit(matrixStack, guiLeft, guiTop, 0, 0, 175, 168);
        int tank_x = guiLeft + 13;
        int tank_y = guiTop + 18;
        int tank_w = 16;
        int tank_h = 47;
        int oX = 180;
        int oY = 126;
        int oW = 20;
        int oH = 51;

        GuiHelper.handleGuiTank(matrixStack, Objects.requireNonNull(tileEntity.getInternalTanks())[0], tank_x, tank_y, tank_w, tank_h, oX, oY, oW, oH, mx, my, guiTexture, (List) null);
    }
}
