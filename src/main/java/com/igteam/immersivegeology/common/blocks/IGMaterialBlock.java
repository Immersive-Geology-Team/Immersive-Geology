package com.igteam.immersivegeology.common.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersivegeology.api.util.IGMathHelper;
import com.igteam.immersivegeology.common.blocks.property.IGProperties;
import com.igteam.immersivegeology.common.util.BlockstateGenerator;
import com.igteam.immersivegeology.common.util.IGItemGrabber;
import com.igteam.immersivegeology.common.util.ItemJsonGenerator;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IColouredBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGMaterialBlock extends IGBaseBlock implements IColouredBlock
{
	protected Material material;
	protected MaterialUseType type;


	public IGMaterialBlock(Material material, MaterialUseType type) { this(material, type, ""); }

	public IGMaterialBlock(Material material, MaterialUseType type, String sub)
	{
		super(sub+"block_"+type.getName()+"_"+material.getName(), 
				Properties.create((type.getMaterial()==null?net.minecraft.block.material.Material.ROCK: type.getMaterial())),
				IGBlockMaterialItem.class, type.getSubGroup());

		this.material = material;
		this.type = type;
		if(itemBlock instanceof IGBlockMaterialItem)
		{
			((IGBlockMaterialItem)itemBlock).material=this.material;
			((IGBlockMaterialItem)itemBlock).subtype=this.type;
		}

		if(type.equals(MaterialUseType.ROCK))
		{
			if(material instanceof MaterialStoneBase)
			{
				MaterialStoneBase rockMat = (MaterialStoneBase) material;
				BlockstateGenerator.generateDefaultBlock(material, type, rockMat.getStoneType());
				ItemJsonGenerator.generateDefaultBlockItem(material, type, rockMat.getStoneType());
			}
		} else
			{
			BlockstateGenerator.generateDefaultBlock(material, type);
			ItemJsonGenerator.generateDefaultBlockItem(material, type);
		}
		
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		// TODO Auto-generated method stub
		ItemStack tool = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if(!player.isCreative() && !player.isSpectator() && this.canHarvestBlock(state, worldIn, pos, player))
		{
			if(type.equals(MaterialUseType.ROCK) && !tool.isEmpty())
			{
				boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;
				if (state.get(IGProperties.NATURAL) && !silk)
				{
					List<ItemStack> blockDrops = new ArrayList<>();
					int level = tool.getHarvestLevel(ToolType.PICKAXE, player, state);
					int effectiveLevel = Math.max(level-this.material.getBlockHarvestLevel(), 0);
					int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
					int dropAmount = Math.max(Math.min(8, 1+effectiveLevel), Math.min(8, Math.round((float)(IGMathHelper.nextInt(2+fortune, effectiveLevel))*( 1+player.getLuck() )) ) );
					blockDrops.add(new ItemStack(IGItemGrabber.getIGItem(MaterialUseType.CHUNK, this.material), dropAmount));
					for(ItemStack item : blockDrops)
					{
						worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(),item));
					}
				} else
					{
					worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this.itemBlock)));
				}
			} else
				{
				worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this.itemBlock)));
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Nullable
	@Override
	public ToolType getHarvestTool(BlockState state) { return ToolType.PICKAXE; }

	@Override
	public int getHarvestLevel(BlockState state) { return this.material.getBlockHarvestLevel(); }

	@Override
	public boolean hasCustomBlockColours()
	{
		return true;
	}

	@Override
	public int getRenderColour(BlockState blockState, @Nullable IBlockReader iBlockReader, @Nullable BlockPos blockPos, int i) { return material.getColor(0); }
}
