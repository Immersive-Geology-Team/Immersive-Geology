package com.igteam.immersive_geology.core.proxy;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.IEDefaultColourHandlers;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.gui.GuiHandler;
import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import blusunrize.immersiveengineering.common.items.IEItems;
import blusunrize.lib.manual.ManualElementItem;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.legacy_api.materials.Material;
import com.igteam.immersive_geology.legacy_api.materials.MaterialEnum;
import com.igteam.immersive_geology.legacy_api.materials.MaterialUseType;
import com.igteam.immersive_geology.client.gui.ReverberationScreen;
import com.igteam.immersive_geology.client.menu.helper.CreativeMenuHandler;
import com.igteam.immersive_geology.client.render.*;
import com.igteam.immersive_geology.common.block.tileentity.BloomeryTileEntity;
import com.igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import com.igteam.immersive_geology.common.gui.ReverberationContainer;
import com.igteam.immersive_geology.common.multiblocks.*;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class ClientProxy extends ServerProxy {

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new CreativeMenuHandler());
        RenderLayerHandler.init(event);
        registerItemColors();
        registerBlockColors();
        registerSpecialRenderers();
    }

    private void registerSpecialRenderers(){
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.VAT.get(), MultiblockChemicalVatRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.GRAVITY.get(), MultiblockGravitySeparatorRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.REV_FURNACE.get(), MultiblockReverberationFurnaceRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.CRYSTALLIZER.get(), MultiblockCrystallizerRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.ROTARYKILN.get(), MultiblockRotaryKilnRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.BLOOMERY.get(), BloomeryRenderer::new);
    }

    @Override
    public void onFinishSetup(FMLLoadCompleteEvent event) {
        setupManualPages();
        setupBloomeryFuels();
        setupReverberationFuels();
    }

    private void setupReverberationFuels(){
        ImmersiveGeology.getNewLogger().info("Setting up Fuels for Reverberation Furnace");
        ReverberationFurnaceTileEntity.fuelMap.put(IEItems.Ingredients.coalCoke, 200);
        ReverberationFurnaceTileEntity.fuelMap.put(IEItems.Ingredients.dustCoke, 250);
        ReverberationFurnaceTileEntity.fuelMap.put(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Coal.getMaterial(), MaterialUseType.DUST), 125);
        BloomeryTileEntity.fuelMap.put(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Coal.getMaterial(), MaterialUseType.TINY_DUST), 10);
        ReverberationFurnaceTileEntity.fuelMap.put(Items.COAL, 100);
    }

    private void setupBloomeryFuels(){
        ImmersiveGeology.getNewLogger().info("Setting up Fuels for Bloomery");
        BloomeryTileEntity.fuelMap.put(IEItems.Ingredients.coalCoke, 200);
        BloomeryTileEntity.fuelMap.put(IEItems.Ingredients.dustCoke, 250);
        BloomeryTileEntity.fuelMap.put(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Coal.getMaterial(), MaterialUseType.DUST), 125);
        BloomeryTileEntity.fuelMap.put(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Coal.getMaterial(), MaterialUseType.TINY_DUST), 10);
        BloomeryTileEntity.fuelMap.put(Items.COAL, 100);
        BloomeryTileEntity.fuelMap.put(Items.CHARCOAL, 100);
    }

    private void registerItemColors(){
        for(Item item : IGRegistrationHolder.registeredIGItems.values()){
            if(item instanceof IEItemInterfaces.IColouredItem && ((IEItemInterfaces.IColouredItem) item).hasCustomItemColours()){
                Minecraft.getInstance().getItemColors().register(IEDefaultColourHandlers.INSTANCE, item);
            }
        }
    }

    private void registerBlockColors(){
        for(Block block : IGRegistrationHolder.registeredIGBlocks.values()){
            if(block instanceof IEBlockInterfaces.IColouredBlock && ((IEBlockInterfaces.IColouredBlock) block).hasCustomBlockColours()) {
                Minecraft.getInstance().getBlockColors().register(IEDefaultColourHandlers.INSTANCE, block);
            }
        }
    }

    @Override
    public void renderTile(TileEntity te, IVertexBuilder iVertexBuilder, MatrixStack transform, IRenderTypeBuffer buffer){
        TileEntityRenderer<TileEntity> tesr = TileEntityRendererDispatcher.instance.getRenderer((TileEntity) te);

        transform.push();
        transform.rotate(new Quaternion(0, -90, 0, true));
        transform.translate(0, 1, -4);

        tesr.render(te, 0, transform, buffer, 0xF000F0, 0);
        transform.pop();
    }

    private static Tree.InnerNode<ResourceLocation, ManualEntry> IG_CATEGORY;
    private static Tree.InnerNode<ResourceLocation, ManualEntry> IG_CATEGORY_MACHINES;
    private static Tree.InnerNode<ResourceLocation, ManualEntry> IG_CATEGORY_MINERALS;
    public void setupManualPages() {
        ManualInstance man = ManualHelper.getManual();

        IG_CATEGORY = man.getRoot().getOrCreateSubnode(modLoc("main"), 101);

        IG_CATEGORY_MINERALS = IG_CATEGORY.getOrCreateSubnode(modLoc("minerals"), 0);
        mineral_info(0);

        IG_CATEGORY_MACHINES = IG_CATEGORY.getOrCreateSubnode(modLoc("machines"), 1);
        gravityseparator(modLoc("gravityseparator"), 0);
        chemicalvat(modLoc("chemicalvat"), 1);
        reverberation_furnace(modLoc("reverberation_furnace"), 2);
        crystallizer(modLoc("crystallizer"), 3);
        rotarykiln(modLoc("rotarykiln"),4);

    }

    private static void mineral_info(int priority){
        ManualInstance man = ManualHelper.getManual();

        for(MaterialEnum material : MaterialEnum.minerals()) {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
            Material mineral = material.getMaterial();
            ItemStack item = new ItemStack(IGRegistrationHolder.getBlockByMaterial(MaterialEnum.Vanilla.getMaterial(), mineral, MaterialUseType.ORE_STONE));
            builder.addSpecialElement(mineral.getName(), 0, () -> new ManualElementItem(man, item));

            ItemStack dirty_crushed_ore = new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanilla.getMaterial(), mineral, MaterialUseType.DIRTY_CRUSHED_ORE));
            builder.addSpecialElement("dirty_crushed_" + mineral.getName(), 0, () -> new ManualElementItem(man, dirty_crushed_ore));

            ItemStack crushed_ore = new ItemStack(IGRegistrationHolder.getItemByMaterial(mineral, MaterialUseType.CRUSHED_ORE));
            builder.addSpecialElement("crushed_" + mineral.getName(), 0, () -> new ManualElementItem(man, crushed_ore));

            Material material_ingot = mineral.getProcessedType().getMaterial();

            ItemStack grit = new ItemStack(IGRegistrationHolder.getItemByMaterial(material_ingot, MaterialUseType.DUST));
            builder.addSpecialElement("grit_" + material_ingot.getName(), 0, () -> new ManualElementItem(man, grit));

            ItemStack output = new ItemStack(IGRegistrationHolder.getItemByMaterial(material_ingot, MaterialUseType.INGOT));
            builder.addSpecialElement("ingot_" + material_ingot.getName(), 0, () -> new ManualElementItem(man, output));

            builder.readFromFile(modLoc("mineral_" + mineral.getName()));
            man.addEntry(IG_CATEGORY_MINERALS, builder.create(), priority);
        }
    }

    private static void chemicalvat(ResourceLocation location, int priority){
        ManualInstance man = ManualHelper.getManual();

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
        builder.addSpecialElement("chemicalvat0", 0, () -> new ManualElementMultiblock(man, ChemicalVatMultiblock.INSTANCE));
        builder.readFromFile(location);
        man.addEntry(IG_CATEGORY_MACHINES, builder.create(), priority);
    }

    private static void rotarykiln(ResourceLocation location, int priority){
        ManualInstance man = ManualHelper.getManual();

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
        builder.addSpecialElement("rotarykiln0", 0, () -> new ManualElementMultiblock(man, RotaryKilnMultiblock.INSTANCE));
        builder.readFromFile(location);
        man.addEntry(IG_CATEGORY_MACHINES, builder.create(), priority);
    }

    private static void crystallizer(ResourceLocation location, int priority){
        ManualInstance man = ManualHelper.getManual();

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
        builder.addSpecialElement("crystallizer0", 0, () -> new ManualElementMultiblock(man, CrystallizerMultiblock.INSTANCE));
        builder.readFromFile(location);
        man.addEntry(IG_CATEGORY_MACHINES, builder.create(), priority);
    }

    private static void gravityseparator(ResourceLocation location, int priority){
        ManualInstance man = ManualHelper.getManual();

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
        builder.addSpecialElement("gravityseparator0", 0, () -> new ManualElementMultiblock(man, GravitySeparatorMultiblock.INSTANCE));
        builder.readFromFile(location);
        man.addEntry(IG_CATEGORY_MACHINES, builder.create(), priority);
    }

    private static void reverberation_furnace(ResourceLocation location, int priority){
        ManualInstance man = ManualHelper.getManual();

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
        builder.addSpecialElement("reverberation_furnace0", 0, () -> new ManualElementMultiblock(man, ReverberationFurnaceMultiblock.INSTANCE));
        builder.readFromFile(location);
        man.addEntry(IG_CATEGORY_MACHINES, builder.create(), priority);
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
        registerScreen(new ResourceLocation(IGLib.MODID, "reverberation_furnace"), ReverberationScreen::new);
    }
}
