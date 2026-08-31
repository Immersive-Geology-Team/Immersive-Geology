/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.fluid;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class IGFluidType extends FluidType
{
	private final BlockCategoryFlags category;
	private final MaterialInterface<?> base;
	private final @Nullable MaterialInterface<?> overlay;
	private final IGFluid fluid;

	public IGFluidType(IGFluid fluid, MaterialInterface<?> base, @Nullable MaterialInterface<?> overlay, BlockCategoryFlags category)
	{
		super(base.getFluidProperties());
		this.base = base;
		this.overlay = overlay;
		this.category = category;
		this.fluid = fluid;
	}

	@Override
	public Component getDescription()
	{
		List<Component> materialList = new ArrayList<>();
		String type = "";

		MaterialInterface<?> baseMaterial = base;
		MaterialInterface<?> overlayMaterial = overlay;

		if(baseMaterial instanceof MaterialMetal){
			materialList.add(Component.translatable("material.immersivegeology.fluid_type.molten"));
			type = "_molten";
		}

		if(baseMaterial instanceof MaterialChemical chemical_base)
		{
			if(chemical_base.hasComplexNamingScheme())
			{
				materialList.add(Component.translatable("component.immersivegeology." + baseMaterial.getName()));
				materialList.add(Component.translatable("component.immersivegeology." + overlayMaterial.getName()));
				return Component.translatable("fluid.immersivegeology." + category.getName().toLowerCase(Locale.ROOT) + type, materialList.toArray());
			} else
			{
				materialList.add(Component.translatable("material.immersivegeology."+overlayMaterial.getName()));
				materialList.add(Component.translatable("component.immersivegeology."+baseMaterial.getName()));
			}
		}

		if (baseMaterial != null) {
			materialList.add(Component.translatable("material.immersivegeology." + baseMaterial.getName()));
		}
		if (overlayMaterial != null) {
			materialList.add(Component.translatable("material.immersivegeology." + overlayMaterial.getName()));
		}
		if(category.equals(BlockCategoryFlags.SLURRY)) materialList.add(Component.translatable("material.immersivegeology.fluid_type.clean_slurry"));
		if(category.equals(BlockCategoryFlags.CLOUDY_SLURRY)) materialList.add(Component.translatable("material.immersivegeology.fluid_type.cloudy_slurry"));

		return Component.translatable("fluid.immersivegeology." + category.getName().toLowerCase(Locale.ROOT) + type, materialList.toArray());
	}

	@Override
	public Component getDescription(FluidStack stack)
	{
		List<Component> materialList = new ArrayList<>();
		String type = "fluid";
		MaterialInterface<?> baseMaterial = base;
		MaterialInterface<?> overlayMaterial = overlay;
		if(baseMaterial instanceof MetalEnum)
		{
			type = "fluid_molten";
		}

		if(baseMaterial instanceof ChemicalEnum chemical_base && overlayMaterial != null)
		{
			type = category.getName().toLowerCase(Locale.ROOT);
			if(chemical_base.hasComplexNamingScheme())
			{
				materialList.add(Component.translatable("component.immersivegeology."+baseMaterial.getName()));
				materialList.add(Component.translatable("component.immersivegeology."+overlayMaterial.getName()));
				type = category.equals(BlockCategoryFlags.CLOUDY_SLURRY) ? "cloudy_complex_slurry" : "complex_slurry";
			}
			else
			{
				materialList.add(Component.translatable("material.immersivegeology."+overlayMaterial.getName()));
				materialList.add(Component.translatable("component.immersivegeology."+baseMaterial.getName()));
			}
		} else {
			materialList.add(Component.translatable("material.immersivegeology." + baseMaterial.getName()));
		}

		return Component.translatable("fluid.immersivegeology." + type, materialList.toArray());
	}

	public void initializeClient(Consumer< IClientFluidTypeExtensions > consumer) {
		consumer.accept(getFluidExtendedProperties(category));
	}

	public IClientFluidTypeExtensions getFluidExtendedProperties(BlockCategoryFlags flag)
	{
		IGFluidType fluid = this;
		return new IClientFluidTypeExtensions()
		{
			@Override
			public int getTintColor()
			{
				return (fluid.overlay != null ? fluid.overlay.getColor(flag, 0) : fluid.base.getColor(flag, 0));
			}

			@Override
			public ResourceLocation getStillTexture()
			{
				return fluid.base.hasFlag(MaterialFlags.IS_MOLTEN_METAL) ? new ResourceLocation(IGLib.MODID, "block/fluid/molten_still") : new ResourceLocation(IGLib.MODID, "block/fluid/default_still");
			}

			@Override
			public ResourceLocation getFlowingTexture()
			{
				return fluid.base.hasFlag(MaterialFlags.IS_MOLTEN_METAL) ? new ResourceLocation(IGLib.MODID, "block/fluid/molten_flow") : new ResourceLocation(IGLib.MODID, "block/fluid/default_flowing");
			}

			@Override
			public void renderOverlay(Minecraft mc, PoseStack poseStack)
			{

			}
		};
	}
}
