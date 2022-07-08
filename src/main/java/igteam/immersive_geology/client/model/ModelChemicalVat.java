package igteam.immersive_geology.client.model;

import igteam.immersive_geology.client.render.IGRenderTypes;
import igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class ModelChemicalVat extends IGModel{
    public static final String ID = "chemical_stirbar";
    public static final ResourceLocation TEXTURE = new ResourceLocation(IGLib.MODID, "textures/models/chemical_stirbar.png");

    public ModelRenderer origin;
    public ModelRenderer connector;

    public float ticks = 0;

    public ModelChemicalVat(){
        super(IGRenderTypes::getEntitySolid);

        this.textureWidth = 24;
        this.textureHeight = 14;
    }

    @Override
    public String toString() {
        return ID;
    }

    @Override
    public void init(){
        this.origin = new ModelRenderer(this, 0, 0);

        this.connector = new ModelRenderer(this, 0, 0);
        this.connector.addBox( -2F, 0, -5F, 4, 4, 10);
        this.connector.setRotationPoint(0f, 0f, 0f);

        this.origin.addChild(this.connector);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){
        this.connector.rotateAngleY = ticks;
        this.origin.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
