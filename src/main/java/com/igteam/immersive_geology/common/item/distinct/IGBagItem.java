package com.igteam.immersive_geology.common.item.distinct;

import com.igteam.immersive_geology.client.menu.helper.IGItemGroup;
import com.igteam.immersive_geology.core.config.IGConfigurationHandler;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.item.IGItemType;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.menu.ItemSubGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collection;

public class IGBagItem extends Item implements IGItemType
{
    protected ItemSubGroup subGroup;
    protected final MaterialPattern pattern;

    public IGBagItem(MaterialPattern pattern)
    {
        super(new Properties().group(IGItemGroup.IGGroup).maxStackSize(1).maxDamage(Math.min(27, Math.max(1, IGConfigurationHandler.Common.ROCK_BAG_STACKS.get()))*64));
        this.subGroup = ItemSubGroup.misc;
        this.pattern = pattern;
        setRegistryName(IGRegistrationHolder.getRegistryKey(this));
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
        return this.pattern.toString();
    }

    @Override
    public BlockPattern getBlockPattern() {
        return null;
    }

    @Override
    public boolean hasCustomItemColours() { return false; }

    @Override
    public int getColourForIGItem(ItemStack stack, int pass) { return 0; }

    @Override
    public boolean isDamageable() { return false; }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) { return false; }
}
