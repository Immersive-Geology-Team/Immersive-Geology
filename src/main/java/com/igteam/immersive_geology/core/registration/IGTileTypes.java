package com.igteam.immersive_geology.core.registration;

import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.common.block.tileentity.*;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.main.IGMultiblockProvider;
import javafx.scene.effect.Bloom;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class IGTileTypes {
    public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, IGLib.MODID);

    public static final RegistryObject<TileEntityType<BloomeryTileEntity>> BLOOMERY = register("bloomery", BloomeryTileEntity::new, IGMultiblockProvider.bloomery);

    public static final RegistryObject<TileEntityType<RotaryKilnTileEntity>> ROTARYKILN = register("rotarykiln", RotaryKilnTileEntity::new, IGMultiblockProvider.rotarykiln);
    public static final RegistryObject<TileEntityType<CrystallizerTileEntity>> CRYSTALLIZER = register("crystallizer", CrystallizerTileEntity::new, IGMultiblockProvider.crystallizer);
    public static final RegistryObject<TileEntityType<ChemicalVatTileEntity>> VAT = register("chemicalvat", ChemicalVatTileEntity::new, IGMultiblockProvider.chemicalvat);
    public static final RegistryObject<TileEntityType<GravitySeparatorTileEntity>> GRAVITY = register("gravityseparator", GravitySeparatorTileEntity::new, IGMultiblockProvider.gravityseparator);
    public static final RegistryObject<TileEntityType<ReverberationFurnaceTileEntity>> REV_FURNACE = register("reverberation_furnace", ReverberationFurnaceTileEntity::new, IGMultiblockProvider.reverberation_furnace);

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, Block... valid){
        return REGISTER.register(name, () -> new TileEntityType<>(factory, ImmutableSet.copyOf(valid), null));
    }
}
