package com.igteam.immersive_geology.common.fluid;

import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EmitterParticle;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Supplier;

public class IGFluidBlock extends FlowingFluidBlock implements IGBlockType {
    private final IGFluid igFluid;
    @Nullable
    private Effect effect;
    private int duration;
    private int level;
    private static IGFluid tempFluid;

    private static Supplier<IGFluid> supply(IGFluid fluid){
        tempFluid = fluid;
        return () -> fluid;
    }

    public IGFluidBlock(IGFluid igFluid){
        super(supply(igFluid), Properties.create(Material.WATER));
        this.igFluid = igFluid;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        IGFluid f;
        if(igFluid != null){
            f = igFluid;
        } else {
            f = tempFluid;
        }
        builder.add(f.getStateContainer().getProperties().toArray(new Property[0]));
    }

    @Nonnull
    @Override
    public FluidState getFluidState(BlockState state) {
        FluidState baseState = super.getFluidState(state);
        for(Property<?> prop : igFluid.getStateContainer().getProperties()){
            if(prop != FlowingFluidBlock.LEVEL){
                baseState = withCopiedValue(prop, baseState, state);
            }
        }
        return baseState;
    }

    private <T extends StateHolder<?, T>, S extends Comparable<S>>
    T withCopiedValue(Property<S> prop, T oldState, StateHolder<?, ?> copyFrom)
    {
        return oldState.with(prop, copyFrom.get(prop));
    }

    public void setEffect(@Nonnull Effect effect, int duration, int level)
    {
        this.effect = effect;
        this.duration = duration;
        this.level = level;
    }

    @Override
    public void onEntityCollision(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Entity entityIn)
    {
        super.onEntityCollision(state, worldIn, pos, entityIn);
        if(effect!=null&&entityIn instanceof LivingEntity)
            ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(effect, duration, level));
    }

    @Override
    public Block getSelf() {
        return this;
    }

    @Override
    public String getHolderName() {
        return getRegistryName().getNamespace();
    }

    @Override
    public MaterialUseType getBlockUseType() {
        return MaterialUseType.FLUIDS;
    }

    @Override
    public com.igteam.immersive_geology.api.materials.Material getMaterial(BlockMaterialType type) {
        return null;
    }

    @Override
    public MaterialUseType getDropUseType() {
        return MaterialUseType.FLUIDS;
    }

    @Override
    public float maxDrops() {
        return 0;
    }


    @Override
    public IFormattableTextComponent getTranslatedName() {
        ArrayList<String> localizedNames = new ArrayList<>();
        localizedNames.add(I18n.format("material."+ IGLib.MODID + "." + igFluid.fluidMaterial.getName().toLowerCase()));
        TranslationTextComponent name = new TranslationTextComponent("block."+ IGLib.MODID+"."+ MaterialUseType.BUCKET.getName().toLowerCase(Locale.ENGLISH), localizedNames.toArray(new Object[localizedNames.size()]));
        return name;
    }

    @Override
    public float minDrops() {
        return 0;
    }
}

