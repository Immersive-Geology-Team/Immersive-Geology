package com.igteam.immersive_geology.core.registration;

import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.common.block.tileentity.*;
import com.igteam.immersive_geology.core.lib.IGLib;
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

    public static final RegistryObject<TileEntityType<BloomeryTileEntity>> BLOOMERY = register("bloomery", BloomeryTileEntity::new, IGMultiblockRegistrationHolder.Multiblock.bloomery);

    public static final RegistryObject<TileEntityType<RotaryKilnTileEntity>> ROTARYKILN = register("rotarykiln", RotaryKilnTileEntity::new, IGMultiblockRegistrationHolder.Multiblock.rotarykiln);
    public static final RegistryObject<TileEntityType<CrystallizerTileEntity>> CRYSTALLIZER = register("crystallizer", CrystallizerTileEntity::new, IGMultiblockRegistrationHolder.Multiblock.crystallizer);
    public static final RegistryObject<TileEntityType<ChemicalVatTileEntity>> VAT = register("chemicalvat", ChemicalVatTileEntity::new, IGMultiblockRegistrationHolder.Multiblock.chemicalvat);
    public static final RegistryObject<TileEntityType<GravitySeparatorTileEntity>> GRAVITY = register("gravityseparator", GravitySeparatorTileEntity::new, IGMultiblockRegistrationHolder.Multiblock.gravityseparator);
    public static final RegistryObject<TileEntityType<ReverberationFurnaceTileEntity>> REV_FURNACE = register("reverberation_furnace", ReverberationFurnaceTileEntity::new, IGMultiblockRegistrationHolder.Multiblock.reverberation_furnace);

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, Block... valid){
        return REGISTER.register(name, () -> new TileEntityType<>(factory, ImmutableSet.copyOf(valid), null));
    }
}
