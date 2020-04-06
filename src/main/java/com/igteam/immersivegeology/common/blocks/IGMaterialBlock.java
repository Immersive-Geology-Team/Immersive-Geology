package com.igteam.immersivegeology.common.blocks;

import java.io.IOException;
import java.util.HashMap;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.MaterialUseType.UseCategory;
import com.igteam.immersivegeology.common.util.JsonGenerator;
import com.igteam.immersivegeology.api.materials.Material;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGMaterialBlock extends IGBaseBlock
{
	
	private IGBlockMaterialItem itemBlockMat;
	
	public IGMaterialBlock(MaterialUseType m) {
		super(m.getName(),Properties.create((m.getMaterial() == null ? net.minecraft.block.material.Material.ROCK : m.getMaterial())),m.getSubGroup());
		
		if(m.getCategory() == UseCategory.MATERIAL_BLOCK) {
			this.itemBlockMat = new IGBlockMaterialItem(this, new net.minecraft.item.Item.Properties().group(ImmersiveGeology.IGgroup),m.getSubGroup(),m);
			this.itemBlock = itemBlockMat;
		}
		
		//TODO set this up to work with a common model.
		JsonGenerator gen = new JsonGenerator();
		
		try {
			gen.generateDefaultBlock(m.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IGBlockMaterialItem getItemBlockMaterial() {
		// TODO Auto-generated method stub
		return itemBlockMat;
	}
	
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		// TODO Auto-generated method stub
		return new ItemStack(itemBlockMat);
	}
}
