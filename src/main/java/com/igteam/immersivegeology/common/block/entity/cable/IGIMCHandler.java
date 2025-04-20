/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.cable;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.InterModComms.IMCMessage;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

// Based on the IEIMCHandler by BluSunrize
public class IGIMCHandler
{
	private static final HashMap<String, Consumer<IMCMessage>> MESSAGE_HANDLERS = new HashMap<>();
	public static void init()
	{
		MESSAGE_HANDLERS.put("energypipe_cover", imcMessage -> {
			Predicate<Block> func = (Predicate<Block>)imcMessage.messageSupplier().get();
			IGEnergyPipeEntity.validPipeCovers.add(func);
		});

		MESSAGE_HANDLERS.put("energypipe_cover_climb", imcMessage -> {
			Predicate<Block> func = (Predicate<Block>)imcMessage.messageSupplier().get();
			IGEnergyPipeEntity.climbablePipeCovers.add(func);
		});
	}

	public static void handleIMCMessages(Stream<IMCMessage> messages)
	{
		messages.forEach(message -> {
			if(MESSAGE_HANDLERS.containsKey(message.method()))
			{
				Consumer<IMCMessage> handler = MESSAGE_HANDLERS.get(message.method());
				handler.accept(message);
			}
		});
	}
}
