package com.igteam.immersive_geology.common.multiblocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.client.model.IGModel;
import com.igteam.immersive_geology.client.model.IGModels;
import com.igteam.immersive_geology.client.model.ModelChemicalVat;
import com.igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
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
import java.util.function.Supplier;

public class ChemicalVatMultiblock extends IETemplateMultiblock {
    public static final ChemicalVatMultiblock INSTANCE = new ChemicalVatMultiblock();

    private ChemicalVatMultiblock(){
        super(new ResourceLocation(IGLib.MODID, "multiblocks/chemicalvat"),
                new BlockPos(3,0,0),
                new BlockPos(3,0,2),
                new BlockPos(4,4,3),
                () -> IGMultiblockRegistrationHolder.Multiblock.chemicalvat.getDefaultState());
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
            BlockState state = IGMultiblockRegistrationHolder.Multiblock.chemicalvat.getDefaultState().with(IEProperties.FACING_HORIZONTAL, Direction.NORTH);
            IBakedModel model = ClientUtils.mc().getBlockRendererDispatcher().getModelForState(state);
            this.list = model.getQuads(state, null, RAND, EmptyModelData.INSTANCE);
        }

        //Fix issue of manual render being off center.
        transform.translate(3,0,0);

        //Need to use this method otherwise it doesn't show up in the manual...
        RenderUtils.renderModelTESRFast(this.list, buffer.getBuffer(RenderType.getCutout()), transform, 0xF000F0, OverlayTexture.NO_OVERLAY);
    }
}
