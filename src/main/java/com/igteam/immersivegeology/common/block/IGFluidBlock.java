/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class IGFluidBlock extends LiquidBlock
{
	private final MaterialInterface<?> material;
	public IGFluidBlock(Supplier<? extends FlowingFluid> fluid, MaterialInterface<?> material, Properties pProperties)
	{
		super(fluid, pProperties);
		this.material = material;
	}

	@Override
	public @NotNull MutableComponent getName()
	{
		return Component.translatable(getDescriptionId(), I18n.get("material.immersivegeology." + material.getName()));
	}

	@Nonnull
	@Override
	public FluidState getFluidState(@Nonnull BlockState state)
	{
		FluidState baseState = super.getFluidState(state);
		for(Property<?> prop : baseState.getProperties())
			if(state.hasProperty(prop))
				baseState = withCopiedValue(prop, baseState, state);
		return baseState;
	}

	public static <T extends StateHolder<?, T>, S extends Comparable<S>>
	T withCopiedValue(Property<S> prop, T oldState, StateHolder<?, ?> copyFrom)
	{
		return oldState.setValue(prop, copyFrom.getValue(prop));
	}

	public boolean isTranslucent()
	{
		return (this.material instanceof ChemicalEnum);
	}
}
