/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.lib;

import com.igteam.immersivegeology.core.material.data.enums.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.energy.IEnergyStorage;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.*;

public class IGLib {
    public static final String MODID = "immersivegeology";

    public static final Logger IG_LOGGER = new PrefixedLogger(LogUtils.getLogger(), "[IG] ");

    // These should probably stay the same
    public static final int SLURRY_TO_CRYSTAL_MB = 144;
    public static final int ACID_RECOVERED_FROM_SLURRY = 120;
    public static final int ACID_TO_SLURRY_AMOUNT = 250;
    public static final int SLURRY_FROM_ACID_AMOUNT = 216;

    public static  final int SULFUR_OUTGAS = 25;

    public static final float TWO_ACID_USED_MULTIPLIER = 0.5f;
    public static final float THREE_ACID_USED_MULTIPLIER = 0.5f;

    // Should we change these baselines?
    public static final int DUST_TO_SLURRY_AMOUNT = 1;
    public static final int COMPOUND_FROM_ACID_AMOUNT = 1;
    public static final int ACID_TO_COMPOUND_AMOUNT = 125;

    // Cryolite process uses these mostly.
    public static final int COMPOUND_ACID_TO_DUST_AMOUNT = 1;
    public static final int ACID_TO_DUST_AMOUNT = 125;
    public static final int DUST_FROM_COMPOUND_ACID_AMOUNT = 1;
    public static final String GUIID_Bloomery = "bloomery";
    public static final String GUIID_RevFurnace= "reverberation_furnace";
    public static final String GUIID_Crystallizer= "crystallizer";
    public static final String GUIID_ChemicalReactor= "chemical_reactor";
    public static final String GUIID_SmallChemicalReactor= "small_chemical_reactor";
    public static final String GUIID_RotaryKiln= "rotary_kiln";
    public static final String GUIID_GeothermalExchanger= "geothermal_exchanger";
    public static final String GUIID_Crate = "crate_menu";
    public static final String GUIID_MetalDetector = "metal_detector";
	public static final int PELLETIZER_DEFAULT_TIME = 600;


