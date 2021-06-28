package com.igteam.immersive_geology.common.world;


import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

//@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class IGInteractionHandler {

    @SubscribeEvent
    public static void InteractionEvent(PlayerInteractEvent event) {
        PlayerEntity playerEntity = event.getPlayer();
        BlockPos pos = event.getPos();
        World w = event.getWorld();
        TileEntity tileEntity = w.getTileEntity(pos);
        if (tileEntity != null)
        {
            LazyOptional<IFluidHandler> cap = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
            if (cap.resolve().isPresent()) {
                IFluidHandler handler = cap.resolve().get();
                for (int i = 0; i< handler.getTanks();i++) {
                    if ((handler.getFluidInTank(i).getFluid() instanceof IGFluid) &&
                            (!((IGFluid) handler.getFluidInTank(i).getFluid()).hasBucket())
                            && (event.getItemStack().getItem() == Items.BUCKET))
                    {
                        event.setCanceled(true);
                        break;
                    }

                }
            }
        }
    }

}
