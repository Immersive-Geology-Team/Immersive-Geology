package com.igteam.immersivegeology.common.items;

import blusunrize.immersiveengineering.client.ClientProxy;
import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.client.menu.helper.IIGSubGroupContained;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IGBaseItem extends Item implements IEItemInterfaces.IColouredItem, IIGSubGroupContained
{
	public String itemName;
	private int burnTime = -1;
	private boolean isHidden = false;

	protected ItemSubGroup subGroup;

	public IGBaseItem(String name)
	{
		this(name, new Properties());
	}

	public IGBaseItem(String name, Properties props)
	{
		super(props.group(ImmersiveGeology.IG_ITEM_GROUP));
		itemName=name;
		ResourceLocation registryName = createRegistryName();
		setRegistryName(registryName);
	}

    public IGBaseItem setBurnTime(int burnTime)
	{
		this.burnTime = burnTime;
		return this;
	}

	@Override
	public int getBurnTime(ItemStack itemStack)
	{
		return burnTime;
	}

	public boolean isHidden()
	{
		return isHidden;
	}

	public void hide()
	{
		isHidden = true;
	}

	public void unhide()
	{
		isHidden = false;
	}

	/*
	//Should be moved to items that actually need GUIs
	protected void openGui(PlayerEntity player, EquipmentSlotType slot)
	{
		ItemStack stack = player.getItemStackFromSlot(slot);
		NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider()
		{
			@Nonnull
			@Override
			public ITextComponent getDisplayName()
			{
				return new StringTextComponent("");
			}

			@Nullable
			@Override
			public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity)
			{
				return GuiHandler.createContainer(playerInventory, playerEntity.world, slot, stack, i);
			}
		}, buffer -> buffer.writeInt(slot.ordinal()));
	}
	 */

	@Override
	public boolean hasCustomProperties()
	{
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack)
	{
		return ClientProxy.itemFont;
	}

	@Override
	public ItemSubGroup getSubGroup()
	{
		return subGroup;
	}

	public ResourceLocation createRegistryName()
	{
		return new ResourceLocation(ImmersiveGeology.MODID, itemName);
	}
}