	public static Logger getNewLogger()
    {
        return new PrefixedLogger(LogUtils.getLogger(), "[IG] ");
    }
    public static final BlockBehaviour.Properties STONE_DECO_PROPS = BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.STONE)
            .requiresCorrectToolForDrops()
            .strength(2, 10);

    public static final BlockBehaviour.Properties CRYSTAL_DECO_PROPS = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .instrument(NoteBlockInstrument.PLING)
            .sound(SoundType.AMETHYST)
            .requiresCorrectToolForDrops()
            .strength(2, 5);

    public static final BlockBehaviour.Properties DEFAULT_METAL_PROPERTIES = BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .sound(SoundType.COPPER)
            .requiresCorrectToolForDrops()
            .strength(3, 15);

    public static final BlockBehaviour.Properties SHEETMETAL_PROPERTIES = BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .sound(SoundType.METAL)
            .strength(2, 2);

    public static final BlockBehaviour.Properties METAL_PROPERTIES_NO_OVERLAY = BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .sound(SoundType.METAL)
            .strength(3, 15)
            .requiresCorrectToolForDrops()
            .isViewBlocking((state, blockReader, pos) -> false);

    public static final BlockBehaviour.Properties METAL_PROPERTIES_NO_OCCLUSION = METAL_PROPERTIES_NO_OVERLAY.noOcclusion().forceSolidOn();

    public static LinkedList<MaterialInterface<?>> getGeologyMaterials(){
        LinkedList<MaterialInterface<?>> list = new LinkedList<>();
        list.addAll(List.of(StoneEnum.values()));
        list.addAll(List.of(MetalEnum.values()));
        list.addAll(List.of(MineralEnum.values()));
        list.addAll(List.of(MiscEnum.values()));
        list.addAll(List.of(ChemicalEnum.values()));

        return list;
    }

    public static LinkedList<MaterialInterface<?>> getGeneratedMaterials(){
        LinkedList<MaterialInterface<?>> list = new LinkedList<>();
        List<MetalEnum> metals = MetalEnum.generatedNativeMetals();
        List<MineralEnum> minerals = Arrays.stream(MineralEnum.values()).toList();
		list.addAll(metals);
		list.addAll(minerals);

        return list;
    }

    public static ResourceLocation rl(String name)
    {
        return new ResourceLocation(IGLib.MODID, name);
    }

    public static ResourceLocation makeTextureLocation(String name) {
        return rl("textures/gui/" + name + ".png");
    }

    public static int fastHash(String s) {
        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash = (hash << 5) - hash + s.charAt(i); // hash * 31 + char
        }
        return hash;
    }

    public static int pushEnergy(IEnergyStorage provider, IEnergyStorage receiver, int maxAmount) {
        int energySim = provider.extractEnergy(maxAmount, true);
        int receivedSim = receiver.receiveEnergy(energySim, true);
        int energy = provider.extractEnergy(receivedSim, false);
        receiver.receiveEnergy(energy, false);
        return energy;
    }
	public static class PrefixedLogger implements Logger {
        private final Logger delegate;
        private final String prefix;

        public PrefixedLogger(Logger delegate, String prefix) {
            this.delegate = delegate;
            this.prefix = prefix;
        }

        @Override
        public String getName() {
            return delegate.getName();
        }

        // TRACE
        @Override
        public boolean isTraceEnabled() {
            return delegate.isTraceEnabled();
        }

        @Override
        public void trace(String msg) {
            delegate.trace(prefix + msg);
        }

        @Override
        public void trace(String format, Object arg) {
            delegate.trace(prefix + format, arg);
        }

        @Override
        public void trace(String format, Object arg1, Object arg2) {
            delegate.trace(prefix + format, arg1, arg2);
        }

        @Override
        public void trace(String format, Object... arguments) {
            delegate.trace(prefix + format, arguments);
        }

        @Override
        public void trace(String msg, Throwable t) {
            delegate.trace(prefix + msg, t);
        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            return delegate.isTraceEnabled(marker);
        }

        @Override
        public void trace(Marker marker, String msg) {
            delegate.trace(marker, prefix + msg);
        }

        @Override
        public void trace(Marker marker, String format, Object arg) {
            delegate.trace(marker, prefix + format, arg);
        }

        @Override
        public void trace(Marker marker, String format, Object arg1, Object arg2) {
            delegate.trace(marker, prefix + format, arg1, arg2);
        }

        @Override
        public void trace(Marker marker, String format, Object... argArray) {
            delegate.trace(marker, prefix + format, argArray);
        }

        @Override
        public void trace(Marker marker, String msg, Throwable t) {
            delegate.trace(marker, prefix + msg, t);
        }

        // DEBUG
        @Override
        public boolean isDebugEnabled() {
            return delegate.isDebugEnabled();
        }

        @Override
        public void debug(String msg) {
            delegate.debug(prefix + msg);
        }

        @Override
        public void debug(String format, Object arg) {
            delegate.debug(prefix + format, arg);
        }

        @Override
        public void debug(String format, Object arg1, Object arg2) {
            delegate.debug(prefix + format, arg1, arg2);
        }

        @Override
        public void debug(String format, Object... arguments) {
            delegate.debug(prefix + format, arguments);
        }

        @Override
        public void debug(String msg, Throwable t) {
            delegate.debug(prefix + msg, t);
        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            return delegate.isDebugEnabled(marker);
        }

        @Override
        public void debug(Marker marker, String msg) {
            delegate.debug(marker, prefix + msg);
        }

        @Override
        public void debug(Marker marker, String format, Object arg) {
            delegate.debug(marker, prefix + format, arg);
        }

        @Override
        public void debug(Marker marker, String format, Object arg1, Object arg2) {
            delegate.debug(marker, prefix + format, arg1, arg2);
        }

        @Override
        public void debug(Marker marker, String format, Object... arguments) {
            delegate.debug(marker, prefix + format, arguments);
        }

        @Override
        public void debug(Marker marker, String msg, Throwable t) {
            delegate.debug(marker, prefix + msg, t);
        }

        // INFO
        @Override
        public boolean isInfoEnabled() {
            return delegate.isInfoEnabled();
        }

        @Override
        public void info(String msg) {
            delegate.info(prefix + msg);
        }

        @Override
        public void info(String format, Object arg) {
            delegate.info(prefix + format, arg);
        }

        @Override
        public void info(String format, Object arg1, Object arg2) {
            delegate.info(prefix + format, arg1, arg2);
        }

        @Override
        public void info(String format, Object... arguments) {
            delegate.info(prefix + format, arguments);
        }

        @Override
        public void info(String msg, Throwable t) {
            delegate.info(prefix + msg, t);
        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            return delegate.isInfoEnabled(marker);
        }

        @Override
        public void info(Marker marker, String msg) {
            delegate.info(marker, prefix + msg);
        }

        @Override
        public void info(Marker marker, String format, Object arg) {
            delegate.info(marker, prefix + format, arg);
        }

        @Override
        public void info(Marker marker, String format, Object arg1, Object arg2) {
            delegate.info(marker, prefix + format, arg1, arg2);
        }

        @Override
        public void info(Marker marker, String format, Object... arguments) {
            delegate.info(marker, prefix + format, arguments);
        }

        @Override
        public void info(Marker marker, String msg, Throwable t) {
            delegate.info(marker, prefix + msg, t);
        }

        // WARN
        @Override
        public boolean isWarnEnabled() {
            return delegate.isWarnEnabled();
        }

        @Override
        public void warn(String msg) {
            delegate.warn(prefix + msg);
        }

        @Override
        public void warn(String format, Object arg) {
            delegate.warn(prefix + format, arg);
        }

        @Override
        public void warn(String format, Object... arguments) {
            delegate.warn(prefix + format, arguments);
        }

        @Override
        public void warn(String format, Object arg1, Object arg2) {
            delegate.warn(prefix + format, arg1, arg2);
        }

        @Override
        public void warn(String msg, Throwable t) {
            delegate.warn(prefix + msg, t);
        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            return delegate.isWarnEnabled(marker);
        }

        @Override
        public void warn(Marker marker, String msg) {
            delegate.warn(marker, prefix + msg);
        }

        @Override
        public void warn(Marker marker, String format, Object arg) {
            delegate.warn(marker, prefix + format, arg);
        }

        @Override
        public void warn(Marker marker, String format, Object arg1, Object arg2) {
            delegate.warn(marker, prefix + format, arg1, arg2);
        }

        @Override
        public void warn(Marker marker, String format, Object... arguments) {
            delegate.warn(marker, prefix + format, arguments);
        }

        @Override
        public void warn(Marker marker, String msg, Throwable t) {
            delegate.warn(marker, prefix + msg, t);
        }

        // ERROR
        @Override
        public boolean isErrorEnabled() {
            return delegate.isErrorEnabled();
        }

        @Override
        public void error(String msg) {
            delegate.error(prefix + msg);
        }

        @Override
        public void error(String format, Object arg) {
            delegate.error(prefix + format, arg);
        }

        @Override
        public void error(String format, Object arg1, Object arg2) {
            delegate.error(prefix + format, arg1, arg2);
        }

        @Override
        public void error(String format, Object... arguments) {
            delegate.error(prefix + format, arguments);
        }

        @Override
        public void error(String msg, Throwable t) {
            delegate.error(prefix + msg, t);
        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            return delegate.isErrorEnabled(marker);
        }

        @Override
        public void error(Marker marker, String msg) {
            delegate.error(marker, prefix + msg);
        }

        @Override
        public void error(Marker marker, String format, Object arg) {
            delegate.error(marker, prefix + format, arg);
        }

        @Override
        public void error(Marker marker, String format, Object arg1, Object arg2) {
            delegate.error(marker, prefix + format, arg1, arg2);
        }

        @Override
        public void error(Marker marker, String format, Object... arguments) {
            delegate.error(marker, prefix + format, arguments);
        }

        @Override
        public void error(Marker marker, String msg, Throwable t) {
            delegate.error(marker, prefix + msg, t);
        }
    }
}
