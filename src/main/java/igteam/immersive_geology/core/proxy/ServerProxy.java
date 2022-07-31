package igteam.immersive_geology.core.proxy;

import blusunrize.immersiveengineering.api.energy.ThermoelectricHandler;
import blusunrize.immersiveengineering.common.gui.GuiHandler;
import blusunrize.immersiveengineering.common.items.IEItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.MiscEnum;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.common.block.tileentity.BloomeryTileEntity;
import igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import igteam.immersive_geology.common.fluid.IGFluid;
import igteam.immersive_geology.common.gui.ReverberationContainer;
import igteam.immersive_geology.common.world.IGWorldGeneration;
import igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

public class ServerProxy implements Proxy {

    @Override
    public void onSetup(FMLCommonSetupEvent event) {
        IGWorldGeneration.initialize();
        MinecraftForge.EVENT_BUS.register(new IGWorldGeneration());
        DataSerializers.registerSerializer(IGFluid.OPTIONAL_FLUID_STACK);
    }

    @Override
    public void onFinishSetup(FMLLoadCompleteEvent event) {
        setupBloomeryFuels();
        setupReverberationFuels();

        //TODO wrap it to check if thorium is enabled
        ThermoelectricHandler.ThermoelectricSource thorium = new ThermoelectricHandler.
                ThermoelectricSource(MetalEnum.Thorium.getBlockTag(BlockPattern.storage),
                1800, ThermoelectricHandler.TemperatureScale.KELVIN);
        ThermoelectricHandler.registerSourceInKelvin(thorium);
   }

    @Override
    public void onServerStarting(FMLServerStartingEvent event) {

    }

    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {

    }

    @Override
    public void renderTile(TileEntity te, IVertexBuilder iVertexBuilder, MatrixStack transform, IRenderTypeBuffer buffer){
    }

    protected static ResourceLocation modLoc(String str){
        return new ResourceLocation(IGLib.MODID, str);
    }
    @SuppressWarnings("unchecked")
    public <C extends Container, S extends Screen & IHasContainer<C>> void registerScreen(ResourceLocation name, ScreenManager.IScreenFactory<C, S> factory){
        ContainerType<C> type = (ContainerType<C>) GuiHandler.getContainerType(name);
        ScreenManager.registerFactory(type, factory);
    }

    public void registerContainersAndScreens() {
        GuiHandler.register(ReverberationFurnaceTileEntity.class,
                new ResourceLocation(IGLib.MODID, "reverberation_furnace"), ReverberationContainer::new);
    }
    private void setupReverberationFuels(){
        ImmersiveGeology.getNewLogger().info("Setting up Fuels for Reverberation Furnace");
        ReverberationFurnaceTileEntity.fuelMap.put(IEItems.Ingredients.coalCoke, 2000);
        ReverberationFurnaceTileEntity.fuelMap.put(IEItems.Ingredients.dustCoke, 2500);
        ReverberationFurnaceTileEntity.fuelMap.put(MiscEnum.Coal.getItem(ItemPattern.dust), 1250);
        ReverberationFurnaceTileEntity.fuelMap.put(Items.COAL, 1000);
    }

    private void setupBloomeryFuels(){
        ImmersiveGeology.getNewLogger().info("Setting up Fuels for Bloomery");
        BloomeryTileEntity.fuelMap.put(IEItems.Ingredients.coalCoke, 2000);
        BloomeryTileEntity.fuelMap.put(IEItems.Ingredients.dustCoke, 2500);
        BloomeryTileEntity.fuelMap.put(MiscEnum.Coal.getItem(ItemPattern.dust), 1250);
        BloomeryTileEntity.fuelMap.put(Items.COAL, 1000);
        BloomeryTileEntity.fuelMap.put(Items.CHARCOAL, 1000);
    }
}
