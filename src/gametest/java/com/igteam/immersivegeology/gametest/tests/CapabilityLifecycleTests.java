/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.gametest.tests;

import com.igteam.immersivegeology.common.block.entity.cable.IGEnergyPipeEntity;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CapabilityLifecycleTests
{
	private static final String LEAK_MESSAGE = "is still marked as valid";

	private static final List<Capability<?>> WATCHED = List.of(
			ForgeCapabilities.FLUID_HANDLER, ForgeCapabilities.ITEM_HANDLER, ForgeCapabilities.ENERGY
	);

	public static List<TestFunction> all()
	{
		List<TestFunction> tests = new ArrayList<>();
		for(IMultiblock mb : MultiblockHandler.getMultiblocks())
			if(mb instanceof IGTemplateMultiblock ig)
				tests.add(new TestFunction(
						"capability", ig.getUniqueName().getPath()+"_teardown",
						ig.getUniqueName().toString(),
						400, 0, true, helper -> capabilityTeardown(helper, ig)
				));
		return tests;
	}

	private static void capabilityTeardown(GameTestHelper helper, IGTemplateMultiblock multiblock)
	{
		BlockPos trigger = multiblock.getTriggerOffset().above();
		Vec3i size = multiblock.getSize(helper.getLevel());

		CommonTests.formMultiblock(multiblock, helper);
		helper.assertBlockPresent(multiblock.getBlock(), trigger);
		Anchor anchor = attachEnergyPipe(helper, size);

		LeakWatcher watcher = LeakWatcher.attach();
		try
		{
			helper.runAfterDelay(20, () -> {
				try
				{
					if(anchor!=null) poll(helper, anchor);

					List<Held> held = captureCapabilities(helper, size);
					if(held.isEmpty())
					{
						watcher.detach();
						helper.succeed();
						return;
					}

					helper.setBlock(trigger, Blocks.AIR);

					helper.runAfterDelay(10, () -> {
						try
						{
							if(anchor!=null) poll(helper, anchor);

							for(Held h : held)
								if(h.provider().isRemoved()&&h.optional().isPresent())
									helper.fail(h.describe()+" outlived its block entity - a capability "
											+"was re-registered during teardown");

							List<String> leaks = watcher.captured();
							if(!leaks.isEmpty())
								helper.fail("Leaked capability reported: "+leaks.get(0));

							helper.succeed();
						}
						finally
						{
							watcher.detach();
						}
					});
				}
				catch(Throwable t)
				{
					watcher.detach();
					throw t;
				}
			});
		}
		catch(Throwable t)
		{
			watcher.detach();
			throw t;
		}
	}

	private static void poll(GameTestHelper helper, Anchor anchor)
	{
		if(helper.getBlockEntity(anchor.pipePos()) instanceof IGEnergyPipeEntity pipe)
			pipe.updateConnectionByte(anchor.towardsReactor());
	}

	@Nullable
	private static Anchor attachEnergyPipe(GameTestHelper helper, Vec3i size)
	{
		Block pipeBlock = findEnergyPipeBlock();
		if(pipeBlock == null)
			return null;

		for(BlockPos pos : BlockPos.betweenClosed(BlockPos.ZERO, new BlockPos(size).offset(1, 1, 1)))
		{
			BlockEntity be = helper.getBlockEntity(pos);
			if(be == null)
				continue;

			for(Direction side : Direction.values())
			{
				if(!be.getCapability(ForgeCapabilities.ENERGY, side).isPresent())
					continue;

				BlockPos pipePos = pos.relative(side);
				if(!helper.getBlockState(pipePos).isAir())
					continue;

				helper.setBlock(pipePos, pipeBlock);
				return new Anchor(pipePos, side.getOpposite());
			}
		}
		return null;
	}

	@Nullable
	private static Block findEnergyPipeBlock()
	{
		List<MaterialInterface<?>> materials = new ArrayList<>();
		Collections.addAll(materials, MiscEnum.values());
		Collections.addAll(materials, MetalEnum.values());
		Collections.addAll(materials, ChemicalEnum.values());
		Collections.addAll(materials, MineralEnum.values());
		Collections.addAll(materials, StoneEnum.values());

		for(MaterialInterface<?> material : materials)
			if(material.hasFlag(BlockCategoryFlags.ENERGY_PIPE))
				return material.getBlock(BlockCategoryFlags.ENERGY_PIPE);
		return null;
	}

	private static List<Held> captureCapabilities(GameTestHelper helper, Vec3i size)
	{
		List<Held> held = new ArrayList<>();
		for(BlockPos pos : BlockPos.betweenClosed(BlockPos.ZERO, new BlockPos(size).offset(1, 1, 1)))
		{
			BlockEntity be = helper.getBlockEntity(pos);
			if(be == null||be instanceof IGEnergyPipeEntity)
				continue;

			for(Capability<?> cap : WATCHED)
				for(Direction side : Direction.values())
				{
					LazyOptional<?> opt = be.getCapability(cap, side);
					if(opt.isPresent())
						held.add(new Held(be, cap, side, opt));
				}
		}
		return held;
	}

	private record Anchor(BlockPos pipePos, Direction towardsReactor) {}

	private record Held(BlockEntity provider, Capability<?> capability, Direction side, LazyOptional<?> optional)
	{
		String describe()
		{
			return capability.getName()+" on "+provider.getClass().getSimpleName()
					+" at "+provider.getBlockPos()+" side "+side;
		}
	}

	private static final class LeakWatcher extends AbstractAppender
	{
		private final List<String> messages = new CopyOnWriteArrayList<>();

		private LeakWatcher()
		{
			super("IGCapabilityLeakWatcher", null, null, false, Property.EMPTY_ARRAY);
		}

		static LeakWatcher attach()
		{
			LeakWatcher watcher = new LeakWatcher();
			watcher.start();
			LoggerContext context = (LoggerContext)LogManager.getContext(false);
			context.getConfiguration().addLoggerAppender(context.getRootLogger(), watcher);
			return watcher;
		}

		void detach()
		{
			LoggerContext context = (LoggerContext)LogManager.getContext(false);
			context.getRootLogger().removeAppender(this);
			stop();
		}

		List<String> captured()
		{
			return Collections.unmodifiableList(messages);
		}

		@Override
		public void append(LogEvent event)
		{
			if(event.getLevel().isMoreSpecificThan(Level.WARN))
			{
				String rendered = event.getMessage().getFormattedMessage();
				if(rendered.contains(LEAK_MESSAGE))
					messages.add(rendered);
			}
		}
	}
}
