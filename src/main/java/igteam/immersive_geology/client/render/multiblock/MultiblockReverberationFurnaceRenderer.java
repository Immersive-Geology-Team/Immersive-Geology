package igteam.immersive_geology.client.render.multiblock;

import igteam.immersive_geology.common.block.tileentity.GravitySeparatorTileEntity;
import igteam.immersive_geology.common.block.tileentity.ReverberationFurnaceTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiblockReverberationFurnaceRenderer extends TileEntityRenderer<ReverberationFurnaceTileEntity> {
    public MultiblockReverberationFurnaceRenderer(TileEntityRendererDispatcher dispatcher){
        super(dispatcher);
    }

    @Override
    public boolean isGlobalRenderer(ReverberationFurnaceTileEntity p_188185_1_) {
        return false;
    }

    @Override
    public void render(ReverberationFurnaceTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if(te != null && !te.isDummy()) {
            transform.push();
            Direction rotation = te.getFacing();
            //TODO Update translation settings to reflect this tile entity
            switch (rotation) {
                case NORTH:
                    transform.rotate(new Quaternion(0, 90F, 0, true));
                    transform.translate(-6, 0, -1);
                    break;
                case EAST:
                    transform.translate(-5, 0, -1);
                    break;
                case SOUTH:
                    transform.rotate(new Quaternion(0, 270F, 0, true));
                    transform.translate(-5, 0, -2);
                    break;
                case WEST:
                    transform.rotate(new Quaternion(0, 180F, 0, true));
                    transform.translate(-6, 0, -2);
                    break;
                default:
                    break;
            }

            ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) te.master();
            transform.pop();
        }

    }
}
