package com.igteam.immersivegeology.common.blocks;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.client.ClientProxy;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.client.menu.helper.IIGSubGroupContained;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class IGBlockItem extends BlockItem implements IIGSubGroupContained
{

	protected ItemSubGroup subGroup;
	private int burnTime;

	public IGBlockItem(Block b, Item.Properties props, ItemSubGroup sub)
	{
		super(b, props.group(ImmersiveGeology.IG_ITEM_GROUP));
		this.subGroup = sub;
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		return getBlock().getTranslationKey();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack)
	{
		return ClientProxy.itemFont;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced)
	{
		if(getBlock() instanceof IIGBlock)
		{
			IIGBlock igBlock = (IIGBlock)getBlock();
			if(igBlock.hasFlavour())
			{
				String flavourKey = Lib.DESC_FLAVOUR+igBlock.getNameForFlavour();
				tooltip.add(new TranslationTextComponent(flavourKey).setStyle(new Style().setColor(TextFormatting.GRAY)));
			}
		}
		super.addInformation(stack, world, tooltip, advanced);
		if(ItemNBTHelper.hasKey(stack, "energyStorage"))
			tooltip.add(new TranslationTextComponent(Lib.DESC_INFO+"energyStored",
					ItemNBTHelper.getInt(stack, "energyStorage")));
		if(ItemNBTHelper.hasKey(stack, "tank"))
		{
			FluidStack fs = FluidStack.loadFluidStackFromNBT(ItemNBTHelper.getTagCompound(stack, "tank"));
			if(fs!=null)
				tooltip.add(new TranslationTextComponent(Lib.DESC_INFO+"fluidStored",
						fs.getDisplayName(), fs.getAmount()));
		}
	}


	public IGBlockItem setBurnTime(int burnTime)
	{
		this.burnTime = burnTime;
		return this;
	}

	@Override
	public int getBurnTime(ItemStack itemStack)
	{
		return this.burnTime;
	}

	@Override
	protected boolean placeBlock(BlockItemUseContext context, BlockState newState)
	{
		Block b = getBlock();
		if(b instanceof IGBaseBlock)
		{
			IGBaseBlock igBlock = (IGBaseBlock)b;
			if(!igBlock.canIGBlockBePlaced(newState, context))
				return false;
			boolean ret = super.placeBlock(context, newState);
			if(ret)
				igBlock.onIGBlockPlacedBy(context, newState);
			return ret;
		}
		else
			return super.placeBlock(context, newState);
	}

	@Override
	public ItemSubGroup getSubGroup()
	{
		// TODO Auto-generated method stub
		return subGroup;
	}

	public static class BlockItemIENoInventory extends IGBlockItem
	{
		public BlockItemIENoInventory(Block b, Properties props)
		{
			super(b, props, ItemSubGroup.misc);
		}

		@Nullable
		@Override
		public CompoundNBT getShareTag(ItemStack stack)
		{
			CompoundNBT ret = super.getShareTag(stack);
			if(ret!=null)
			{
				ret = ret.copy();
				ret.remove("inventory");
			}
			return ret;
		}
	}
}
