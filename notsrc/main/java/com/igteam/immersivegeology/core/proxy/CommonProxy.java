package com.igteam.immersivegeology.core.proxy;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class CommonProxy {
    /** Fired at {@link FMLLoadCompleteEvent} */
    public void setup(){
    }

    public void preInit(){
    }

    public void preInitEnd(){
    }

    public void init(){
    }

    public void postInit(){
    }

    /** Fired at {@link FMLLoadCompleteEvent} */
    public void completed(FMLLoadCompleteEvent event){
    }

    public void renderTile(BlockEntity te, VertexConsumer iVertexBuilder, PoseStack transform, MultiBufferSource buffer){
    }

    public void handleEntitySound(SoundEvent soundEvent, Entity entity, boolean active, float volume, float pitch){
    }

    public void handleTileSound(SoundEvent soundEvent, BlockEntity te, boolean active, float volume, float pitch){
    }

    public void drawUpperHalfSlab(PoseStack transform, ItemStack stack){
    }

    public Level getClientWorld(){
        return null;
    }

    public Player getClientPlayer(){
        return null;
    }
}
