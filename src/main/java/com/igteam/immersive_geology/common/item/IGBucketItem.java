package com.igteam.immersive_geology.common.item;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.materials.material_data.fluids.slurry.MaterialSlurryWrapper;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.item.helper.IFlaskPickupHandler;
import com.igteam.immersive_geology.common.item.helper.IGFlaskFluidHandler;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Supplier;

public class IGBucketItem extends BucketItem implements IGSubGroup, IEItemInterfaces.IColouredItem{

    private Fluid igContainedBlock;
    protected ItemSubGroup subGroup;
    private final Material fluidMaterial;
    protected final MaterialUseType useType;

    public IGBucketItem(Supplier<? extends Fluid> fluid_supplier, Material material, MaterialUseType useType, Properties p_i244800_2_) {
        super(fluid_supplier, p_i244800_2_.group(ImmersiveGeology.IGGroup));
        this.igContainedBlock = fluid_supplier.get();
        this.fluidMaterial = material;
        this.subGroup = ItemSubGroup.misc;
        this.useType = useType;
    }

    public String getHoldingName(){
        return IGRegistrationHolder.getRegistryKey(fluidMaterial, useType);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        ArrayList<String> localizedNames = new ArrayList<>();
        if(getFluid() == Fluids.EMPTY){
            localizedNames.add(fluidMaterial.getDisplayName());
            TranslationTextComponent name = new TranslationTextComponent("item."+ IGLib.MODID+".empty_"+ useType.getName().toLowerCase(Locale.ENGLISH), localizedNames.toArray(new Object[localizedNames.size()]));
            return name;
        } else {
            if(fluidMaterial instanceof MaterialSlurryWrapper){
                MaterialSlurryWrapper slurry = (MaterialSlurryWrapper) fluidMaterial;
                localizedNames.add(slurry.getSoluteMaterial().getDisplayName());
                localizedNames.add(slurry.getBaseFluidMaterial().getComponentName());
            } else {
                localizedNames.add(fluidMaterial.getDisplayName());
            }
            TranslationTextComponent name = new TranslationTextComponent("item."+ IGLib.MODID+"."+ useType.getName().toLowerCase(Locale.ENGLISH), localizedNames.toArray(new Object[localizedNames.size()]));
            return name;
        }

    }

    @Override
    public ItemSubGroup getSubGroup() {
        return subGroup;
    }

    @Override
    public boolean hasCustomItemColours()
    {
        return true;
    }

    @Override
    public int getColourForIEItem(ItemStack stack, int pass)
    {
        if(pass == 0) {
            return fluidMaterial.getColor(0);
        } else {
            return 0xFFFFFF;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
        if(useType == MaterialUseType.BUCKET){
           return super.onItemRightClick(p_77659_1_, p_77659_2_, p_77659_3_);
        } else {
            ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
            RayTraceResult raytraceresult = rayTrace(p_77659_1_, p_77659_2_, this.igContainedBlock == Fluids.EMPTY ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
            ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(p_77659_2_, p_77659_1_, itemstack, raytraceresult);
            if (ret != null) {
                return ret;
            } else if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
                return ActionResult.resultPass(itemstack);
            } else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
                return ActionResult.resultPass(itemstack);
            } else {
                BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
                BlockPos blockpos = blockraytraceresult.getPos();
                Direction direction = blockraytraceresult.getFace();
                BlockPos blockpos1 = blockpos.offset(direction);
                if (p_77659_1_.isBlockModifiable(p_77659_2_, blockpos) && p_77659_2_.canPlayerEdit(blockpos1, direction, itemstack)) {
                    BlockState blockstate1;
                    if (this.igContainedBlock == Fluids.EMPTY) {
                        blockstate1 = p_77659_1_.getBlockState(blockpos);
                        if (blockstate1.getBlock() instanceof IFlaskPickupHandler) {
                            Fluid fluid = ((IFlaskPickupHandler) blockstate1.getBlock()).pickupFlaskFluid(p_77659_1_, blockpos, blockstate1);
                            if (fluid != Fluids.EMPTY) {
                                p_77659_2_.addStat(Stats.ITEM_USED.get(this));
                                SoundEvent soundevent = this.igContainedBlock.getAttributes().getFillSound();
                                if (soundevent == null) {
                                    soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
                                }

                                p_77659_2_.playSound(soundevent, 1.0F, 1.0F);
                                ItemStack itemstack1 = DrinkHelper.fill(itemstack, p_77659_2_, new ItemStack(fluid.getFilledBucket()));
                                if (!p_77659_1_.isRemote) {
                                    CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) p_77659_2_, new ItemStack(fluid.getFilledBucket()));
                                }

                                return ActionResult.func_233538_a_(itemstack1, p_77659_1_.isRemote());
                            }
                        }

                        return ActionResult.resultFail(itemstack);
                    } else {
                        blockstate1 = p_77659_1_.getBlockState(blockpos);
                        BlockPos blockpos2 = this.canBlockContainFluid(p_77659_1_, blockpos, blockstate1) ? blockpos : blockpos1;
                        if (this.tryPlaceContainedLiquid(p_77659_2_, p_77659_1_, blockpos2, blockraytraceresult)) {
                            this.onLiquidPlaced(p_77659_1_, itemstack, blockpos2);
                            if (p_77659_2_ instanceof ServerPlayerEntity) {
                                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) p_77659_2_, blockpos2, itemstack);
                            }

                            p_77659_2_.addStat(Stats.ITEM_USED.get(this));
                            return ActionResult.func_233538_a_(this.emptyBucket(itemstack, p_77659_2_), p_77659_1_.isRemote());
                        } else {
                            return ActionResult.resultFail(itemstack);
                        }
                    }
                } else {
                    return ActionResult.resultFail(itemstack);
                }
            }
        }
    }

    private boolean canBlockContainFluid(World p_canBlockContainFluid_1_, BlockPos p_canBlockContainFluid_2_, BlockState p_canBlockContainFluid_3_) {
        return p_canBlockContainFluid_3_.getBlock() instanceof ILiquidContainer && ((ILiquidContainer)p_canBlockContainFluid_3_.getBlock()).canContainFluid(p_canBlockContainFluid_1_, p_canBlockContainFluid_2_, p_canBlockContainFluid_3_, this.igContainedBlock);
    }

    @Override
    protected ItemStack emptyBucket(ItemStack itemStack, PlayerEntity playerEntity) {
        if (useType == MaterialUseType.BUCKET) {
            return super.emptyBucket(itemStack, playerEntity);
        } else {
            Item flask = IGRegistrationHolder.getItemByMaterial(MaterialEnum.Glass.getMaterial(), MaterialUseType.FLASK);
            return !playerEntity.abilities.isCreativeMode ? new ItemStack(flask) : itemStack;
        }
    }

    public MaterialUseType getUseType() {
        return useType;
    }


    @Override
    public boolean hasContainerItem(ItemStack stack){
        return FluidUtil.getFluidContained(stack).isPresent();
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        if (FluidUtil.getFluidContained(stack) != null) {
            ItemStack ret = stack.copy();
            FluidUtil.getFluidHandler(ret).ifPresent(handler -> handler.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE));
            return ret;
        }
        return stack;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        //(container, new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Glass.getMaterial(), MaterialUseType.FLASK)), capacity);
        if (this.useType == MaterialUseType.FLASK )
        {
                return new IGFlaskFluidHandler(stack);
        } else
        {
            return  new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
        }

    }
}

