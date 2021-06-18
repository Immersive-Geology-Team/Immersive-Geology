package com.igteam.immersive_geology.core.registration;
/*
import blusunrize.immersiveengineering.common.blocks.metal.EnergyConnectorTileEntity;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.common.block.tileentity.VatTileEntity;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.function.Supplier;

public class IGTileTypes
{
    public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(
            ForgeRegistries.TILE_ENTITIES, IGLib.MODID);

    public static final RegistryObject<TileEntityType<VatTileEntity>> CRAFTING_TABLE = REGISTER.register(
            "craftingtable", makeType(VatTileEntity::new, () -> IGRegistration.Multiblocks.vat)
    );

    static
    {
        EnergyConnectorTileEntity.registerConnectorTEs(REGISTER);
    }

    private static <T extends TileEntity> Supplier<TileEntityType<T>> makeType(Supplier<T> create, Supplier<Block> valid)
    {
        return makeTypeMultipleBlocks(create, () -> ImmutableSet.of(valid.get()));
    }

    private static <T extends TileEntity> Supplier<TileEntityType<T>> makeTypeMultipleBlocks(
            Supplier<T> create,
            Supplier<Collection<Block>> valid)
    {
        return () -> new TileEntityType<>(create, ImmutableSet.copyOf(valid.get()), null);
    }
}

 */
