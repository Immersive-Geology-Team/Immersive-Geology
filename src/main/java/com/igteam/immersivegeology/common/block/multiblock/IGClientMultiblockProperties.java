package com.igteam.immersivegeology.common.block.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class IGClientMultiblockProperties implements ClientMultiblocks.MultiblockManualData{
    private final IGTemplateMultiblock multiblock;
    @Nullable
    private NonNullList<ItemStack> materials;
    private final ItemStack renderStack;
    @Nullable
    private final Vec3 renderOffset;

    private IGClientMultiblockProperties(IGTemplateMultiblock multiblock, @Nullable Vec3 renderOffset){
        this.multiblock = multiblock;
        this.renderStack = new ItemStack(multiblock.getBaseBlock());
        this.renderOffset = renderOffset;
    }

    public IGClientMultiblockProperties(IGTemplateMultiblock multiblock, double offX, double offY, double offZ){
        this(multiblock, new Vec3(offX, offY, offZ));
    }

    public IGClientMultiblockProperties(IGTemplateMultiblock multiblock){
        this(multiblock, null);
    }

    @Override
    public NonNullList<ItemStack> getTotalMaterials(){
        // TODO (malte): Add helper for this to IE API
        if(materials == null){
            List<StructureTemplate.StructureBlockInfo> structure = multiblock.getStructure(null);
            materials = NonNullList.create();
            for(StructureTemplate.StructureBlockInfo info:structure){
                ItemStack picked = Utils.getPickBlock(info.state);
                boolean added = false;
                for(ItemStack existing:materials)
                    if(ItemStack.isSame(existing, picked)){
                        existing.grow(1);
                        added = true;
                        break;
                    }
                if(!added)
                    materials.add(picked.copy());
            }
        }
        return materials;
    }

    @Override
    public boolean canRenderFormedStructure(){
        return renderOffset != null;
    }

    @Override
    public void renderFormedStructure(PoseStack transform, MultiBufferSource buffer){
        Objects.requireNonNull(renderOffset);
        transform.translate(renderOffset.x, renderOffset.y, renderOffset.z);
        ClientUtils.mc().getItemRenderer().renderStatic(
                renderStack, ItemTransforms.TransformType.NONE, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, transform, buffer, 0
        );
    }
}
