package com.igteam.immersive_geology.client.menu;

import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;


import java.util.ArrayList;

public class CreativeMenuHandler {
    private static final ResourceLocation CEX_GUI_TEXTURES = new ResourceLocation(IGLib.MODID,"textures/gui/creative_expansion.png");
    private static ArrayList<CreativeMenuButton> subGroupButtons = new ArrayList<CreativeMenuButton>();

    @SubscribeEvent
    public void drawScreen(ScreenEvent.BackgroundDrawnEvent event) {
        Screen screen = event.getScreen();
        if(screen instanceof CreativeModeInventoryScreen) {
            CreativeModeInventoryScreen gui = (CreativeModeInventoryScreen) screen;
            int i = (int) (gui.getGuiLeft() - Math.floor(136*1.425));

            if(gui.getSelectedTab() == IGItemGroup.IGGroup.getId()) {
                if(!subGroupButtons.isEmpty()) {
                    subGroupButtons.forEach((button) -> {
                        button.active = true;
                        button.visible = true;
                    });
                }

                PoseStack matrixStack = event.getPoseStack();
                matrixStack.pushPose();
                GlStateManager._clearColor(1F, 1F, 1F, 1F);
                GuiUtils.drawContinuousTexturedBox(matrixStack, CEX_GUI_TEXTURES, i + 166, gui.getGuiTop(), 0, 0, 29, 136, 256, 256, 0, 0);
                matrixStack.popPose();
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
    public void initializeGuiEvent(ScreenEvent.InitScreenEvent event) {
        Screen screen = event.getScreen();
        if(screen instanceof CreativeModeInventoryScreen) {
            CreativeModeInventoryScreen gui = (CreativeModeInventoryScreen) screen;
            int i = (int) (gui.getGuiLeft() - Math.floor(136*1.425));
            int j = (gui.height - 195) / 2;

            ItemSubGroup[] groups = ItemSubGroup.values();
            for(int iteration = 0; iteration < ItemSubGroup.values().length; iteration++) {
                ItemSubGroup currentGroup = groups[iteration];

                CreativeMenuButton button = new CreativeMenuButton(gui, currentGroup, i + 166 + 7, j + 46 + (23 * iteration),button1 -> {
                    IGItemGroup.updateSubGroup(currentGroup); //Update the sub-group
                    gui.resize(gui.getMinecraft(), gui.width, gui.height); //resize the gui to the same size, quick way to get it to update the content
                });

                subGroupButtons.add(button);
                event.addListener(button);
            }
        }
    }

    public class CreativeMenuButton extends Button {

        public CreativeModeInventoryScreen screen;
        public CreativeModeInventoryScreen.ItemPickerMenu contained;
        public ItemSubGroup group;
        public CreativeMenuButton(CreativeModeInventoryScreen screen, ItemSubGroup group, int x, int y, Button.OnPress onPress) {
            super(18,18,x, y, new TextComponent("Tooltip?"), onPress);
            this.width = 18;
            this.height = 18;
            this.x = x;
            this.y = y;
            this.contained = screen.getMenu();
            this.screen = screen;
            this.group = group;
        }

        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {

            if(!visible) { return; };

            Minecraft mc = Minecraft.getInstance();
            GlStateManager._clearColor(1F, 1F, 1F, 1F);

            boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

            GuiUtils.drawContinuousTexturedBox(poseStack,CEX_GUI_TEXTURES, x, y, ((hovered || (IGItemGroup.getCurrentSubGroup().equals(group))) ? 29 : 47), 0, width, height, 256, 256,0, 0);

            IFlagType<?> groupPattern = group.getFlag();
            ItemStack stack = group.getMaterial().getStack(groupPattern);

            if(hovered || (IGItemGroup.getCurrentSubGroup().equals(group))) {
                mc.getItemRenderer().renderGuiItem(stack, x + 1, y + 2);
            } else {
                mc.getItemRenderer().renderGuiItem(stack, x + 1, y + 1);
            }

            //Tool tip on hover
            if(hovered) {
                String name = group.name();
                String name_part = name.substring(0, 1).toUpperCase();
                String corrected_name = name_part + name.substring(1);

                //we divide the width by four to force the tooltip to the left, as it's on a lower layer then the items in the creative menu.
                screen.renderTooltip(poseStack, new TextComponent(corrected_name), mouseX, mouseY);
            }
        }
    }
}
