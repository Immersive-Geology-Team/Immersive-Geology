package com.igteam.immersivegeology.client.menu.handler;

import java.util.ArrayList;
import java.util.List;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.client.menu.IGItemGroup;
import com.igteam.immersivegeology.client.menu.helper.IGSubGroup;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.items.IGItems;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen.CreativeContainer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.config.GuiUtils;

public class CreativeMenuHandler {
	private static final ResourceLocation CEX_GUI_TEXTURES = new ResourceLocation(ImmersiveGeology.MODID,"textures/gui/creative_expansion.png");
	

	@SubscribeEvent
	public void drawScreen(DrawScreenEvent.Pre event) {
		Screen screen = event.getGui();
		if(screen instanceof CreativeScreen) {
			CreativeScreen gui = (CreativeScreen) screen; 
			int i = (int) (gui.getGuiLeft() - Math.floor(136*1.425));
			
			if(!gui.getContainer().itemList.isEmpty())
			if(gui.getContainer().itemList.get(0).getItem() instanceof IGSubGroup) {
				Minecraft.getInstance().textureManager.bindTexture(CEX_GUI_TEXTURES);
				GlStateManager.color3f(1F, 1F, 1F);
				
				AbstractGui.blit(i + 166, gui.getGuiTop(), 0, 0, 29, 136, 256, 256);
			}
			
		}
	}
	
	@SubscribeEvent
    public void openGui(InitGuiEvent.Post event) {
		Screen screen = event.getGui();
		if(screen instanceof CreativeScreen) {
			CreativeScreen gui = (CreativeScreen) screen; 

			int i = (int) (gui.getGuiLeft() - Math.floor(136*1.425));
			int j = (gui.height - 195) / 2; 

			for(int iteration = 0; iteration < ItemSubGroup.values().length; iteration++) {
				ItemSubGroup currentGroup = ItemSubGroup.values()[iteration];
				event.addWidget(new CreativeMenuButton(gui.getContainer(), iteration, i + 166 + 7, j + 46 + (23 * iteration), (onPress) -> {
						gui.getContainer().itemList.clear();
						
						IGItemGroup.updateSubGroup(currentGroup);
						
						gui.getContainer().inventorySlots.forEach((slot) -> (slot).putStack(ItemStack.EMPTY));
						gui.getContainer().itemList.addAll(IGItemGroup.getCurrentList());
						for(int k = 0; k < gui.getContainer().itemList.size(); k++) {
							ItemStack stack = gui.getContainer().itemList.get(k);
							gui.getContainer().inventorySlots.get(k).putStack(stack);
						}						
					;}));	
			}
		}
	}
	
	private static int selectedSubTabID = 0;
	public static class CreativeMenuSidebar extends Widget {
		
		public CreativeContainer contained;
		public CreativeMenuSidebar(CreativeContainer container, int x, int y) {
			super(x, y, "");
			this.x = x;
			this.y = y;
			this.width = 29;
			this.height = 136;
			this.contained = container;
		}
		
		@Override
		public void render(int mouseX, int mouseY, float pticks) {
			if(contained.itemList.get(0).getItem() instanceof IGSubGroup) {
				Minecraft.getInstance().textureManager.bindTexture(CEX_GUI_TEXTURES);
				GlStateManager.color3f(1F, 1F, 1F);
				AbstractGui.blit(x, y, 0, 0, 29, 136, 256, 256);
			}
		}
		
	}
	
	public static class CreativeMenuButton extends Button {

		public CreativeContainer contained;
		public int subId;
		public CreativeMenuButton(CreativeContainer container, int subId, int x, int y, IPressable onPress) {
			super(18,18,x, y, "Tooltip?", onPress);
			this.width = 18;
			this.height = 18;
			this.x = x;
			this.y = y;
			this.contained = container;
			this.subId = subId;
		}
		
		@Override
		public void renderButton(int mouseX, int mouseY, float pticks) {
			if(!contained.itemList.isEmpty()) {
				if(contained.itemList.get(0).getItem() instanceof IGSubGroup) {
					Minecraft mc = Minecraft.getInstance();
					Minecraft.getInstance().textureManager.bindTexture(CEX_GUI_TEXTURES);
					GlStateManager.color3f(1F, 1F, 1F);
					
					boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
					if(selectedSubTabID == subId) {
						hovered = true;
					}
					AbstractGui.blit(x, y, (hovered ? 29 : 47), 0, width, height, 256, 256);
					
					ItemStack stack = ItemStack.EMPTY;
					
					//need a better method, but for some reason getting the reference from ItemSubGroups doesn't display.
					switch(subId) {
						case 0:
							stack = new ItemStack(Items.COAL_ORE);
						break;
						case 1:
							stack = new ItemStack(Items.IRON_INGOT);
						break;
						case 2:
							stack = new ItemStack(Items.PISTON);
						break;
						case 3:
							stack = new ItemStack(Items.OAK_SIGN);
						break;
					}
					
					RenderHelper.enableGUIStandardItemLighting();
					
					if(!hovered) {
							mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x + 1, y + 1);
					} else {
						mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x + 1, y + 2);
						if(selectedSubTabID != subId) {
							String name = ItemSubGroup.values()[subId].name();
							String name_part = name.substring(0, 1).toUpperCase();
							String corrected_name = name_part + name.substring(1);
							List<String> tooltip = new ArrayList<String>();
							tooltip.add(corrected_name);
							//we divide the width by four to force the tooltip to the left, as it's on a lower layer then the items in the creative menu.
							GuiUtils.drawHoveringText(tooltip,mouseX,mouseY,(mc.currentScreen.width / 4),mc.currentScreen.height,80,mc.fontRenderer);
						}
					}
				}
			}
		}
		
		@Override
		public void onPress() {
			if(contained.itemList.get(0).getItem() instanceof IGSubGroup) {
				selectedSubTabID = subId;
				super.onPress();
			}
		}
		
	}
}
