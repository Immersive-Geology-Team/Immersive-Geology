package com.igteam.immersive_geology.common.item.distinct;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.item.helper.IFlaskPickupHandler;
import com.igteam.immersive_geology.common.item.helper.IGFlaskFluidHandler;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.item.IGItemType;
import igteam.immersive_geology.materials.MiscEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.data.slurry.variants.MaterialSlurryWrapper;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import igteam.immersive_geology.menu.ItemSubGroup;
import igteam.immersive_geology.menu.helper.IGItemGroup;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Supplier;


public class IGBucketItem extends BucketItem implements IGItemType{

    private Fluid igContainedBlock;
    protected ItemSubGroup subGroup;
    private final MaterialBase fluidMaterial;
    protected final MaterialPattern pattern;

    public IGBucketItem(Supplier<? extends Fluid> fluid_supplier, MaterialBase material, MaterialPattern pattern, Properties p_i244800_2_) {
        super(fluid_supplier, p_i244800_2_.group(IGItemGroup.IGGroup));
        this.igContainedBlock = fluid_supplier.get();
        this.fluidMaterial = material;
        this.subGroup = ItemSubGroup.misc;
        this.pattern = pattern;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        ArrayList<String> localizedNames = new ArrayList<>();
        if(getFluid() == Fluids.EMPTY){
            localizedNames.add(fluidMaterial.getName());
            TranslationTextComponent name = new TranslationTextComponent("item."+ IGLib.MODID+".empty_"+ pattern.getName().toLowerCase(Locale.ENGLISH), localizedNames.toArray(new Object[localizedNames.size()]));
            return name;
        } else {
            if(pattern == MiscPattern.slurry){
                MaterialSlurryWrapper slurry = (MaterialSlurryWrapper) fluidMaterial;
                localizedNames.add(slurry.getSoluteMaterial().getName());
                localizedNames.add(slurry.getFluidBase().getName());
            } else {
                localizedNames.add(fluidMaterial.getName());
            }
            TranslationTextComponent name = new TranslationTextComponent("item."+ IGLib.MODID+"."+ pattern.getName().toLowerCase(Locale.ENGLISH), localizedNames.toArray(new Object[localizedNames.size()]));
            return name;
        }
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return subGroup;
    }

    @Override
    public Collection<MaterialInterface> getMaterials() {
        return null;
    }

    @Override
    public MaterialPattern getPattern() {
        return null;
    }

    @Override
    public String getHolderKey() {
        return null;
    }

    @Override
    public boolean hasCustomItemColours()
    {
        return true;
    }

    @Override
    public int getColourForIGItem(ItemStack stack, int pass) {
        if(pass == 0) {
            return fluidMaterial.getColor(pattern);
        } else {
            return 0xFFFFFF;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
        if(pattern == ItemPattern.flask){
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
        if (pattern == ItemPattern.flask) {
            return super.emptyBucket(itemStack, playerEntity);
        } else {
            Item flask = MiscEnum.Glass.getItem(ItemPattern.flask);
            return !playerEntity.abilities.isCreativeMode ? new ItemStack(flask) : itemStack;
        }
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
        if (this.pattern == ItemPattern.flask)
        {
                return new IGFlaskFluidHandler(stack);
        } else
        {
            return  new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
        }
    }
}

