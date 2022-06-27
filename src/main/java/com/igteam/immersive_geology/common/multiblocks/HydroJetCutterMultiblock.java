package com.igteam.immersive_geology.common.multiblocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import igteam.immersive_geology.main.IGMultiblockProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;

public class HydroJetCutterMultiblock extends IETemplateMultiblock {
    public static final HydroJetCutterMultiblock INSTANCE = new HydroJetCutterMultiblock();

    //Used in HydroJetCutterTileEntity -- Do not forget about Data Gen - this is where we apply the Model File!
    //Keep naming convention the same as In IGTileType - this is referencing the NBT Structure File, I'd recommend ensuring you have a pre-made one at your convenience
    //See IGBlockStateProvider in DataGen for how we link a multiblock it's model and texture.

    //For the fancy animation stuff we use the HydrojetRenderer - which is a assigned in ClientProxy.

    //Don't forget to register the multiblock in IGMultiblockRegistrationHolder!
    //This is where we assign it's size, master position and activation block (the one you hammer)
    private HydroJetCutterMultiblock(){
        super(new ResourceLocation(IGLib.MODID, "multiblocks/hydrojet_cutter"),
                new BlockPos(0,0,0), // master block location - generally want this in the middle.
                new BlockPos(0,1,2), // hammer block to activate
                new BlockPos(2,2,3), //total blockspace used
                () -> IGMultiblockProvider.hydrojet_cutter.getDefaultState());
    }

    @Override
    public float getManualScale(){
        return 11;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canRenderFormedStructure(){
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    List<BakedQuad> list;

    Random RAND = new Random();

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderFormedStructure(MatrixStack transform, IRenderTypeBuffer buffer){
        //This is to ensure it can load on servers, if it's null, it's a server, so we can't use it anyway.
        if(this.list == null){
            BlockState state = IGMultiblockProvider.hydrojet_cutter.getDefaultState().with(IEProperties.FACING_HORIZONTAL, Direction.NORTH);
            IBakedModel model = ClientUtils.mc().getBlockRendererDispatcher().getModelForState(state);
            this.list = model.getQuads(state, null, RAND, EmptyModelData.INSTANCE);
        }

        //Need to use this method otherwise it doesn't show up in the manual...
        RenderUtils.renderModelTESRFast(this.list, buffer.getBuffer(RenderType.getCutout()), transform, 0xF000F0, OverlayTexture.NO_OVERLAY);
    }
}
