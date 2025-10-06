/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.network.msg;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.network.IMessage;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SmallChemicalReactorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SmallChemicalReactorLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.part.SmallChemicalReactorPart;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Objects;
import java.util.function.Supplier;

public class MessageSCRFail implements IMessage
{
	private final BlockPos pos;

	public MessageSCRFail(BlockPos pos)
	{
		this.pos = pos;
	}


	public MessageSCRFail(FriendlyByteBuf buf)
	{
		this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	@Override
	public void toBytes(FriendlyByteBuf buf)
	{
		buf.writeInt(this.pos.getX()).writeInt(this.pos.getY()).writeInt(this.pos.getZ());
	}

	@Override
	public void process(Supplier<Context> context)
	{
		NetworkEvent.Context ctx = context.get();
		if (ctx.getDirection().getReceptionSide() == LogicalSide.SERVER) {
			ctx.enqueueWork(() -> {
				ServerLevel world = ((ServerPlayer)Objects.requireNonNull(ctx.getSender())).serverLevel();
				if (world.isAreaLoaded(this.pos, 1)) {
					BlockState blockState = world.getBlockState(this.pos);
					if (blockState.getBlock() instanceof SmallChemicalReactorPart part) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity instanceof IMultiblockBE be) {
							be.getHelper().disassemble();
						}
						MutableBlockPos b = new MutableBlockPos();
						b.set(this.pos);
						for(int x = 0; x<=1; x++)
						{
							for(int z = 0; z<=1; z++)
							{
								for(int y = 0; y<4; y++)
								{
									world.setBlock(b.offset(x,y,z), MiscEnum.RustyMetal.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK).defaultBlockState(), 0);
								}
							}
						}
					}
				}

			});
		} else {
			ctx.enqueueWork(() -> {
				Level world = ImmersiveEngineering.proxy.getClientWorld();
				if (world != null) {
					BlockEntity tile = world.getBlockEntity(this.pos);
					if (tile instanceof IMultiblockBE iMultiblockBE) {
						iMultiblockBE.getHelper().markDisassembling();
					}
				}
			});
		}
	}
}
