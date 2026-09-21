package com.igteam.immersivegeology.common.block.multiblocks.shim.process;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockLevel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class MultiblockProcessor<R extends MultiblockRecipe, CTX extends ProcessContext<R>>
{
	public interface ProcessLoader<R extends MultiblockRecipe, CTX extends ProcessContext<R>>
	{
		MultiblockProcess<R, CTX> load(NBTTagCompound nbt);
	}

	private final List<MultiblockProcess<R, CTX>> processQueue = new ArrayList<>();
	private final int maxQueueLength;
	private final float minDelayBetween;
	private final int maxProcessPerTick;
	private final Runnable markDirty;

	public MultiblockProcessor(int maxQueueLength, float minDelayBetween, int maxProcessPerTick, Runnable markDirty)
	{
		this.maxQueueLength = maxQueueLength;
		this.minDelayBetween = minDelayBetween;
		this.maxProcessPerTick = maxProcessPerTick;
		this.markDirty = markDirty;
	}

	public List<MultiblockProcess<R, CTX>> getQueue()
	{
		return processQueue;
	}

	public boolean addProcessToQueue(MultiblockProcess<R, CTX> process, boolean simulate)
	{
		if(processQueue.size() >= maxQueueLength) return false;
		if(minDelayBetween > 0)
			for(MultiblockProcess<R, CTX> queued : processQueue)
				if(queued.processTick < minDelayBetween) return false;

		if(!simulate)
		{
			processQueue.add(process);
			markDirty.run();
		}
		return true;
	}

	public boolean tickServer(CTX context, IMultiblockLevel level, boolean enabled)
	{
		if(!enabled||processQueue.isEmpty()) return false;

		int ticked = 0;
		boolean active = false;

		Iterator<MultiblockProcess<R, CTX>> iterator = processQueue.iterator();
		while(iterator.hasNext()&&ticked < maxProcessPerTick)
		{
			MultiblockProcess<R, CTX> process = iterator.next();
			if(process.canProcess(context))
			{
				process.doProcessTick(context, level);
				active = true;
				ticked++;
			}
			if(process.clearProcess)
			{
				iterator.remove();
				markDirty.run();
			}
		}
		return active;
	}

	public NBTTagList toNBT()
	{
		NBTTagList list = new NBTTagList();
		for(MultiblockProcess<R, CTX> process : processQueue)
		{
			NBTTagCompound tag = new NBTTagCompound();
			process.writeToNBT(tag);
			list.appendTag(tag);
		}
		return list;
	}

	public void fromNBT(NBTTagList list, ProcessLoader<R, CTX> loader)
	{
		processQueue.clear();
		if(list==null) return;

		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			MultiblockProcess<R, CTX> process = loader.load(tag);
			if(process==null) continue;
			process.readFromNBT(tag);
			processQueue.add(process);
		}
	}
}
