package com.igteam.immersivegeology.common.items;

import blusunrize.immersiveengineering.api.tool.ITool;
import blusunrize.immersiveengineering.common.items.IEItemInterfaces.IColouredItem;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.interfaces.IBindingMaterial;
import com.igteam.immersivegeology.api.interfaces.IHandleMaterial;
import com.igteam.immersivegeology.api.interfaces.IHeadMaterial;
import com.igteam.immersivegeology.api.interfaces.ITipMaterial;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.materials.MaterialEmpty;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

import static com.igteam.immersivegeology.common.items.IGMaterialResourceItem.hasShiftDown;

public abstract class IGModularToolItem extends IGBaseItem implements ITool, IColouredItem
{

	protected static String[] StrMaterials = {"head_material", "binding_material", "handle_material", "tip_material"};
	protected static String[] StrColors = {"head_color", "binding_color", "handle_color", "tip_color"};

	public IGModularToolItem(String name)
	{
		super(name);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		Material toolMaterial = getToolMaterial(stack, 0);
		if(toolMaterial instanceof MaterialEmpty){
			return 1;
		} else {
			int durability = ((IHeadMaterial) toolMaterial).getHeadDurability() + ((IBindingMaterial) getToolMaterial(stack, 1)).getBindingDurability() +
					((IHandleMaterial) getToolMaterial(stack, 2)).getHandleDurability();
			durability += ((ITipMaterial) getToolMaterial(stack, 3)).getTipDurability();
			durability = (int) ((double) durability * ((IBindingMaterial) getToolMaterial(stack, 1)).getBindingMultiplier() *
					((IHandleMaterial) getToolMaterial(stack, 2)).getHandleMultiplier());
			return durability;
		}
	}

	public static Material getToolMaterial(ItemStack stack, int part)
	{
		return EnumMaterials.filterByName(ItemNBTHelper.getString(stack, StrMaterials[part])).material;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
		if (!p_179218_2_.isRemote && p_179218_3_.getBlockHardness(p_179218_2_, p_179218_4_) != 0.0F) {
			p_179218_1_.damageItem(1, p_179218_5_, (p_lambda$onBlockDestroyed$2_0_) -> {
				p_lambda$onBlockDestroyed$2_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
			});
		}

		return true;
	}

	@Override
	public boolean isTool(ItemStack itemStack)
	{
		return !itemStack.getToolTypes().isEmpty();
	}

	@Override
	public boolean hasCustomItemColours()
	{
		return true;
	}

	@Override
	public int getColourForIEItem(ItemStack stack, int pass)
	{
		//0 for head, 1 for handle
		return getColorCache(stack, pass);
	}

	//true for head, false for handle
	public int getColorCache(ItemStack stack, int part)
	{
		String tagName = StrColors[part];
		//Adds quick access to the color, greatly improves performance
		if(!ItemNBTHelper.hasKey(stack, tagName))
			stack.getOrCreateTag().putInt(tagName, getToolMaterial(stack, part).getColor(0));
		return ItemNBTHelper.getInt(stack, tagName);
	}

	@Override
	public int getHarvestLevel(ItemStack stack, ToolType toolType, @Nullable PlayerEntity player, @Nullable BlockState state)
	{
		return ((IHeadMaterial)getToolMaterial(stack, 0)).getHeadMiningLevel();
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.addInformation(stack, world, list, flag);
		StringTextComponent space = new StringTextComponent("");
		StringTextComponent text = new StringTextComponent("");

		StringTextComponent text2 = new StringTextComponent("");
		StringTextComponent text3 = new StringTextComponent("");
		StringTextComponent text4 = new StringTextComponent("");

		list.add(space);

		if(hasShiftDown()){
			Material head_mat = getToolMaterial(stack, 0);
			Material handle_mat = getToolMaterial(stack, 1);
			Material binding_mat = getToolMaterial(stack, 2);
			Material tip_mat = getToolMaterial(stack, 3);

			text.appendText("Head Material: <hexcol=" + head_mat.getColor(0) +":" + head_mat.getName().substring(0,1).toUpperCase() + head_mat.getName().substring(1) +">");
			text2.appendText("Handle Material: <hexcol=" + handle_mat.getColor(0) +":" + handle_mat.getName().substring(0,1).toUpperCase() + handle_mat.getName().substring(1) +">");
			text3.appendText("Binding Material:  <hexcol=" + binding_mat.getColor(0) +":" + binding_mat.getName().substring(0,1).toUpperCase() + binding_mat.getName().substring(1) +">");
			if(!(tip_mat instanceof MaterialEmpty)) {
				text4.appendText("Tip Material:  <hexcol=" + tip_mat.getColor(0) +":" + tip_mat.getName().substring(0,1).toUpperCase() + tip_mat.getName().substring(1) +">");
				list.add(text4);
			}
			list.add(text2);
			list.add(text3);
		} else {
			text.appendText("Hold Sneak for Info");
		}
		list.add(text);
	}

	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return ((IHeadMaterial)getToolMaterial(stack, 0)).getHeadMiningSpeed();
	}

}
