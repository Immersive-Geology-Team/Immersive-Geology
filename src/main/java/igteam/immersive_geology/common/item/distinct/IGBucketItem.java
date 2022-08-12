package igteam.immersive_geology.common.item.distinct;

import igteam.immersive_geology.common.item.helper.IFlaskPickupHandler;
import igteam.immersive_geology.common.item.helper.IGFlaskFluidHandler;
import igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.client.menu.helper.IGItemGroup;
import igteam.api.item.IGItemType;
import igteam.api.materials.MiscEnum;
import igteam.api.materials.data.MaterialBase;
import igteam.api.main.IGRegistryProvider;
import igteam.api.materials.data.slurry.variants.MaterialSlurryWrapper;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialTexture;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.menu.ItemSubGroup;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.client.resources.I18n;
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

import java.util.*;
import java.util.List;
import java.util.function.Supplier;


public class IGBucketItem extends BucketItem implements IGItemType {
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
        setRegistryName(IGRegistrationHolder.getRegistryKey(this));
    }

    public ITextComponent getDisplayName(ItemStack stack) {
        List<String> materialList = new ArrayList<>();
        if(fluidMaterial instanceof MaterialSlurryWrapper){
            MaterialSlurryWrapper wrapper = (MaterialSlurryWrapper) fluidMaterial;
            materialList.add(I18n.format("material.immersive_geology." + wrapper.getSoluteMaterial().getName()));
            materialList.add(I18n.format("component.immersive_geology." + wrapper.getFluidBase().getName()));
        } else {
            materialList.add(I18n.format("material.immersive_geology." + fluidMaterial.getName()));
        }
        return new TranslationTextComponent("item.immersive_geology." + pattern.getName(), materialList.toArray());
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
        return pattern;
    }

    @Override
    public String getHolderKey() {
        return IGRegistryProvider.getRegistryKey(fluidMaterial, pattern).getPath();
    }

    @Override
    public BlockPattern getBlockPattern() {
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
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerEntity, Hand handIn) {
        if(pattern == ItemPattern.bucket){
            return super.onItemRightClick(world, playerEntity, handIn);
        }
        ItemStack itemstack = playerEntity.getHeldItem(handIn);
        RayTraceResult raytraceresult = rayTrace(world, playerEntity, this.igContainedBlock == Fluids.EMPTY ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
        ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(playerEntity, world, itemstack, raytraceresult);
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
            if (world.isBlockModifiable(playerEntity, blockpos) && playerEntity.canPlayerEdit(blockpos1, direction, itemstack)) {
                BlockState blockstate1;
                if (this.igContainedBlock == Fluids.EMPTY) {
                    blockstate1 = world.getBlockState(blockpos);
                    if (blockstate1.getBlock() instanceof IFlaskPickupHandler) {
                        Fluid fluid = ((IFlaskPickupHandler) blockstate1.getBlock()).pickupFlaskFluid(world, blockpos, blockstate1);
                        if (fluid != Fluids.EMPTY) {
                            playerEntity.addStat(Stats.ITEM_USED.get(this));
                            SoundEvent soundevent = this.igContainedBlock.getAttributes().getFillSound();
                            if (soundevent == null) {
                                soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
                            }

                            playerEntity.playSound(soundevent, 1.0F, 1.0F);
                            ItemStack itemstack1 = DrinkHelper.fill(itemstack, playerEntity, new ItemStack(fluid.getFilledBucket()));
                            if (!world.isRemote) {
                                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) playerEntity, new ItemStack(fluid.getFilledBucket()));
                            }

                            return ActionResult.func_233538_a_(itemstack1, world.isRemote());
                        }
                    }

                    return ActionResult.resultFail(itemstack);
                } else {
                    blockstate1 = world.getBlockState(blockpos);
                    BlockPos blockpos2 = this.canBlockContainFluid(world, blockpos, blockstate1) ? blockpos : blockpos1;
                    if (this.tryPlaceContainedLiquid(playerEntity, world, blockpos2, blockraytraceresult)) {
                        this.onLiquidPlaced(world, itemstack, blockpos2);
                        if (playerEntity instanceof ServerPlayerEntity) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) playerEntity, blockpos2, itemstack);
                        }

                        playerEntity.addStat(Stats.ITEM_USED.get(this));
                        return ActionResult.func_233538_a_(this.emptyBucket(itemstack, playerEntity), world.isRemote());
                    } else {
                        return ActionResult.resultFail(itemstack);
                    }
                }
            } else {
                return ActionResult.resultFail(itemstack);
            }
        }
        
    }

    private boolean canBlockContainFluid(World p_canBlockContainFluid_1_, BlockPos p_canBlockContainFluid_2_, BlockState p_canBlockContainFluid_3_) {
        return p_canBlockContainFluid_3_.getBlock() instanceof ILiquidContainer && ((ILiquidContainer)p_canBlockContainFluid_3_.getBlock()).canContainFluid(p_canBlockContainFluid_1_, p_canBlockContainFluid_2_, p_canBlockContainFluid_3_, this.igContainedBlock);
    }

    @Override
    protected ItemStack emptyBucket(ItemStack itemStack, PlayerEntity playerEntity) {
        if (pattern == ItemPattern.bucket) {
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
        if (FluidUtil.getFluidContained(stack).isPresent()) {
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

    public MaterialBase getMaterial(MaterialTexture base) {
        if(base == MaterialTexture.base){
            return MiscEnum.Glass.instance();
        }
        return fluidMaterial;
    }
}

