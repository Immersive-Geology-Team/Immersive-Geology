package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect;
import blusunrize.immersiveengineering.client.IEManual;
import blusunrize.immersiveengineering.client.manual.IEManualInstance;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEBlocks.MetalDevices;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualEntry.SpecialElementData;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree.InnerNode;
import com.igteam.immersivegeology.client.menu.multiblock.BloomeryScreen;
import com.igteam.immersivegeology.client.menu.multiblock.ReverberationScreen;
import com.igteam.immersivegeology.client.menu.multiblock.SchematicsScreen;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.block.multiblocks.IGCoreDrillMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.IGCrystalizerMultiblock;
import com.igteam.immersivegeology.common.tag.IGTags;
import com.igteam.immersivegeology.common.world.IGWorldGen;
import com.igteam.immersivegeology.common.world.IGWorldSubscription;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class IGContent {

    public static void initializeIETweaks()
    {
        ChemthrowerHandler.registerEffect(ChemicalEnum.ChemicalWaste.getFluidTag(), new ChemthrowerEffect()
        {
            @Override
            public void applyToEntity(LivingEntity livingEntity, @Nullable Player player, ItemStack itemStack, Fluid fluid)
            {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 140));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 100,1));

				RandomSource rand = livingEntity.getRandom();
                if(rand.nextFloat() > 0.5f) livingEntity.addEffect(new MobEffectInstance(MobEffects.HARM, 1));
            }

            @Override
            public void applyToBlock(Level level, HitResult hitResult, @Nullable Player player, ItemStack itemStack, Fluid fluid)
            {
                Vec3 vec = hitResult.getLocation();
                BlockPos loc = new BlockPos((int)vec.x(), (int)vec.y(), (int)vec.z()).below();
                BlockState state = level.getBlockState(new BlockPos(loc));

                if(state.is(Blocks.GRASS_BLOCK))
                {
                    level.setBlock(loc, Blocks.DIRT.defaultBlockState(), 3);
                }

                if(state.is(Blocks.DIRT))
                {
                    level.setBlock(loc, Blocks.COARSE_DIRT.defaultBlockState(), 3);
                }
            }
        });
    }

    public static void registerContainersAndScreens()
    {
        MenuScreens.register(IGMenuTypes.BLOOMERY.getType(), BloomeryScreen::new);
        MenuScreens.register(IGMenuTypes.REVERBERATION_FURNACE.getType(), ReverberationScreen::new);
        MenuScreens.register(IGMenuTypes.SCHEMATICS.getType(), SchematicsScreen::new);
    }

    public static void initializeManualEntries()
    {
        ManualInstance instance = ManualHelper.getManual();
        InnerNode<ResourceLocation, ManualEntry> parent_category = instance.getRoot().getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "main"), 99);

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        builder.readFromFile(new ResourceLocation(IGLib.MODID, "intro"));
        instance.addEntry(parent_category, builder.create());

        InnerNode<ResourceLocation, ManualEntry> multiblock_category = parent_category.getOrCreateSubnode(new ResourceLocation(IGLib.MODID, "ig_multiblocks"), 0);
        multiblockEntry(instance, multiblock_category, "crystallizer");
        multiblockEntry(instance, multiblock_category, "coredrill");
        multiblockEntry(instance, multiblock_category, "gravityseparator");
        multiblockEntry(instance, multiblock_category, "trommel");
        multiblockEntry(instance, multiblock_category, "rotarykiln");
        multiblockEntry(instance, multiblock_category, "reverberation_furnace");
        multiblockEntry(instance, multiblock_category, "bloomery");
        multiblockEntry(instance, multiblock_category, "chemical_reactor");
        multiblockEntry(instance, multiblock_category, "ballmill");
        multiblockEntry(instance, multiblock_category, "centrifuge");

        // Create a new subnode for "Bug Bounty Contributors"
        InnerNode<ResourceLocation, ManualEntry> contributors_category = parent_category.getOrCreateSubnode(
                new ResourceLocation(IGLib.MODID, "bug_bounty_contributors"), 10);

        // Build the manual entry for the contributors
        ManualEntry.ManualEntryBuilder contributorsEntry = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        contributorsEntry.readFromFile(new ResourceLocation(IGLib.MODID, "bug_bounty_contributors"));
        instance.addEntry(contributors_category, contributorsEntry.create());
    }

    private static void multiblockEntry(ManualInstance instance, InnerNode<ResourceLocation, ManualEntry> category, String id){
        ManualEntry.ManualEntryBuilder multiblock = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
        multiblock.readFromFile(new ResourceLocation(IGLib.MODID, id));
        instance.addEntry(category, multiblock.create());
    }

    ChemthrowerEffect acidic = new ChemthrowerEffect()
    {

        @Override
        public void applyToEntity(LivingEntity livingEntity, @Nullable Player player, ItemStack itemStack, Fluid fluid)
        {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 1));
        }

        @Override
        public void applyToBlock(Level level, HitResult hitResult, @Nullable Player player, ItemStack itemStack, Fluid fluid)
        {
            Vec3 vec = hitResult.getLocation();
            BlockPos loc = new BlockPos((int)vec.x(), (int)vec.y(), (int)vec.z());
            BlockState state = level.getBlockState(new BlockPos(loc));
            if(state.is(Blocks.GRASS_BLOCK))
            {
                level.setBlock(loc, Blocks.DIRT.defaultBlockState(), 0);
            }
        }
    };
}
