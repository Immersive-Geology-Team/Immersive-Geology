package com.igteam.immersivegeology.common.tileentity;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.tileentity.entities.IGToolForge;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

public class IGRegisterTileEntityTypes {

    public static void parseRegistry(RegistryEvent.Register<TileEntityType<?>> e){
//
//        e.getRegistry().registerAll(
//                TileEntityType.Builder.create((Supplier<TileEntity>) IGToolForge::new, IGContent.toolForge).build(null).setRegistryName(tool_forge_name));

    }
}
