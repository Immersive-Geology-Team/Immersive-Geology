package com.igteam.immersive_geology.common.multiblocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import com.igteam.immersive_geology.common.block.tileentity.GravitySeperatorTileEntity;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;

public class GravitySeperatorMultiblock extends IETemplateMultiblock {
    private static final Random RAND = new Random();
    public static final GravitySeperatorMultiblock INSTANCE = new GravitySeperatorMultiblock();

    private GravitySeperatorMultiblock(){
        super(new ResourceLocation(IGLib.MODID, "multiblocks/gravityseperator"),
                new BlockPos(0,0,0),
                new BlockPos(0,0,3),
                new BlockPos(4,6,4),
                () -> IGMultiblockRegistrationHolder.Multiblock.gravityseperator.getDefaultState());
    }

    @Override
    public float getManualScale(){
        return 12;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canRenderFormedStructure(){
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private GravitySeperatorTileEntity te;
    @OnlyIn(Dist.CLIENT)
    List<BakedQuad> list;
    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderFormedStructure(MatrixStack transform, IRenderTypeBuffer buffer){
        if(this.te == null){
            this.te = new GravitySeperatorTileEntity();
            this.te.setOverrideState(IGMultiblockRegistrationHolder.Multiblock.gravityseperator.getDefaultState().with(IEProperties.FACING_HORIZONTAL, Direction.NORTH));
        }

        if(this.list == null){
            BlockState state = IGMultiblockRegistrationHolder.Multiblock.gravityseperator.getDefaultState().with(IEProperties.FACING_HORIZONTAL, Direction.NORTH);
            IBakedModel model = ClientUtils.mc().getBlockRendererDispatcher().getModelForState(state);
            this.list = model.getQuads(state, null, RAND, EmptyModelData.INSTANCE);
        }

        if(this.list != null && this.list.size() > 0){
            World world = ClientUtils.mc().world;
            if(world != null){
                transform.push();
                transform.translate(1, 0, 0);
                RenderUtils.renderModelTESRFast(this.list, buffer.getBuffer(RenderType.getSolid()), transform, 0xF000F0, OverlayTexture.NO_OVERLAY);

                transform.push();
                transform.rotate(rot);
                transform.translate(-2, -1, -1);
                ImmersiveGeology.proxy.renderTile(this.te, buffer.getBuffer(RenderType.getSolid()), transform, buffer);
                transform.pop();

                transform.pop();
            }
        }
    }

    final Quaternion rot = new Quaternion(new Vector3f(0F, 1F, 0F), 90, true);
}
