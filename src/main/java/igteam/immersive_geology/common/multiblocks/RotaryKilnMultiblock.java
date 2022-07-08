package igteam.immersive_geology.common.multiblocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;

public class RotaryKilnMultiblock extends IETemplateMultiblock {
    public static final RotaryKilnMultiblock INSTANCE = new RotaryKilnMultiblock();

    private RotaryKilnMultiblock(){
        super(new ResourceLocation(IGLib.MODID, "multiblocks/rotarykiln"),
                new BlockPos(0,0,0),
                new BlockPos(1,2,7),
                new BlockPos(3,3,8), // as we hit the north side of the block at the end to form the multiblock structure, the Z size is the dimension where the model should be largest, as we hit the block face in the z dimension toward the model.
                () -> IGMultiblockProvider.rotarykiln.getDefaultState());
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
        if(this.list == null){
            BlockState state = IGMultiblockProvider.rotarykiln.getDefaultState().with(IEProperties.FACING_HORIZONTAL, Direction.NORTH);
            IBakedModel model = ClientUtils.mc().getBlockRendererDispatcher().getModelForState(state);
            this.list = model.getQuads(state, null, RAND, EmptyModelData.INSTANCE);
        }

        //Need to use this method otherwise it doesn't show up in the manual...
        RenderUtils.renderModelTESRFast(this.list, buffer.getBuffer(RenderType.getCutout()), transform, 0xF000F0, OverlayTexture.NO_OVERLAY);
    }
}
