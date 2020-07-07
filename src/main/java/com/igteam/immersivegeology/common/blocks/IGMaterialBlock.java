package com.igteam.immersivegeology.common.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersivegeology.common.blocks.property.IGProperties;
import com.igteam.immersivegeology.common.util.BlockstateGenerator;
import com.igteam.immersivegeology.common.util.IGItemGrabber;
import com.igteam.immersivegeology.common.util.ItemJsonGenerator;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IColouredBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext.Builder;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGMaterialBlock extends IGBaseBlock implements IColouredBlock
{
	protected Material material;
	protected MaterialUseType type;


	public IGMaterialBlock(Material material, MaterialUseType type)
	{
		this(material, type, "");
	}

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

		if(type.equals(MaterialUseType.ROCK)) {
			if(material instanceof MaterialStoneBase) {
				MaterialStoneBase rockMat = (MaterialStoneBase) material;
				BlockstateGenerator.generateDefaultBlock(material, type, rockMat.getStoneType());
				ItemJsonGenerator.generateDefaultBlockItem(material, type, rockMat.getStoneType());
			}
		} else {
			BlockstateGenerator.generateDefaultBlock(material, type);
			ItemJsonGenerator.generateDefaultBlockItem(material, type);
		}
		
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		// TODO Auto-generated method stub
		if(!player.isCreative() && !player.isSpectator()) {
			if(type.equals(MaterialUseType.ROCK)) {
				if (state.get(IGProperties.NATURAL)) {
					List<ItemStack> blockDrops = new ArrayList<ItemStack>();
					blockDrops.add(new ItemStack(IGItemGrabber.getIGItem(MaterialUseType.CHUNK, this.material), Math.max(1, RANDOM.nextInt(4))));
					for(ItemStack item : blockDrops) { 
						worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(),item));
					}
				} else {
					worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this.itemBlock)));
				}
			} else {
				worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this.itemBlock)));
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	
	@Override
	public boolean hasCustomBlockColours()
	{
		return true;
	}

	@Override
	public int getRenderColour(BlockState blockState, @Nullable IBlockReader iBlockReader, @Nullable BlockPos blockPos, int i)
	{
		return material.getColor(0);
	}
}
