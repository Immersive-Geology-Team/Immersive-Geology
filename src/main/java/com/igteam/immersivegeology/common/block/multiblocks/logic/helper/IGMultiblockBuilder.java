/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic.helper;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.ComparatorManager;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent.StateWrapper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.component.MultiblockGui;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.multiblocks.gui.helper.IGMultiblockGUI;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMenuTypes.MultiblockContainer;
import net.minecraft.core.BlockPos;

public class IGMultiblockBuilder<S extends IMultiblockState>
		extends MultiblockRegistrationBuilder<S, IGMultiblockBuilder<S>>
{
	public IGMultiblockBuilder(IMultiblockLogic<S> logic, String name)
	{
		super(logic, IGLib.rl(name));
	}

	public IGMultiblockBuilder<S> gui(MultiblockContainer<S, ?> menu)
	{
		return component(new IGMultiblockGUI<>(menu));
	}

	public IGMultiblockBuilder<S> redstoneNoComputer(StateWrapper<S, RSState> getState, BlockPos... positions)
	{
		redstoneAware();
		return selfWrappingComponent(new RedstoneControl<>(getState, false, positions));
	}

	public IGMultiblockBuilder<S> redstone(StateWrapper<S, RSState> getState, BlockPos... positions)
	{
		redstoneAware();
		return selfWrappingComponent(new RedstoneControl<>(getState, positions));
	}

	public IGMultiblockBuilder<S> comparator(ComparatorManager<S> comparator)
	{
		withComparator();
		return super.selfWrappingComponent(comparator);
	}

	@Override
	public <CS, C extends IMultiblockComponent<CS> & StateWrapper<S, CS>>
	IGMultiblockBuilder<S> selfWrappingComponent(C extraComponent)
	{
		Preconditions.checkArgument(!(extraComponent instanceof ComparatorManager<?>));
		return super.selfWrappingComponent(extraComponent);
	}

	@Override
	protected IGMultiblockBuilder<S> self()
	{
		return this;
	}
}