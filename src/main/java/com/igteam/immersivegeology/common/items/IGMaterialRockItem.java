package com.igteam.immersivegeology.common.items;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersivegeology.common.materials.EnumOreBearingMaterials;
import com.igteam.immersivegeology.common.util.IGItemGrabber;
import com.igteam.immersivegeology.common.util.ItemJsonGenerator;

import net.minecraft.item.ItemStack;

/**
 * Created by Pabilo8 on 28-03-2020.
 */
public class IGMaterialRockItem extends IGMaterialResourceItem
{
	
	private final EnumOreBearingMaterials oreType;
	private final Material baseType;
	
	public IGMaterialRockItem(Material mat, MaterialUseType type, EnumOreBearingMaterials oreType)
	{
		super(mat, type);
		this.oreType = oreType;
		this.baseType = mat;

		if(type.equals(MaterialUseType.ORE_CHUNK)){
			
			this.setRegistryName("item_"+ type.getName() + "_" + mat.getName() + "_"+ oreType.toString().toLowerCase());
			this.itemName = "item."+ type.getName() + "." + mat.getName() + "_"+ oreType.toString().toLowerCase() + ".name";
			IGItemGrabber.inputNewOreItem(type, mat, oreType, this);
			if(mat instanceof MaterialStoneBase) {
				ItemJsonGenerator.generateOreItem(mat, type, oreType);
			}
		}
		
	}

	
	@Override
	public int getColourForIEItem(ItemStack stack, int pass)
	{
		if(pass==0) {
			return baseType.getColor(0);
		} else {
			return oreType.getColor();
		}
	}

	@Override
	public boolean hasCustomItemColours() {
		// TODO Auto-generated method stub
		return true;
	}
	 
}
