package igteam.immersive_geology.common.world;

import blusunrize.immersiveengineering.api.shader.IShaderItem;
import blusunrize.immersiveengineering.api.shader.ShaderRegistry;
import blusunrize.immersiveengineering.common.blocks.metal.SheetmetalTankTileEntity;
import igteam.api.item.IGItemType;
import igteam.api.main.IGRegistryProvider;
import igteam.api.materials.pattern.ItemPattern;
import igteam.immersive_geology.common.fluid.IGFluid;
import igteam.immersive_geology.common.item.IGGenericItem;
import igteam.immersive_geology.common.item.distinct.GeologistPouch;
import igteam.immersive_geology.common.item.helper.IGItemStackHandler;
import igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.concurrent.atomic.AtomicReference;

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
                            (!((IGFluid) handler.getFluidInTank(i).getFluid()).hasFlask())
                            && (event.getItemStack().getItem() == Items.BUCKET))
                    {
                        event.setCanceled(true);
                        break;
                    }

                }
            }

            //FIXME -- ask IE guyz to provide API to get capabilites of multiblock as whole
            if (tileEntity instanceof SheetmetalTankTileEntity)
            {
                Fluid fluid =((SheetmetalTankTileEntity) tileEntity).master().tank.getFluid().getFluid();
                if ((fluid instanceof  IGFluid) && (((IGFluid) fluid).hasFlask())
                        && (event.getItemStack().getItem() == Items.BUCKET))
                {
                    event.setCanceled(true);
                }
            }
        }
    }

    //


    /**
     * Got the general idea from Sophisticated Backpacks.
     * Hope it works
     * UnSchtlch
     * @param event
     */
    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemEntity itemEntity = event.getItem();

        if (itemEntity.getItem().getItem() instanceof IGGenericItem) {
            if (((IGGenericItem) itemEntity.getItem().getItem().getItem()).getPattern() == ItemPattern.ore_bit ||
                    ((IGGenericItem) itemEntity.getItem().getItem().getItem()).getPattern() == ItemPattern.ore_chunk) {
                ItemStack tempPouch = new ItemStack(IGRegistryProvider.IG_ITEM_REGISTRY.get(new ResourceLocation(IGLib.MODID, "geologist_bag")));
                if (player.inventory.hasItemStack(tempPouch)) {
                    int index = player.inventory.getSlotFor(tempPouch);
                    ItemStack realPouch = player.inventory.getStackInSlot(index);
                    IGItemStackHandler t = (IGItemStackHandler)realPouch.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().get();
                    AtomicReference<ItemStack> remainingStackSimulated = new AtomicReference<>(itemEntity.getItem().copy());
                    for (int i =0; i< ((GeologistPouch)realPouch.getItem()).getSlotCount(); i++){
                        t.insertItem(i, remainingStackSimulated.get() ,true);
                    }
                    if (remainingStackSimulated.get().isEmpty())
                    {
                        ItemStack remainingStack = itemEntity.getItem().copy();
                        for (int i =0; i< ((GeologistPouch)realPouch.getItem()).getSlotCount(); i++){
                            t.insertItem(i, remainingStack ,false);
                        }
                    }
                    itemEntity.setItem(ItemStack.EMPTY);
                    event.setCanceled(true);
                }
            }
        }
    }

}
