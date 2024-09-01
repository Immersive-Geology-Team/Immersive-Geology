package com.igteam.immersivegeology.client.menu;

import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import static com.igteam.immersivegeology.client.menu.IGItemGroup.selectedGroup;

public class CreativeMenuHandler {
    private static final ResourceLocation CEX_GUI_TEXTURES = new ResourceLocation(IGLib.MODID,"textures/gui/creative_expansion.png");
    private static ArrayList<CreativeMenuButton> subGroupButtons = new ArrayList<CreativeMenuButton>();
    boolean reset = true;
    Logger logger = IGLib.getNewLogger();

    @SubscribeEvent
    public void drawScreen(ScreenEvent.BackgroundRendered event) {
        Screen screen = event.getScreen();
        if(screen instanceof CreativeModeInventoryScreen) {
            CreativeModeInventoryScreen gui = (CreativeModeInventoryScreen) screen;
            int i = (int) (gui.getGuiLeft() - Math.floor(136*1.425));
            CreativeModeTab selectedTab = CreativeModeInventoryScreen.selectedTab;

            CreativeModeTab igTab = IGRegistrationHolder.IG_BASE_TAB.get();
            if(selectedTab.equals(igTab)) {
                if(reset) screen.resize(event.getScreen().getMinecraft(), screen.width, screen.height);

                if(!subGroupButtons.isEmpty()) {
                    subGroupButtons.forEach((button) -> {
                        button.active = true;
                        button.visible = true;
                    });
                }

                PoseStack matrixStack = event.getGuiGraphics().pose();
                matrixStack.pushPose();
                GlStateManager._clearColor(1F, 1F, 1F, 1F);
                GuiGraphics pgui = event.getGuiGraphics();
                //int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight
                //  int x, int y, int u, int v, int width, int height,
                //
                pgui.blit(CEX_GUI_TEXTURES,i + 166, gui.getGuiTop(), 0, 0, 29, 136);
                matrixStack.popPose();
            } else {
                reset = true;
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
    public void initializeGuiEvent(ScreenEvent.Init.Pre event) {
        Screen screen = event.getScreen();
        if(screen instanceof CreativeModeInventoryScreen) {
            CreativeModeInventoryScreen gui = (CreativeModeInventoryScreen) screen;
            int i = (int) (gui.getGuiLeft() - Math.floor(136*1.425));
            int j = (gui.height - 195) / 2;

            ItemSubGroup[] groups = ItemSubGroup.values();
            for(int iteration = 0; iteration < ItemSubGroup.values().length; iteration++) {
                ItemSubGroup currentGroup = groups[iteration];

                CreativeMenuButton button = new CreativeMenuButton(gui, currentGroup, i + 166 + 7, j + 46 + (23 * iteration), button1 -> {
                    IGItemGroup.updateSubGroup(currentGroup); //Update the sub-group
                    gui.resize(gui.getMinecraft(), gui.width, gui.height); //resize the gui to the same size, quick way to get it to update the content
                }, narration -> {
                    return Component.translatable("");
                });

                subGroupButtons.add(button);
                event.addListener(button);
            }
        }
    }

    public static class CreativeMenuButton extends Button {

        public CreativeModeInventoryScreen screen;
        public CreativeModeInventoryScreen.ItemPickerMenu contained;
        public ItemSubGroup group;
        public CreativeMenuButton(CreativeModeInventoryScreen screen, ItemSubGroup group, int x, int y, OnPress onPress, CreateNarration narration) {
            super(18,18,x, y, Component.translatable(""), onPress, narration);
            this.width = 18;
            this.height = 18;
            this.setX(x);
            this.setY(y);
            this.contained = screen.getMenu();
            this.screen = screen;
            this.group = group;
        }

        @Override
        protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

            if(!visible) { return; };

            Minecraft mc = Minecraft.getInstance();
            GlStateManager._clearColor(1F, 1F, 1F, 1F);
            int x = this.getX();
            int y = this.getY();

            boolean hovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height;
            pGuiGraphics.blit(CEX_GUI_TEXTURES, x, y, ((hovered || (IGItemGroup.getCurrentSubGroup().equals(group))) ? 29 : 47), 0, width, height);

            IFlagType<?> groupPattern = group.getFlag();

            ItemStack stack = group.getMaterial().getStack(groupPattern);

            if(hovered || (IGItemGroup.getCurrentSubGroup().equals(group))) {
                pGuiGraphics.renderItem(stack, x + 1, y + 2);
            } else {
                pGuiGraphics.renderItem(stack, x + 1, y + 1);
            }


            //Tool tip on hover
            if(hovered) {
                String name = group.name();
                String name_part = name.substring(0, 1).toUpperCase();
                String corrected_name = name_part + name.substring(1);

                //we divide the width by four to force the tooltip to the left, as it's on a lower layer then the items in the creative menu.
                pGuiGraphics.renderTooltip(mc.font, Component.translatable(corrected_name), pMouseX, pMouseY);
            }
        }
    }
}
