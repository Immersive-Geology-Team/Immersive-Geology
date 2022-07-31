package igteam.immersive_geology.core.proxy;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.energy.ThermoelectricHandler;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.common.gui.GuiHandler;
import blusunrize.immersiveengineering.common.items.IEItems;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree;
import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.client.gui.BloomeryScreen;
import igteam.immersive_geology.client.gui.ReverberationScreen;
import igteam.immersive_geology.client.menu.helper.CreativeMenuHandler;
import igteam.immersive_geology.client.render.IGColorHandler;
import igteam.immersive_geology.client.render.RenderLayerHandler;
import igteam.immersive_geology.client.render.multiblock.*;
import igteam.immersive_geology.client.render.tileentity.BloomeryRenderer;
import igteam.immersive_geology.common.block.tileentity.BloomeryTileEntity;
import igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import igteam.immersive_geology.common.gui.BloomeryContainer;
import igteam.immersive_geology.common.gui.ReverberationContainer;
import igteam.immersive_geology.common.multiblocks.*;
import igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.core.registration.IGTileTypes;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import igteam.api.IGApi;
import igteam.api.block.IGBlockType;
import igteam.api.item.IGItemType;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.MiscEnum;
import igteam.api.main.IGRegistryProvider;
import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.helper.APIMaterials;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
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
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.HashMap;

