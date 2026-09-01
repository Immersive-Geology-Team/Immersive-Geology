/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.commands;

import com.igteam.immersivegeology.common.event.IGCommonForgeEvents;
import com.igteam.immersivegeology.common.event.VeinScanTask;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.server.command.EnumArgument;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;

public class IGFindMineralVeinCommand
{
	/**
	 * The seed pass that narrows a default search is pure arithmetic, so the radius barely costs it anything.
	 * An {@code absolute} search checks per chunk instead, and is capped separately by the scan itself.
	 */
	private static final int MAX_RADIUS = 256;
	private static final int MAX_RESULTS = 16;

	/** The {@code anyIG} form has no type argument to read. */
	private static final TypeResolver ANY_DEPOSIT = context -> null;

	private static final DynamicCommandExceptionType ERROR_UNKNOWN_METAL = new DynamicCommandExceptionType(
			name -> Component.literal("Unknown native metal: "+name));

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(
				Commands.literal("locate")
						.requires(source -> source.hasPermission(2))
						.then(Commands.literal("mineral")
								.then(Commands.argument("type", EnumArgument.enumArgument(MineralEnum.class))
										.then(searchArguments(context -> context.getArgument("type", MineralEnum.class)))))
						.then(Commands.literal("metal")
								.then(Commands.argument("type", StringArgumentType.word())
										.suggests((context, builder) -> {
											for(MetalEnum metal : MetalEnum.values())
											{
												if(metal.hasFlag(BlockCategoryFlags.ORE_BLOCK))
													builder.suggest(metal.name().toLowerCase(Locale.ROOT));
											}
											return builder.buildFuture();
										})
										.then(searchArguments(IGFindMineralVeinCommand::getNativeMetal))))
						.then(Commands.literal("anyIG")
								.then(searchArguments(ANY_DEPOSIT)))
		);
	}

	/**
	 * The {@code <radius> [absolute] [count]} tail every form of the command shares, with each optional argument
	 * executable in its own right so the shorter forms keep working.
	 */
	private static RequiredArgumentBuilder<CommandSourceStack, Integer> searchArguments(TypeResolver resolver)
	{
		return Commands.argument("radius", IntegerArgumentType.integer(0, MAX_RADIUS))
				.executes(context -> scan(context, resolver, false, 1))
				.then(Commands.argument("absolute", BoolArgumentType.bool())
						.executes(context -> scan(context, resolver, BoolArgumentType.getBool(context, "absolute"), 1))
						.then(Commands.argument("count", IntegerArgumentType.integer(1, MAX_RESULTS))
								.executes(context -> scan(context, resolver,
										BoolArgumentType.getBool(context, "absolute"),
										IntegerArgumentType.getInteger(context, "count")))));
	}

	private static int scan(CommandContext<CommandSourceStack> context, TypeResolver resolver, boolean absolute, int count)
			throws CommandSyntaxException
	{
		CommandSourceStack source = context.getSource();
		source.getPlayerOrException();
		MaterialInterface<?> type = resolver.resolve(context);
		int radius = IntegerArgumentType.getInteger(context, "radius");

		IGCommonForgeEvents.activeVeinScans.add(type!=null
				?VeinScanTask.forMaterial(source, source.getLevel(), type, radius, absolute, count)
				: VeinScanTask.forAnyDeposit(source, source.getLevel(), radius, absolute, count));
		return 1;
	}

	private static MaterialInterface<?> getNativeMetal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String typeName = StringArgumentType.getString(context, "type");
		return Arrays.stream(MetalEnum.values())
				.filter(metal -> metal.name().equalsIgnoreCase(typeName)&&metal.hasFlag(BlockCategoryFlags.ORE_BLOCK))
				.findFirst()
				.orElseThrow(() -> ERROR_UNKNOWN_METAL.create(typeName));
	}

	@FunctionalInterface
	private interface TypeResolver
	{
		@Nullable
		MaterialInterface<?> resolve(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
	}
}
