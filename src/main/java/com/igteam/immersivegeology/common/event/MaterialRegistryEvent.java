package com.igteam.immersivegeology.common.event;

import net.minecraftforge.eventbus.api.Event;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class MaterialRegistryEvent extends Event
{
	@Override
	public boolean isCancelable()
	{
		return false;
	}

	@Override
	public boolean hasResult()
	{
		return false;
	}

	public MaterialRegistryEvent()
	{

	}
}
