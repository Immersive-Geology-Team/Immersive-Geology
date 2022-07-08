package igteam.immersive_geology.common.multiblocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.common.block.tileentity.GravitySeparatorTileEntity;
import igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import igteam.api.main.IGMultiblockProvider;
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

public class GravitySeparatorMultiblock extends IETemplateMultiblock {
    private static final Random RAND = new Random();
    public static final GravitySeparatorMultiblock INSTANCE = new GravitySeparatorMultiblock();

    private GravitySeparatorMultiblock(){
        super(new ResourceLocation(IGLib.MODID, "multiblocks/gravityseparator"),
                new BlockPos(1,0,1),
                new BlockPos(1,6,2),
                new BlockPos(3,7,3),
                () -> IGMultiblockProvider.gravityseparator.getDefaultState());
    }

    @Override
    public float getManualScale(){
        return 9;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canRenderFormedStructure(){
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private GravitySeparatorTileEntity te;

    @OnlyIn(Dist.CLIENT)
    List<BakedQuad> list;
    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderFormedStructure(MatrixStack transform, IRenderTypeBuffer buffer){
        if(this.te == null){
            this.te = new GravitySeparatorTileEntity();
            this.te.setOverrideState(IGMultiblockProvider.gravityseparator.getDefaultState().with(IEProperties.FACING_HORIZONTAL, Direction.NORTH));
        }

        if(this.list == null){
            BlockState state = IGMultiblockProvider.gravityseparator.getDefaultState().with(IEProperties.FACING_HORIZONTAL, Direction.NORTH);
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
