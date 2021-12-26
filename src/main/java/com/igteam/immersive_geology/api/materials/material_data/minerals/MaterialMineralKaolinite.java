package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCraftingProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.api.tags.IGTags;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralKaolinite extends MaterialMineralBase {
	
	@Override
	public String getName()
	{
		return "kaolinite";
	}

	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}

	@Override
	public EnumMineralType getMineralType() {
		// TODO Auto-generated method stub
		return EnumMineralType.CLAY;
	}

	@Override
	public MaterialTypes getMaterialSubType() {
		// TODO Auto-generated method stub
		return MaterialTypes.STONE; //do not want in world ore bearing rocks!
	}
	
	@Override
	public LinkedHashSet<ElementProportion> getElements() {
		// TODO Auto-generated method stub
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM, 2),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON, 2),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 5)
		));
	}

	@Override
	public Rarity getRarity() {
		// TODO Auto-generated method stub
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint() {
		// TODO Auto-generated method stub
		return 9999;
	}

	@Override
	public int getMeltingPoint() {
		// TODO Auto-generated method stub
		return 930;
	}
	
	public static int baseColor = 0xE5DFD1;
	
	@Override
	public int getColor(int temperature) {
		// TODO Auto-generated method stub
		return baseColor;
	}

	@Override
	public float getHardness() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public float getMiningResistance() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float getBlastResistance() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float getDensity() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return MaterialEnum.Aluminium;
	}

	@Override
	public IGMaterialProcess getProcessingMethod() {
		IGCraftingProcessingMethod refractory_clay = new IGCraftingProcessingMethod("create_refractory", IGTags.getTagsFor(this).clay);
		refractory_clay.setOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Refractory.getMaterial(), MaterialUseType.CLAY), 2));
		refractory_clay.setShapeless(
				IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CLAY),
				IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CLAY),
				IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CLAY),
				Items.CLAY_BALL
		);
		IGCraftingProcessingMethod refractory_clay2 = new IGCraftingProcessingMethod("create_refractory", IGTags.getTagsFor(this).clay);
		refractory_clay2.setOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Refractory.getMaterial(), MaterialUseType.CLAY), 2));
		refractory_clay2.setShapeless(
				IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CLAY),
				IGRegistrationHolder.getItemByMaterial(MaterialEnum.Alumina.getMaterial(), MaterialUseType.DUST)
		);

		inheritedProcessingMethods.add(refractory_clay);
		inheritedProcessingMethods.add(refractory_clay2);

		return super.getProcessingMethod();
	}
}
