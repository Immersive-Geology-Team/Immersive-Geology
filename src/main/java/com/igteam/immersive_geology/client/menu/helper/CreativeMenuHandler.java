package com.igteam.immersive_geology.client.menu.helper;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.client.menu.IGItemGroup;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreativeMenuHandler {
    private static final ResourceLocation CEX_GUI_TEXTURES = new ResourceLocation(IGLib.MODID,"textures/gui/creative_expansion.png");
    private static ArrayList<CreativeMenuButton> subGroupButtons = new ArrayList<CreativeMenuButton>();

    @SubscribeEvent
    public void drawScreen(GuiScreenEvent.BackgroundDrawnEvent event) {
        Screen screen = event.getGui();
        if(screen instanceof CreativeScreen) {
            CreativeScreen gui = (CreativeScreen) screen;
            int i = (int) (gui.getGuiLeft() - Math.floor(136*1.425));

            if(gui.getSelectedTabIndex() == ImmersiveGeology.IGGroup.getIndex()) {
                if(!subGroupButtons.isEmpty()) {
                    subGroupButtons.forEach((button) -> {
                        button.active = true;
                        button.visible = true;
                    });
                }

                MatrixStack matrixStack = event.getMatrixStack();
                matrixStack.push();
                Minecraft.getInstance().textureManager.bindTexture(CEX_GUI_TEXTURES);
                GlStateManager.clearColor(1F, 1F, 1F, 1F);

                AbstractGui.blit(matrixStack, i + 166, gui.getGuiTop(), 0, 0, 29, 136, 256, 256);
                matrixStack.pop();
            } else {
                if(!subGroupButtons.isEmpty()) {
                    subGroupButtons.forEach((button) -> {
                        button.active = false;
                        button.visible = false;
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public void initializeGuiEvent(GuiScreenEvent.InitGuiEvent event) {
        Screen screen = event.getGui();
        if(screen instanceof CreativeScreen) {
            CreativeScreen gui = (CreativeScreen) screen;
            int i = (int) (gui.getGuiLeft() - Math.floor(136*1.425));
            int j = (gui.height - 195) / 2;

            for(int iteration = 0; iteration < ItemSubGroup.values().length; iteration++) {
                ItemSubGroup currentGroup = ItemSubGroup.values()[iteration];

                CreativeMenuButton button = new CreativeMenuButton(gui, currentGroup, i + 166 + 7, j + 46 + (23 * iteration),button1 -> {
                    IGItemGroup.updateSubGroup(currentGroup);
                    gui.getContainer().setAll(new ArrayList<ItemStack>());
                    PlayerInventory playerinventory = gui.getMinecraft().player.inventory;
                    gui.getContainer().inventorySlots.forEach((slot) -> {
                        if(slot.inventory != playerinventory){
                            slot.putStack(ItemStack.EMPTY);
                        }
                    });

                    ImmersiveGeology.IGGroup.fill(gui.getContainer().itemList);

                    gui.getContainer().itemList.sort(new ResourceSorter());

                    int slotIteration = 0;
                    for(int l = 0; l < gui.getContainer().inventorySlots.size(); l++) {
                        Slot slot = gui.getContainer().inventorySlots.get(l);
                        if(slot.inventory != playerinventory){
                            if(slotIteration < gui.getContainer().itemList.size()) {
                                slot.putStack(gui.getContainer().itemList.get(slotIteration));
                                slotIteration++;
                            }
                        }
                    }
                    //resize the gui to the same size, quick way to get it to update the content
                    gui.resize(gui.getMinecraft(), gui.width, gui.height);
                });

                subGroupButtons.add(button);
                event.addWidget(button);
            }
        }
    }

    public class CreativeMenuButton extends Button {

        public CreativeScreen screen;
        public CreativeScreen.CreativeContainer contained;
        public ItemSubGroup group;
        public CreativeMenuButton(CreativeScreen screen, ItemSubGroup group, int x, int y, IPressable onPress) {
            super(18,18,x, y, new StringTextComponent("Tooltip?"), onPress);
            this.width = 18;
            this.height = 18;
            this.x = x;
            this.y = y;
            this.contained = screen.getContainer();
            this.screen = screen;
            this.group = group;
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float ticks) {

            if(!visible) { return; };

            Minecraft mc = Minecraft.getInstance();
            Minecraft.getInstance().textureManager.bindTexture(CEX_GUI_TEXTURES);
            GlStateManager.clearColor(1F, 1F, 1F, 1F);

            boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

            AbstractGui.blit(matrixStack, x, y, ((hovered || (IGItemGroup.getCurrentSubGroup().equals(group))) ? 29 : 47), 0, width, height, 256, 256);

            ItemStack stack = new ItemStack(group.getIcon());

            if(hovered || (IGItemGroup.getCurrentSubGroup().equals(group))) {
                mc.getItemRenderer().renderItemIntoGUI(stack, x + 1, y + 2);
            } else {
                mc.getItemRenderer().renderItemIntoGUI(stack, x + 1, y + 1);
            }

            //Tool tip on hover
            if(hovered) {
                String name = group.name();
                String name_part = name.substring(0, 1).toUpperCase();
                String corrected_name = name_part + name.substring(1);
                List<IFormattableTextComponent> tooltip = new ArrayList<>();
                tooltip.add(new StringTextComponent(corrected_name));


                //we divide the width by four to force the tooltip to the left, as it's on a lower layer then the items in the creative menu.
                GuiUtils.drawHoveringText(matrixStack, tooltip,mouseX,mouseY,(mc.currentScreen.width / 4),mc.currentScreen.height,80,mc.fontRenderer);
            }
        }
    }
}
