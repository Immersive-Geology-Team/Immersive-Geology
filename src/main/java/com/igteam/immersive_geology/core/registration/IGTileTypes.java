package com.igteam.immersive_geology.core.registration;

import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.common.block.tileentity.ChemicalVatTileEntity;
import com.igteam.immersive_geology.common.block.tileentity.GravitySeperatorTileEntity;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class IGTileTypes {
    public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, IGLib.MODID);

    public static final RegistryObject<TileEntityType<ChemicalVatTileEntity>> VAT = register("chemicalvat", ChemicalVatTileEntity::new, IGMultiblockRegistrationHolder.Multiblock.chemicalvat);
    public static final RegistryObject<TileEntityType<GravitySeperatorTileEntity>> GRAVITY = register("gravityseperator", GravitySeperatorTileEntity::new, IGMultiblockRegistrationHolder.Multiblock.gravityseperator);

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, Block... valid){
        return REGISTER.register(name, () -> new TileEntityType<>(factory, ImmutableSet.copyOf(valid), null));
    }
}
