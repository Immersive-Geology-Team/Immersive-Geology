/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.metal.BlastFurnacePreheaterBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler.IntRange;
import com.igteam.immersivegeology.common.block.multiblocks.IGBulkBlastFurnaceMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BulkBlastFurnaceRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shapes.BulkBlastFurnaceShape;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BulkBlastFurnaceLogic implements IMultiblockLogic<BulkBlastFurnaceLogic.State>, MBOverlayText<BulkBlastFurnaceLogic.State>,
		IServerTickableComponent<BulkBlastFurnaceLogic.State>, IClientTickableComponent<BulkBlastFurnaceLogic.State>
{
	public static final int GRID_WIDTH = 4;
	public static final int NUM_INPUT_SLOTS = GRID_WIDTH*GRID_WIDTH;
	public static final int TOTAL_TANK_CAPACITY = 128_000;

	public static final int MAX_HEAT = 200;
	public static final int PREHEATER_HEAT = 80;
	public static final int PREHEATER_MISSING = 0;
	public static final int PREHEATER_IDLE = 1;
	public static final int PREHEATER_ACTIVE = 2;
	private static final int BASE_CHARGE_HEAT = 25;
	private static final int HEAT_PER_COKE = 12;
	private static final float HEAT_RISE = 0.5f;
	private static final float HEAT_DECAY = 0.05f;
	public static final float MIN_RATIO_TOLERANCE = 0.25f;
	public static final float GOOD_QUALITY = 0.75f;
	private static final int COKE_BLOCK_VALUE = 9;
	private static final float MAX_BANKED_COKE_FRACTION = 0.25f;

	public static final BlockPos REDSTONE_IN = new BlockPos(2, 0, 0);

	private static final CapabilityPosition ITEM_INPUT_CAP = new CapabilityPosition(1, 7, 1, RelativeBlockFace.UP);
	private static final MultiblockFace METAL_OUTPUT = new MultiblockFace(1, 0, 0, RelativeBlockFace.FRONT);
	private static final MultiblockFace SLAG_OUTPUT = new MultiblockFace(1, 0, 2, RelativeBlockFace.BACK);
	private static final CapabilityPosition METAL_OUTPUT_CAP = new CapabilityPosition(1, 0, 0, RelativeBlockFace.FRONT);
	private static final CapabilityPosition SLAG_OUTPUT_CAP = new CapabilityPosition(1, 0, 2, RelativeBlockFace.BACK);

	public static final BlockPos[][] PREHEATER_CANDIDATES = {
			{new BlockPos(-1, 1, 1), new BlockPos(-1, 2, 1)},
			{new BlockPos(3, 1, 1), new BlockPos(3, 2, 1)}
	};

	private static final Vec3 FLUE_VENT = new Vec3(1.5, 2.0625, 2.8125);
	private static final RelativeBlockFace FLUE_FACE = RelativeBlockFace.BACK;
	private static final double FLUE_SPEED = 0.15;
	private static final double FLUE_RISE = 0.03125;

	private static MultiblockFace neighbourOf(MultiblockFace port)
	{
		return new MultiblockFace(port.face(), port.face().offsetRelative(port.posInMultiblock(), 1));
	}

	public static TagKey<Item> fluxTag()
	{
		return MetalEnum.Calcium.getItemTag(ItemCategoryFlags.METAL_OXIDE);
	}

	public static int fuelValue(ItemStack stack)
	{
		if(stack.is(IETags.coalCoke)) return stack.getCount();
		if(stack.is(IETags.getItemTag(IETags.coalCokeBlock))) return stack.getCount()*COKE_BLOCK_VALUE;
		return 0;
	}

	public static boolean isCoke(ItemStack stack)
	{
		return fuelValue(stack) > 0;
	}

	public static boolean isFlux(ItemStack stack)
	{
		return stack.is(fluxTag());
	}

	public static boolean isChargeComponent(@Nullable Level level, ItemStack stack)
	{
		return isCoke(stack)||isFlux(stack)||BulkBlastFurnaceRecipe.findRecipe(level, stack)!=null;
	}

	@Override
	public State createInitialState(IInitialMultiblockContext<State> ctx)
	{
		return new State(ctx);
	}

	@Override
	public void tickServer(IMultiblockContext<State> context)
	{
		final State state = context.getState();
		final IMultiblockLevel level = context.getLevel();

		state.preheaters = state.tickPreheaters(level);
		state.heatFloor = state.preheaters*PREHEATER_HEAT;

		final boolean wasActive = state.active;
		if(state.processMax > 0)
		{
			state.active = true;
			if(state.heating)
			{
				state.heat = Math.min(state.chargeHeatTarget, state.heat+HEAT_RISE);
				if(state.heat >= state.chargeHeatTarget)
				{
					state.heating = false;
					state.readoutStatus = ChargeStatus.SMELTING;
				}
			}
			else
			{
				if(state.heatDrawPerTick > 0) state.heat -= state.heatDrawPerTick;
				state.heat = Mth.clamp(state.heat, 0, MAX_HEAT);
				state.progress++;
				if(state.progress >= state.processMax) finishCharge(context);
			}
		}
		else
		{
			state.heat = state.heat < state.heatFloor
					?Math.min(state.heatFloor, state.heat+HEAT_RISE)
					: Math.max(state.heatFloor, state.heat-HEAT_DECAY);
			state.active = false;
			if(state.rsState.isEnabled(context)) tryStartCharge(context);
		}
		updateReadout(state, level.getRawLevel());

		if(state.metalTank.getFluidAmount() > 0) pushFluid(context, state.metalTank, state.metalOutput);
		if(state.slagTank.getFluidAmount() > 0) pushFluid(context, state.slagTank, state.slagOutput);

		final boolean litInWorld = level.getBlockState(IGBulkBlastFurnaceMultiblock.INSTANCE.getMasterFromOriginOffset()).getValue(IEProperties.ACTIVE);
		if(state.active!=litInWorld)
			NonMirrorableWithActiveBlock.setActive(level, IGBulkBlastFurnaceMultiblock.INSTANCE, state.active);

		context.markMasterDirty();
		if(wasActive!=state.active) context.requestMasterBESync();
	}

	private record ChargeAnalysis(
			BulkBlastFurnaceRecipe recipe, boolean refining, float units, int coke, int flux,
			float cokeIdeal, float fluxIdeal, int drained
	)
	{
		float cokeNeeded()
		{
			return units*cokeIdeal;
		}

		float fluxNeeded()
		{
			return units*fluxIdeal;
		}
	}

	@Nullable
	private ChargeAnalysis analyse(State state, @Nullable Level level)
	{
		BulkBlastFurnaceRecipe recipe = null;
		for(int slot = 0; slot < NUM_INPUT_SLOTS&&recipe==null; slot++)
			recipe = BulkBlastFurnaceRecipe.findRecipe(level, state.inventory.getStackInSlot(slot));

		final boolean refining = recipe==null;
		if(refining) recipe = BulkBlastFurnaceRecipe.findRefiningRecipe(level, state.metalTank.getFluid());
		if(recipe==null) return null;

		int ore = 0, coke = 0, flux = 0;
		for(int slot = 0; slot < NUM_INPUT_SLOTS; slot++)
		{
			ItemStack stack = state.inventory.getStackInSlot(slot);
			if(stack.isEmpty()) continue;
			if(recipe.oreInput!=null&&recipe.oreInput.testIgnoringSize(stack)) ore += stack.getCount();
			else if(isCoke(stack)) coke += fuelValue(stack);
			else if(isFlux(stack)) flux += stack.getCount();
		}

		final int drained;
		final float units;
		if(refining)
		{
			drained = state.metalTank.getFluidAmount();
			if(drained < recipe.meltInput.getAmount()) return null;
			units = drained/(float)recipe.meltInput.getAmount();
		}
		else
		{
			if(ore <= 0) return null;
			drained = 0;
			units = ore;
		}

		final BulkBlastFurnaceRecipe.Regime regime = recipe.regimeFor(coke/units);
		return new ChargeAnalysis(recipe, refining, units, coke, flux, regime.cokeIdeal(), recipe.fluxRatio, drained);
	}

	private void updateReadout(State state, @Nullable Level level)
	{
		if(state.processMax > 0) return;

		ChargeAnalysis analysis = analyse(state, level);
		if(analysis==null)
		{
			state.readoutMaterial = 0;
			state.readoutFuel = 0;
			state.readoutFuelNeeded = 0;
			state.readoutFlux = 0;
			state.readoutFluxNeeded = 0;
			state.readoutHeatRequired = 0;
			state.readoutCarbonTarget = 0;
			state.readoutHeatCap = 0;
			state.readoutStatus = ChargeStatus.IDLE;
			return;
		}
		storeReadout(state, analysis);
	}

	private static void storeReadout(State state, ChargeAnalysis analysis)
	{
		state.readoutMaterial = analysis.units();
		state.readoutFuel = analysis.coke();
		state.readoutFuelNeeded = analysis.cokeNeeded();
		state.readoutFlux = analysis.flux();
		state.readoutFluxNeeded = analysis.fluxNeeded();
		state.readoutHeatRequired = analysis.recipe().getHeatRequired();
		state.readoutCarbonTarget = carbonTargetHeat(analysis);
		state.readoutHeatCap = heatCap(analysis);
		state.readoutStatus = statusOf(state, analysis);
	}

	public static float heatCap(ChargeAnalysis analysis)
	{
		if(analysis.cokeIdeal() <= 0) return MAX_HEAT;
		float supplied = analysis.coke()/analysis.units();
		return MAX_HEAT*Mth.clamp(supplied/analysis.cokeIdeal(), 0, 1);
	}

	private static float chargeHeatTarget(State state, ChargeAnalysis analysis)
	{
		float fromFuel = Math.max(state.heatFloor, BASE_CHARGE_HEAT+analysis.coke()*HEAT_PER_COKE);
		return Math.min(heatCap(analysis), fromFuel);
	}

	private record ChargeOutput(FluidStack metal, FluidStack slag, float quality, float cokeFromHeat)
	{
	}

	private static ChargeOutput projectOutput(State state, ChargeAnalysis analysis)
	{
		final BulkBlastFurnaceRecipe recipe = analysis.recipe();
		final BulkBlastFurnaceRecipe.Regime regime = recipe.regimeFor(analysis.coke()/analysis.units());
		final float bankedCoke = Math.max(0, (state.heat-recipe.getHeatRequired())/HEAT_PER_COKE);
		final float blastAssist = Math.min(bankedCoke, analysis.cokeNeeded()*MAX_BANKED_COKE_FRACTION);
		final float cokeFromHeat = Mth.clamp(analysis.cokeNeeded()-analysis.coke(), 0, blastAssist);
		final float quality = ratioQuality((analysis.coke()+cokeFromHeat)/analysis.units(), analysis.cokeIdeal())
				*ratioQuality(analysis.flux()/analysis.units(), analysis.fluxIdeal());

		final int volume = analysis.refining()?analysis.drained(): Math.round(analysis.units()*regime.melt().getAmount());
		final int metalAmount = Math.round(volume*recipe.metalFraction(quality));
		final int slagAmount = volume-metalAmount;
		return new ChargeOutput(
				new FluidStack(regime.melt().getFluid(), metalAmount),
				new FluidStack(MiscEnum.MoltenSlag.getFluid(BlockCategoryFlags.FLUID), slagAmount),
				quality, cokeFromHeat
		);
	}

	private static boolean outputFits(State state, ChargeAnalysis analysis, ChargeOutput output)
	{
		if(output.slag().getAmount() > 0&&state.slagTank.fill(output.slag(), FluidAction.SIMULATE) < output.slag().getAmount())
			return false;
		if(analysis.refining()||output.metal().getAmount() <= 0) return true;
		return state.metalTank.fill(output.metal(), FluidAction.SIMULATE) >= output.metal().getAmount();
	}

	private static float carbonTargetHeat(ChargeAnalysis analysis)
	{
		float shortfall = Math.min(
				Math.max(0, analysis.cokeNeeded()-analysis.coke()),
				analysis.cokeNeeded()*MAX_BANKED_COKE_FRACTION
		);
		return analysis.recipe().getHeatRequired()+shortfall*HEAT_PER_COKE;
	}

	private static ChargeStatus statusOf(State state, @Nullable ChargeAnalysis analysis)
	{
		if(state.processMax > 0) return state.heating?ChargeStatus.HEATING: ChargeStatus.SMELTING;
		if(analysis==null) return ChargeStatus.IDLE;
		if(state.heat <= 0) return ChargeStatus.NEEDS_HEAT;
		if(analysis.coke() <= 0) return ChargeStatus.NEEDS_FUEL;
		if(analysis.flux() < Math.floor(analysis.fluxNeeded())) return ChargeStatus.NEEDS_FLUX;
		if(chargeHeatTarget(state, analysis) < analysis.recipe().getHeatRequired()) return ChargeStatus.NEEDS_FUEL;
		if(!outputFits(state, analysis, projectOutput(state, analysis))) return ChargeStatus.OUTPUT_FULL;
		return ChargeStatus.READY;
	}

	private void tryStartCharge(IMultiblockContext<State> context)
	{
		final State state = context.getState();
		final ChargeAnalysis analysis = analyse(state, context.getLevel().getRawLevel());
		if(analysis==null) return;

		storeReadout(state, analysis);
		if(statusOf(state, analysis)!=ChargeStatus.READY) return;

		final BulkBlastFurnaceRecipe recipe = analysis.recipe();
		final ChargeOutput output = projectOutput(state, analysis);

		for(int slot = 0; slot < NUM_INPUT_SLOTS; slot++)
		{
			ItemStack stack = state.inventory.getStackInSlot(slot);
			if(stack.isEmpty()) continue;
			if((recipe.oreInput!=null&&recipe.oreInput.testIgnoringSize(stack))||isCoke(stack)||isFlux(stack))
				state.inventory.setStackInSlot(slot, ItemStack.EMPTY);
		}
		if(analysis.refining()) state.metalTank.drain(analysis.drained(), FluidAction.EXECUTE);

		state.pendingMetal = output.metal();
		state.pendingSlag = output.slag();
		state.quality = output.quality();
		state.progress = 0;
		state.processMax = Math.max(1, (int)Math.round(recipe.getTotalProcessTime()*Math.sqrt(analysis.units())));
		state.chargeHeatTarget = chargeHeatTarget(state, analysis);
		state.heatDrawPerTick = output.cokeFromHeat()*HEAT_PER_COKE/state.processMax;
		state.heating = state.heat < state.chargeHeatTarget;
		state.heatingMax = Math.max(1, Math.round((state.chargeHeatTarget-state.heat)/HEAT_RISE));
		state.heatingStart = state.heat;
		state.readoutStatus = state.heating?ChargeStatus.HEATING: ChargeStatus.SMELTING;
	}

	private static float ratioQuality(float actual, float ideal)
	{
		return Math.max(0, 1-Math.abs(actual-ideal)/Math.max(ideal, MIN_RATIO_TOLERANCE));
	}

	private void finishCharge(IMultiblockContext<State> context)
	{
		final State state = context.getState();
		if(!state.pendingMetal.isEmpty()) state.metalTank.fill(state.pendingMetal, FluidAction.EXECUTE);
		if(!state.pendingSlag.isEmpty()) state.slagTank.fill(state.pendingSlag, FluidAction.EXECUTE);
		state.pendingMetal = FluidStack.EMPTY;
		state.pendingSlag = FluidStack.EMPTY;
		state.progress = 0;
		state.processMax = 0;
		state.chargeHeatTarget = 0;
		state.heatDrawPerTick = 0;
		state.heating = false;
		state.heatingMax = 0;
		state.heatingStart = 0;
		context.requestMasterBESync();
	}

	private void pushFluid(IMultiblockContext<State> context, FluidTank tank, CapabilityReference<IFluidHandler> outputRef)
	{
		IFluidHandler output = outputRef.getNullable();
		if(output==null) return;

		int outSize = Math.min(FluidType.BUCKET_VOLUME, tank.getFluidAmount());
		FluidStack out = Utils.copyFluidStackWithAmount(tank.getFluid(), outSize, false);
		int accepted = output.fill(out, FluidAction.SIMULATE);
		if(accepted <= 0) return;

		int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
		tank.drain(drained, FluidAction.EXECUTE);
		context.markMasterDirty();
		context.requestMasterBESync();
	}

	@Override
	public void tickClient(IMultiblockContext<State> context)
	{
		final State state = context.getState();
		if(!state.active||!context.getLevel().shouldTickModulo(2)) return;

		final Vec3 vent = context.getLevel().toAbsolute(FLUE_VENT);
		final Direction out = context.getLevel().toAbsolute(FLUE_FACE);
		final double speed = FLUE_SPEED*ApiUtils.RANDOM.nextDouble(0.6, 1.4);
		context.getLevel().getRawLevel().addAlwaysVisibleParticle(
				ParticleTypes.CAMPFIRE_COSY_SMOKE,
				vent.x, vent.y, vent.z,
				out.getStepX()*speed+drift(), FLUE_RISE, out.getStepZ()*speed+drift()
		);
	}

	private static double drift()
	{
		return ApiUtils.RANDOM.nextDouble(-0.015625, 0.015625);
	}

	@Override
	public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
	{
		final State state = ctx.getState();
		if(cap==ForgeCapabilities.ITEM_HANDLER&&ITEM_INPUT_CAP.equalsOrNullFace(position))
			return state.itemInputCap.cast(ctx);
		if(cap==ForgeCapabilities.FLUID_HANDLER)
		{
			if(METAL_OUTPUT_CAP.equals(position)) return state.metalCap.cast(ctx);
			if(SLAG_OUTPUT_CAP.equals(position)) return state.slagCap.cast(ctx);
		}
		return LazyOptional.empty();
	}

	@Override
	public void dropExtraItems(State state, Consumer<ItemStack> drop)
	{
		MBInventoryUtils.dropItems(state.inventory, drop);
	}

	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType)
	{
		return BulkBlastFurnaceShape.GETTER;
	}

	@Nullable
	@Override
	public List<Component> getOverlayText(State state, Player player, boolean hammer)
	{
		if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND)))
			return List.of(
					TextUtils.formatFluidStack(state.metalTank.getFluid()),
					TextUtils.formatFluidStack(state.slagTank.getFluid())
			);
		return List.of();
	}

	public static class SharedTank extends FluidTank
	{
		private FluidTank partner;

		public SharedTank()
		{
			super(TOTAL_TANK_CAPACITY);
		}

		public void setPartner(FluidTank partner)
		{
			this.partner = partner;
		}

		private void refreshCapacity()
		{
			capacity = TOTAL_TANK_CAPACITY-(partner==null?0: partner.getFluidAmount());
		}

		@Override
		public int getCapacity()
		{
			refreshCapacity();
			return capacity;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action)
		{
			refreshCapacity();
			return super.fill(resource, action);
		}
	}

	public enum ChargeStatus
	{
		IDLE, NEEDS_HEAT, NEEDS_FLUX, NEEDS_FUEL, OUTPUT_FULL, READY, HEATING, SMELTING
	}

	public static class State implements IGMultiblockState
	{
		public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
		public final SlotwiseItemHandler inventory;
		private final SharedTank metalTank = new SharedTank();
		private final SharedTank slagTank = new SharedTank();

		private final StoredCapability<IItemHandler> itemInputCap;
		private final StoredCapability<IFluidHandler> metalCap;
		private final StoredCapability<IFluidHandler> slagCap;
		private final CapabilityReference<IFluidHandler> metalOutput;
		private final CapabilityReference<IFluidHandler> slagOutput;

		private boolean active = false;
		private float heat = 0;
		private float chargeHeatTarget = 0;
		private float quality = 0;
		private float heatFloor = 0;
		private float readoutMaterial = 0;
		private float readoutFuel = 0;
		private float readoutFuelNeeded = 0;
		private float readoutFlux = 0;
		private ChargeStatus readoutStatus = ChargeStatus.IDLE;
		private boolean heating = false;
		private int heatingMax = 0;
		private float heatingStart = 0;
		private float readoutFluxNeeded = 0;
		private float readoutCarbonTarget = 0;
		private float readoutHeatCap = 0;
		private int readoutHeatRequired = 0;
		private float heatDrawPerTick = 0;
		private int preheaters = 0;
		private int preheaterStates = 0;
		private int progress = 0;
		private int processMax = 0;
		private FluidStack pendingMetal = FluidStack.EMPTY;
		private FluidStack pendingSlag = FluidStack.EMPTY;

		public State(IInitialMultiblockContext<State> ctx)
		{
			final Runnable markDirty = ctx.getMarkDirtyRunnable();
			metalTank.setPartner(slagTank);
			slagTank.setPartner(metalTank);
			final Supplier<@Nullable Level> levelGetter = ctx.levelSupplier();
			this.inventory = new SlotwiseItemHandler(
					Collections.nCopies(NUM_INPUT_SLOTS, new IOConstraint(true, i -> isChargeComponent(levelGetter.get(), i))), markDirty
			);
			this.itemInputCap = new StoredCapability<>(new WrappingItemHandler(
					inventory, true, false, new IntRange(0, NUM_INPUT_SLOTS)
			));
			this.metalCap = new StoredCapability<>(new ArrayFluidHandler(new IFluidTank[]{metalTank}, true, false, markDirty));
			this.slagCap = new StoredCapability<>(new ArrayFluidHandler(new IFluidTank[]{slagTank}, true, false, markDirty));
			this.metalOutput = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, neighbourOf(METAL_OUTPUT));
			this.slagOutput = ctx.getCapabilityAt(ForgeCapabilities.FLUID_HANDLER, neighbourOf(SLAG_OUTPUT));
		}

		public int tickPreheaters(IMultiblockLevel level)
		{
			int running = 0;
			int packed = 0;
			for(int i = 0; i < PREHEATER_CANDIDATES.length; i++)
			{
				BlastFurnacePreheaterBlockEntity preheater = getPreheater(level, PREHEATER_CANDIDATES[i]);
				int side;
				if(preheater==null) side = PREHEATER_MISSING;
				else if(preheater.doSpeedup() > 0)
				{
					side = PREHEATER_ACTIVE;
					running++;
				}
				else side = PREHEATER_IDLE;
				packed |= side << (i*2);
			}
			preheaterStates = packed;
			return running;
		}

		public @Nullable BlastFurnacePreheaterBlockEntity getPreheater(IMultiblockLevel level, BlockPos[] candidates)
		{
			for(BlockPos offset : candidates)
			{
				BlockEntity be = level.getBlockEntity(offset);
				if(be instanceof BlastFurnacePreheaterBlockEntity heater)
				{
					BlastFurnacePreheaterBlockEntity master = heater.master();
					return master!=null?master: heater;
				}
			}
			return null;
		}

		public FluidTank getMetalTank()
		{
			return metalTank;
		}

		public FluidTank getSlagTank()
		{
			return slagTank;
		}

		public SlotwiseItemHandler getInventory()
		{
			return inventory;
		}

		public boolean isActive()
		{
			return active;
		}

		public float getHeat()
		{
			return heat;
		}

		public float getQuality()
		{
			return quality;
		}

		public float getHeatFloor()
		{
			return heatFloor;
		}

		public float getMaterial()
		{
			return readoutMaterial;
		}

		public float getFuel()
		{
			return readoutFuel;
		}

		public float getFuelNeeded()
		{
			return readoutFuelNeeded;
		}

		public float getFlux()
		{
			return readoutFlux;
		}

		public int getStatus()
		{
			return readoutStatus.ordinal();
		}

		public float getHeatingProgress()
		{
			if(!heating||chargeHeatTarget <= heatingStart) return 0;
			return Mth.clamp((heat-heatingStart)/(chargeHeatTarget-heatingStart), 0, 1);
		}

		public float getFluxNeeded()
		{
			return readoutFluxNeeded;
		}

		public float getCarbonTargetHeat()
		{
			return readoutCarbonTarget;
		}

		public float getHeatCap()
		{
			return readoutHeatCap;
		}

		public int getHeatRequired()
		{
			return readoutHeatRequired;
		}

		public int getPreheaterCount()
		{
			return preheaters;
		}

		public int getPreheaterStates()
		{
			return preheaterStates;
		}

		public float getProgress()
		{
			return processMax > 0?Mth.clamp(progress/(float)processMax, 0, 1): 0;
		}

		@Override
		public void writeSaveNBT(CompoundTag nbt)
		{
			nbt.put("inventory", inventory.serializeNBT());
			nbt.put("metal_tank", metalTank.writeToNBT(new CompoundTag()));
			nbt.put("slag_tank", slagTank.writeToNBT(new CompoundTag()));
			nbt.put("pending_metal", pendingMetal.writeToNBT(new CompoundTag()));
			nbt.put("pending_slag", pendingSlag.writeToNBT(new CompoundTag()));
			nbt.putBoolean("active", active);
			nbt.putFloat("heat", heat);
			nbt.putFloat("charge_heat_target", chargeHeatTarget);
			nbt.putFloat("heat_floor", heatFloor);
			nbt.putFloat("readout_material", readoutMaterial);
			nbt.putFloat("readout_fuel", readoutFuel);
			nbt.putFloat("readout_fuel_needed", readoutFuelNeeded);
			nbt.putFloat("readout_flux", readoutFlux);
			nbt.putInt("readout_status", readoutStatus.ordinal());
			nbt.putBoolean("heating", heating);
			nbt.putInt("heating_max", heatingMax);
			nbt.putFloat("heating_start", heatingStart);
			nbt.putFloat("readout_flux_needed", readoutFluxNeeded);
			nbt.putFloat("readout_carbon_target", readoutCarbonTarget);
			nbt.putFloat("readout_heat_cap", readoutHeatCap);
			nbt.putInt("readout_heat_required", readoutHeatRequired);
			nbt.putFloat("heat_draw", heatDrawPerTick);
			nbt.putInt("preheaters", preheaters);
			nbt.putInt("preheater_states", preheaterStates);
			nbt.putFloat("quality", quality);
			nbt.putInt("progress", progress);
			nbt.putInt("process_max", processMax);
		}

		@Override
		public void readSaveNBT(CompoundTag nbt)
		{
			inventory.deserializeNBT(nbt.getCompound("inventory"));
			metalTank.readFromNBT(nbt.getCompound("metal_tank"));
			slagTank.readFromNBT(nbt.getCompound("slag_tank"));
			pendingMetal = FluidStack.loadFluidStackFromNBT(nbt.getCompound("pending_metal"));
			pendingSlag = FluidStack.loadFluidStackFromNBT(nbt.getCompound("pending_slag"));
			active = nbt.getBoolean("active");
			heat = nbt.getFloat("heat");
			chargeHeatTarget = nbt.getFloat("charge_heat_target");
			heatFloor = nbt.getFloat("heat_floor");
			readoutMaterial = nbt.getFloat("readout_material");
			readoutFuel = nbt.getFloat("readout_fuel");
			readoutFuelNeeded = nbt.getFloat("readout_fuel_needed");
			readoutFlux = nbt.getFloat("readout_flux");
			int statusIndex = nbt.getInt("readout_status");
			ChargeStatus[] statuses = ChargeStatus.values();
			readoutStatus = statusIndex >= 0&&statusIndex < statuses.length?statuses[statusIndex]: ChargeStatus.IDLE;
			heating = nbt.getBoolean("heating");
			heatingMax = nbt.getInt("heating_max");
			heatingStart = nbt.getFloat("heating_start");
			readoutFluxNeeded = nbt.getFloat("readout_flux_needed");
			readoutCarbonTarget = nbt.getFloat("readout_carbon_target");
			readoutHeatCap = nbt.getFloat("readout_heat_cap");
			readoutHeatRequired = nbt.getInt("readout_heat_required");
			heatDrawPerTick = nbt.getFloat("heat_draw");
			preheaters = nbt.getInt("preheaters");
			preheaterStates = nbt.getInt("preheater_states");
			quality = nbt.getFloat("quality");
			progress = nbt.getInt("progress");
			processMax = nbt.getInt("process_max");
		}

		@Override
		public void writeSyncNBT(CompoundTag nbt)
		{
			writeSaveNBT(nbt);
		}

		@Override
		public void readSyncNBT(CompoundTag nbt)
		{
			readSaveNBT(nbt);
		}

		@Override
		public void invalidate(@NotNull IMultiblockContext<?> ctx)
		{
			this.itemInputCap.get(ctx).invalidate();
			this.metalCap.get(ctx).invalidate();
			this.slagCap.get(ctx).invalidate();
		}
	}
}
