/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import blusunrize.immersiveengineering.api.utils.CapabilityUtils;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class IGGenericBucketItem extends BucketItem implements IGFlagItem {
    private final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
    private final BlockCategoryFlags fluid_category;

    public IGGenericBucketItem(Supplier<? extends Fluid> fluid, BlockCategoryFlags flag, MaterialInterface<?> material) {
        super(fluid, new Properties());
        this.materialMap.put(MaterialTexture.base, material);
        this.fluid_category = flag;
    }

    public IGGenericBucketItem(Supplier<? extends Fluid> fluid, BlockCategoryFlags flag, MaterialInterface<?> material, MaterialInterface<?> extra) {
        this(fluid, flag, material);
        this.materialMap.put(MaterialTexture.overlay, extra);
    }

    public int getColor(int index) {
        if(index == 0) return 0xffffffff;
        if (index >= materialMap.values().size()) index = materialMap.values().size() - 1;
        //let's use last available colour. map could not be empty
        return materialMap.get(MaterialTexture.values()[index]).getColor(ItemCategoryFlags.BUCKET);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        List<String> materialList = new ArrayList<>();
        String type = "bucket";
        MaterialInterface<?> baseMaterial = getMaterial(MaterialTexture.base);
        MaterialInterface<?> overlayMaterial = getMaterial(MaterialTexture.overlay);

        if(baseMaterial instanceof MetalEnum)
        {
            type = "bucket_molten";
        }

        if(baseMaterial instanceof ChemicalEnum)
        {
            type = "flask_slurry";

            if(overlayMaterial != null) {
                materialList.add(I18n.get("material.immersivegeology." + overlayMaterial.getName()));
                materialList.add(I18n.get("component.immersivegeology." + baseMaterial.getName()));
            } else {
                type = "flask";
                materialList.add(I18n.get("material.immersivegeology." + baseMaterial.getName()));
            }
        } else {
            materialList.add(I18n.get("material.immersivegeology." + baseMaterial.getName()));
        }

        return Component.translatable("item.immersivegeology." + type, materialList.toArray());
    }

    @Override
    public ItemCategoryFlags getFlag() {
        return ItemCategoryFlags.BUCKET;
    }

    public BlockCategoryFlags getFluidCategory()
    {
        return fluid_category;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return ItemCategoryFlags.BUCKET.getSubGroup();
    }

    @Override
    public Collection<MaterialInterface<?>> getMaterials() {
        return materialMap.values();
    }

    @Override
    public MaterialInterface<?> getMaterial(MaterialTexture t) {
        return materialMap.get(t);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new FluidHandler(stack);
    }

    private static class FluidHandler implements IFluidHandlerItem, ICapabilityProvider
    {
        private final ItemStack stack;
        private boolean empty = false;

        private FluidHandler(ItemStack stack)
        {
            this.stack = stack;
        }

        private FluidStack getFluid()
        {
            Fluid fluid = ((IGGenericBucketItem) stack.getItem()).getFluid();
            if(empty)
                return FluidStack.EMPTY;
            else
                return new FluidStack(fluid, FluidType.BUCKET_VOLUME);
        }

        @Nonnull
        @Override
        public ItemStack getContainer()
        {
            return empty?new ItemStack(Items.BUCKET): stack;
        }

        @Override
        public int getTanks()
        {
            return 1;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank)
        {
            if(tank==0)
                return getFluid();
            else
                return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank)
        {
            return tank==0?FluidType.BUCKET_VOLUME: 0;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack)
        {
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            return 0;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action)
        {
            FluidStack fluid = getFluid();
            if(!fluid.isFluidEqual(resource)||!Objects.equals(fluid.getTag(), resource.getTag()))
                return FluidStack.EMPTY;
            return drain(resource.getAmount(), action);
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action)
        {
            if(empty||stack.getCount() > 1||maxDrain < FluidType.BUCKET_VOLUME)
                return FluidStack.EMPTY;

            FluidStack potion = getFluid();
            if(action.execute())
                empty = true;
            return potion;
        }

        private final LazyOptional<IFluidHandlerItem> lazyOpt = CapabilityUtils.constantOptional(this);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            if(cap==ForgeCapabilities.FLUID_HANDLER_ITEM)
                return lazyOpt.cast();
            else
                return LazyOptional.empty();
        }
    }
}
