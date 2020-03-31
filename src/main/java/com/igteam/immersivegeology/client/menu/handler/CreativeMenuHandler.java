package com.igteam.immersivegeology.client.menu.handler;

import java.util.ArrayList;
import java.util.List;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.client.menu.IGItemGroup;
import com.igteam.immersivegeology.client.menu.helper.IGSubGroup;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen.CreativeContainer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.config.GuiUtils;

public class CreativeMenuHandler {
	private static final ResourceLocation CEX_GUI_TEXTURES = new ResourceLocation(ImmersiveGeology.MODID,"textures/gui/creative_expansion.png");
	
	private static ArrayList<CreativeMenuButton> subGroupButtons = new ArrayList<CreativeMenuButton>();
	
	@SubscribeEvent
	public void drawScreen(DrawScreenEvent.Pre event) {
		Screen screen = event.getGui();
		if(screen instanceof CreativeScreen) {
			CreativeScreen gui = (CreativeScreen) screen; 
			int i = (int) (gui.getGuiLeft() - Math.floor(136*1.425));
			
			if(!gui.getContainer().itemList.isEmpty()) {
				if(gui.getContainer().itemList.get(0).getItem() instanceof IGSubGroup) {
					if(!subGroupButtons.isEmpty()) {
						subGroupButtons.forEach((button) -> {
							button.active = true;
							button.visible = true;	
						});
					}
					Minecraft.getInstance().textureManager.bindTexture(CEX_GUI_TEXTURES);
					GlStateManager.color3f(1F, 1F, 1F);
					
					AbstractGui.blit(i + 166, gui.getGuiTop(), 0, 0, 29, 136, 256, 256);
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
	}
	
	@SubscribeEvent
    public void initializeGuiEvent(InitGuiEvent.Post event) {
		Screen screen = event.getGui();
		if(screen instanceof CreativeScreen) {
			CreativeScreen gui = (CreativeScreen) screen; 

			int i = (int) (gui.getGuiLeft() - Math.floor(136*1.425));
			int j = (gui.height - 195) / 2; 

			for(int iteration = 0; iteration < ItemSubGroup.values().length; iteration++) {
				ItemSubGroup currentGroup = ItemSubGroup.values()[iteration];
				
				CreativeMenuButton button = new CreativeMenuButton(gui.getContainer(), currentGroup, i + 166 + 7, j + 46 + (23 * iteration), (onPress) -> {
					
					IGItemGroup.updateSubGroup(currentGroup);
					
					gui.getContainer().inventorySlots.forEach((slot) -> {
							if(gui.getContainer().itemList.size() > 45) {
								if(slot.getSlotIndex() > 9) {
									(slot).putStack(ItemStack.EMPTY);
								}
							} else {
								(slot).putStack(ItemStack.EMPTY);
							}
						});
					
					gui.getContainer().itemList.clear();
					
					gui.getContainer().itemList.addAll(IGItemGroup.getCurrentList());
					for(int k = 0; k < gui.getContainer().itemList.size(); k++) {
						
						ItemStack stack = gui.getContainer().itemList.get(k);
						Slot slot = gui.getContainer().inventorySlots.get(k);
						
						if(gui.getContainer().itemList.size() > 45) {
							if(slot.getSlotIndex() > 9) {
								slot.putStack(stack);
							}
						} else {
							slot.putStack(stack);
						}
					}
				;});
				
				subGroupButtons.add(button);
				event.addWidget(button);	
			}
		}
	}
	
	public class CreativeMenuButton extends Button {

		public CreativeContainer contained;
		public ItemSubGroup group;
		public CreativeMenuButton(CreativeContainer container, ItemSubGroup group, int x, int y, IPressable onPress) {
			super(18,18,x, y, "Tooltip?", onPress);
			this.width = 18;
			this.height = 18;
			this.x = x;
			this.y = y;
			this.contained = container;
			this.group = group;
		}
		
		@Override
		public void renderButton(int mouseX, int mouseY, float pticks) {
					Minecraft mc = Minecraft.getInstance();
					Minecraft.getInstance().textureManager.bindTexture(CEX_GUI_TEXTURES);
					GlStateManager.color3f(1F, 1F, 1F);
					RenderHelper.disableStandardItemLighting();
					boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
					
					AbstractGui.blit(x, y, ((hovered || (IGItemGroup.getCurrentSubGroup().equals(group))) ? 29 : 47), 0, width, height, 256, 256);
					
					ItemStack stack = ItemStack.EMPTY;
					stack = new ItemStack(group.getIcon());
					
					
					RenderHelper.enableGUIStandardItemLighting();
					if(hovered || (IGItemGroup.getCurrentSubGroup().equals(group))) {
						mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x + 1, y + 2);
					} else {
						mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x + 1, y + 1);
					}
					
					//Tool tip on hover
					if(hovered) {
						String name = group.name();
						String name_part = name.substring(0, 1).toUpperCase();
						String corrected_name = name_part + name.substring(1);
						List<String> tooltip = new ArrayList<String>();
						tooltip.add(corrected_name);
						//we divide the width by four to force the tooltip to the left, as it's on a lower layer then the items in the creative menu.
						GuiUtils.drawHoveringText(tooltip,mouseX,mouseY,(mc.currentScreen.width / 4),mc.currentScreen.height,80,mc.fontRenderer);
					}
		}
		
		@Override
		public void onPress() {
			if(!contained.itemList.isEmpty())
			if(contained.itemList.get(0).getItem() instanceof IGSubGroup) {
				super.onPress();
			}
		}
	}
}