public class ClientProxy extends ServerProxy {

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new CreativeMenuHandler());
        RenderLayerHandler.init(event);
        registerItemColors();
        registerBlockColors();
        registerSpecialRenderers();
        supplyMaterialTint(event);
        setupManualPages();
    }

    private void supplyMaterialTint(FMLClientSetupEvent event){
        Minecraft minecraft = event.getMinecraftSupplier().get();
        for(MaterialInterface<?> i : APIMaterials.all()) {
            MaterialBase base = i.get();
            HashMap<MaterialPattern, Boolean> colorCheckMap = new HashMap<>();
            for (MaterialPattern pattern : MaterialPattern.values()) {
                colorCheckMap.put(pattern, true);
                if (base.hasPattern(pattern)) {
                    ResourceLocation test = new ResourceLocation(IGApi.MODID, "textures/" + (pattern instanceof ItemPattern ? "item" : "block") + "/colored/" + base.getName() + "/" + pattern.getName() + ".png");
                    if (pattern.equals(BlockPattern.slab)) //crutch for sheetmetal slabs
                    {
                        test =  new ResourceLocation(IGApi.MODID, "textures/" + (pattern instanceof ItemPattern ? "item" : "block") + "/colored/" + base.getName() + "/" + BlockPattern.sheetmetal.getName() + ".png");
                    }
                    try {
                        boolean check = minecraft.getResourceManager().hasResource(test);
                        colorCheckMap.put(pattern, !check);
                    } catch (Exception ignored) {}
                }
            }

            base.initializeColorTint(colorCheckMap::get);
        }
    }

    private void registerSpecialRenderers(){
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.VAT.get(), MultiblockChemicalVatRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.GRAVITY.get(), MultiblockGravitySeparatorRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.REV_FURNACE.get(), MultiblockReverberationFurnaceRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.CRYSTALLIZER.get(), MultiblockCrystallizerRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.ROTARYKILN.get(), MultiblockRotaryKilnRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.HYDROJET.get(), MultiblockHydroJetRenderer::new);

        ClientRegistry.bindTileEntityRenderer(IGTileTypes.BLOOMERY.get(), BloomeryRenderer::new);
    }

    @Override
    public void onFinishSetup(FMLLoadCompleteEvent event) { //Common Finish Setup!
        super.onFinishSetup(event);
        setupBloomeryFuels();
        setupReverberationFuels();

        //TODO wrap it to check if thorium is enabled
        ThermoelectricHandler.ThermoelectricSource thorium = new ThermoelectricHandler.
                ThermoelectricSource(MetalEnum.Thorium.getBlockTag(BlockPattern.storage),
                1800, ThermoelectricHandler.TemperatureScale.KELVIN);
        ThermoelectricHandler.registerSourceInKelvin(thorium);
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

    private void registerItemColors(){
        for(Item item : IGRegistryProvider.IG_ITEM_REGISTRY.values()){
            if(item instanceof IGItemType && ((IGItemType) item).hasCustomItemColours()){
                Minecraft.getInstance().getItemColors().register(IGColorHandler.INSTANCE, item);
            }
        }
    }

    private void registerBlockColors(){
        for(Block block : IGRegistryProvider.IG_BLOCK_REGISTRY.values()){
            if(block instanceof IGBlockType && ((IGBlockType) block).hasCustomBlockColours()) {
                Minecraft.getInstance().getBlockColors().register(IGColorHandler.INSTANCE, block);
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
    private static Tree.InnerNode<ResourceLocation, ManualEntry> IG_CATEGORY_MACHINES, IG_CATEGORY_MINERALS, IG_CATEGORY_METALS;
    public void setupManualPages() {
        ManualInstance man = ManualHelper.getManual();

        IG_CATEGORY = man.getRoot().getOrCreateSubnode(modLoc("main"), 101);

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);

        builder.readFromFile(modLoc("ig_note"));
        man.addEntry(IG_CATEGORY, builder.create(), 0);

        IG_CATEGORY_MINERALS = IG_CATEGORY.getOrCreateSubnode(modLoc("minerals"), 1);
        mineral_info(0);

        IG_CATEGORY_METALS = IG_CATEGORY.getOrCreateSubnode(modLoc("metals"), 2);
        metal_info(0);

        IG_CATEGORY_MACHINES = IG_CATEGORY.getOrCreateSubnode(modLoc("machines"), 3);
        gravityseparator(modLoc("gravityseparator"), 0);
        chemicalvat(modLoc("chemicalvat"), 1);
        reverberation_furnace(modLoc("reverberation_furnace"), 2);
        crystallizer(modLoc("crystallizer"), 3);
        rotarykiln(modLoc("rotarykiln"),4);
        hydrojet(modLoc("hydrojet"),5);

    }

    private static void metal_info(int priority){
        ManualInstance man = ManualHelper.getManual();

        for (MaterialInterface<?> wrapper : MetalEnum.values()){
            Tree.InnerNode<ResourceLocation, ManualEntry> IG_CATEGORY_RARITY = IG_CATEGORY_METALS.getOrCreateSubnode(modLoc(wrapper.get().getRarity().name().toLowerCase()),wrapper.get().getRarity().ordinal());
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);

            builder.readFromFile(modLoc(wrapper.getName()));
            man.addEntry(IG_CATEGORY_RARITY, builder.create(), priority);
        }
    }
    private static void mineral_info(int priority){
        ManualInstance man = ManualHelper.getManual();

        for (MaterialInterface<?> wrapper : MineralEnum.values()){
            Tree.InnerNode<ResourceLocation, ManualEntry> IG_CATEGORY_RARITY = IG_CATEGORY_MINERALS.getOrCreateSubnode(modLoc(wrapper.get().getRarity().name().toLowerCase()),wrapper.get().getRarity().ordinal());
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);

            builder.readFromFile(modLoc(wrapper.getName()));
            man.addEntry(IG_CATEGORY_RARITY, builder.create(), priority);
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
    private static void hydrojet(ResourceLocation location, int priority){
        ManualInstance man = ManualHelper.getManual();

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
        builder.addSpecialElement("hydrojet0", 0, () -> new ManualElementMultiblock(man, RotaryKilnMultiblock.INSTANCE));
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

        GuiHandler.register(BloomeryTileEntity.class,
                new ResourceLocation(IGLib.MODID, "bloomery"), BloomeryContainer::new);
        registerScreen(new ResourceLocation(IGLib.MODID, "bloomery"), BloomeryScreen::new);
    }

    public static void requestModelsAndTextures() {
        MultiblockHydroJetRenderer.ARM = DynamicModel.createSided(
                new ResourceLocation(IGApi.MODID, "multiblock/obj/hydrojet/hydrojet_arm.obj"),
                "hydrojet_arm", DynamicModel.ModelType.OBJ
        );

        MultiblockHydroJetRenderer.HEAD = DynamicModel.createSided(
                new ResourceLocation(IGApi.MODID, "multiblock/obj/hydrojet/hydrojet_head.obj"),
                "hydrojet_head", DynamicModel.ModelType.OBJ
        );
    }
}
