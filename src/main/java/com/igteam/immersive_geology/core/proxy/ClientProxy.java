package com.igteam.immersive_geology.core.proxy;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.IEDefaultColourHandlers;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree;
import com.igteam.immersive_geology.client.menu.helper.CreativeMenuHandler;
import com.igteam.immersive_geology.client.render.MultiblockChemicalVatRenderer;
import com.igteam.immersive_geology.client.render.RenderLayerHandler;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import com.igteam.immersive_geology.common.multiblocks.ChemicalVatMultiblock;
import com.igteam.immersive_geology.common.world.IGWorldGeneration;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ClientProxy extends ServerProxy {

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new CreativeMenuHandler());
        RenderLayerHandler.init(event);
        registerItemColors();
        registerBlockColors();
        ClientRegistry.bindTileEntityRenderer(IGTileTypes.VAT.get(), MultiblockChemicalVatRenderer::new);
        setupManualPages();
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

        if(te instanceof ChemicalVatTileEntity){
            transform.push();
            transform.rotate(new Quaternion(0, -90, 0, true));
            transform.translate(1, 1, -2);

            float pt = 0;
            if(Minecraft.getInstance().player != null){
                ((ChemicalVatTileEntity) te).activeTicks = Minecraft.getInstance().player.ticksExisted;
                pt = Minecraft.getInstance().getRenderPartialTicks();
            }

            tesr.render(te, pt, transform, buffer, 0xF000F0, 0);
            transform.pop();
        }else{
            transform.push();
            transform.rotate(new Quaternion(0, -90, 0, true));
            transform.translate(0, 1, -4);

            tesr.render(te, 0, transform, buffer, 0xF000F0, 0);
            transform.pop();
        }
    }

    private static Tree.InnerNode<ResourceLocation, ManualEntry> IG_CATEGORY;
    public void setupManualPages(){
        ManualInstance man = ManualHelper.getManual();

        IG_CATEGORY = man.getRoot().getOrCreateSubnode(modLoc("main"), 100);
        chemicalvat(modLoc("chemicalvat"), 0);
    }

    private static void chemicalvat(ResourceLocation location, int priority){
        ManualInstance man = ManualHelper.getManual();

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
        builder.addSpecialElement("chemicalvat0", 0, () -> new ManualElementMultiblock(man, ChemicalVatMultiblock.INSTANCE));
        builder.readFromFile(location);
        man.addEntry(IG_CATEGORY, builder.create(), priority);
    }
}
