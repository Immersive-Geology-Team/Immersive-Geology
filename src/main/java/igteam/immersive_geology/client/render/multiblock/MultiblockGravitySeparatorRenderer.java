package igteam.immersive_geology.client.render.multiblock;

import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import igteam.api.IGApi;
import igteam.api.main.IGRegistryProvider;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.immersive_geology.client.menu.helper.IGItemGroup;
import igteam.immersive_geology.common.block.helpers.IProgress;
import igteam.immersive_geology.common.block.tileentity.GravitySeparatorTileEntity;
import igteam.api.processing.recipe.SeparatorRecipe;
import com.mojang.blaze3d.matrix.MatrixStack;
import igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class MultiblockGravitySeparatorRenderer extends TileEntityRenderer<GravitySeparatorTileEntity> {

    List<Vector3f> spiralPoints = new ArrayList<>();
    int numPoints = 100;
    public MultiblockGravitySeparatorRenderer(TileEntityRendererDispatcher dispatcher){
        super(dispatcher);
        float rotationOffset = (float) Math.toRadians(90);
        // Generate spiral points
        for (int i = 0; i < numPoints; i++) {
            float progress = (float) i / numPoints;
            float angle = (float) (progress * 4.33f * Math.PI + rotationOffset); // 3 full cycles
            float radius = 1.65f;
            float x = (float) (Math.cos(angle) * radius);
            float y = (float) (Math.sin(angle) * radius);
            float z = 5.5f - (9f * (progress * 0.5f)); // Control the height of the item
            spiralPoints.add(new Vector3f(8.9f + x, y - 2.33f, z));
        }
    }

    @Override
    public boolean isGlobalRenderer(GravitySeparatorTileEntity p_188185_1_) {
        return false;
    }

    @Override
    public void render(GravitySeparatorTileEntity te, float partialTicks, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if(te != te.master()) return;

        transform.push();
        Direction rotation = te.getFacing();
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

        ArrayList<Pair<ItemStack, Integer>> teInventory = te.getInternalInventory();
        for (Pair<ItemStack, Integer> pair : teInventory) {
            int itemProgress = pair.getValue();
            ItemStack stack = pair.getKey();
            SeparatorRecipe recipe = SeparatorRecipe.findRecipe(stack);
            if (recipe != null) {
                int maxTicks = recipe.getTotalProcessTime();
                ItemStack output = recipe.getRecipeOutput();
                if (pair.getRight() > (double) (maxTicks / 2)) stack = output;

                float progress = ((float) itemProgress / maxTicks);

                // Use the progress variable to interpolate between points
                int index = (int) (progress * (numPoints - 1));
                Vector3f startPoint = spiralPoints.get(index);
                Vector3f endPoint = spiralPoints.get(Math.min(numPoints - 1, index + 1));
                float lerpFactor = progress * (numPoints - 1) - index;

                // Interpolate between the start and end points using lerp
                float interpolatedX = startPoint.getX() + (endPoint.getX() - startPoint.getX()) * lerpFactor;
                float interpolatedY = startPoint.getY() + (endPoint.getY() - startPoint.getY()) * lerpFactor;
                float interpolatedZ = startPoint.getZ() + (endPoint.getZ() - startPoint.getZ()) * lerpFactor;

                ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
                transform.push();
                float scale = .625f;
                transform.rotate(new Quaternion(new Vector3f(1, 0, 0), -90, true));
                transform.scale(scale, scale, 1);
                transform.translate(0, 0, interpolatedZ);
                transform.translate(0, interpolatedY, 0);
                transform.translate(interpolatedX, 0, 0);
                itemRender.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, transform, buffer);
                transform.pop();
            }
        }

        transform.pop();

    }
}
