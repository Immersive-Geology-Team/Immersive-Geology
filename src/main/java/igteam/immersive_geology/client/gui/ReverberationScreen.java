package igteam.immersive_geology.client.gui;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import igteam.immersive_geology.common.gui.ReverberationContainer;
import igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class ReverberationScreen extends IEContainerScreen<ReverberationContainer> {

    private static final ResourceLocation guiTexture = new ResourceLocation(IGLib.MODID, "textures/gui/reverberation_furnace.png");
    private ReverberationFurnaceTileEntity tileEntity;

    public ReverberationScreen(ReverberationContainer inventorySlotsIn, PlayerInventory inv, ITextComponent title) {
        super(inventorySlotsIn, inv, title);
        this.tileEntity = inventorySlotsIn.tile;
    }

    @Override
    public void render(MatrixStack matrix, int mx, int my, float partialTicks) {
        super.render(matrix, mx, my, partialTicks);
        int tank_x = guiLeft + 13;
        int tank_y = guiTop + 18;
        int tank_w = 16;
        int tank_h = 47;
        int oX = 180;
        int oY = 126;
        int oW = 20;
        int oH = 51;

        ReverberationFurnaceTileEntity master = tileEntity.master();
        List<ITextComponent> tooltip = new ArrayList<>();
        assert master != null;

        GuiHelper.handleGuiTank(matrix, this.tileEntity.gasTank, tank_x, tank_y, tank_w, tank_h, oX, oY, oW, oH, mx, my, guiTexture, tooltip);

        if(!tooltip.isEmpty()){
            GuiUtils.drawHoveringText(matrix, tooltip, mx, my, width, height, -1, font);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mx, int my) {
        ClientUtils.bindTexture(guiTexture);
        this.blit(matrix, guiLeft, guiTop, 0, 0, 175, 168);
        int tank_x = guiLeft + 13;
        int tank_y = guiTop + 18;
        int tank_w = 16;
        int tank_h = 47;
        int oX = 180;
        int oY = 126;
        int oW = 20;
        int oH = 51;

        RenderSystem.disableBlend();
        GuiHelper.handleGuiTank(matrix, this.tileEntity.gasTank, tank_x, tank_y, tank_w, tank_h, oX, oY, oW, oH, mx, my, guiTexture, (List<ITextComponent>) null);

        float leftProgress =  ((float) container.getLeftProgress() / 100);

        this.blit(matrix, guiLeft + 42, guiTop + 35, 176, 16, Math.round(21 * leftProgress), 16);

        float rightProgress = ((float) container.getRightProgress() / 100);
        this.blit(matrix, guiLeft + 122, guiTop + 35, 176, 16, Math.round(21 * rightProgress), 16);
    }
}
