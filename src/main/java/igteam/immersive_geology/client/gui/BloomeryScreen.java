package igteam.immersive_geology.client.gui;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import igteam.immersive_geology.common.block.tileentity.BloomeryTileEntity;
import igteam.immersive_geology.common.gui.BloomeryContainer;
import igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

//FIXME -- remove internal class usage
public class BloomeryScreen extends IEContainerScreen<BloomeryContainer> {

    private static final ResourceLocation guiTexture = new ResourceLocation(IGLib.MODID, "textures/gui/bloomery.png");
    private BloomeryTileEntity tileEntity;

    public BloomeryScreen(BloomeryContainer inventorySlotsIn, PlayerInventory inv, ITextComponent title) {
        super(inventorySlotsIn, inv, title);
        this.tileEntity = inventorySlotsIn.tile;
    }
    @Override
    public void render(MatrixStack transform, int mouseX, int mouseY, float partialTicks) {
        super.render(transform, mouseX, mouseY, partialTicks);

        List<ITextComponent> tooltip = new ArrayList<>();

        if(!tooltip.isEmpty()){
            GuiUtils.drawHoveringText(transform, tooltip, mouseX, mouseY, width, height, -1, font);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mx, int my) {
        ClientUtils.bindTexture(guiTexture);
        this.blit(matrix, guiLeft, guiTop, 0, 0, 175, 168);


        BloomeryTileEntity master = (BloomeryTileEntity) tileEntity.master();

     if(master != null) {
            float leftProgress = ((float) container.getProgress() / container.getMaxProgress());
            this.blit(matrix, guiLeft + 72, guiTop + 15, 176, 16, Math.round(21 * leftProgress), 16);
            if(container.getBurningState())
            {
                this.blit(matrix, guiLeft + 51, guiTop + 36, 176, 0, 16, 16);
            }
        }
    }

}
